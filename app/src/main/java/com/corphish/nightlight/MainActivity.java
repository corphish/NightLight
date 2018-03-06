package com.corphish.nightlight;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.corphish.nightlight.data.Constants;
import com.corphish.nightlight.design.alert.BottomSheetAlertDialog;
import com.corphish.nightlight.design.fragments.ColorTemperatureFragment;
import com.corphish.nightlight.design.fragments.SetOnBootDelayFragment;
import com.corphish.nightlight.engine.Core;
import com.corphish.nightlight.helpers.PreferenceHelper;
import com.corphish.nightlight.helpers.StringUtils;
import com.corphish.nightlight.interfaces.NightLightSettingModeListener;
import com.corphish.nightlight.interfaces.NightLightStateListener;
import com.corphish.nightlight.services.NightLightAppService;
import com.corphish.nightlight.design.fragments.AutoFragment;
import com.corphish.nightlight.design.fragments.FilterFragment;
import com.corphish.nightlight.design.fragments.ForceSwitchFragment;
import com.corphish.nightlight.design.fragments.MasterSwitchFragment;

import java.util.List;

public class MainActivity
        extends AppCompatActivity
        implements MasterSwitchFragment.MasterSwitchClickListener, NightLightStateListener, NightLightSettingModeListener {

    private boolean masterSwitchEnabled;
    private final int containerId = R.id.layout_container;

    FragmentTransaction fragmentTransaction;

    private boolean taskerError;
    private final int REQ_CODE  =   100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NightLightAppService.getInstance()
                .registerNightLightStateListener(this)
                .registerNightLightSettingModeChangeListener(this) // TODO: Define night light setting mode change listener
                .startService();

        if (savedInstanceState == null) {
            init();
            viewInit();
            setViews(masterSwitchEnabled);
        }

        getSupportFragmentManager().executePendingTransactions();
        NightLightAppService.getInstance()
                .notifyInitDone();

        applyProfileIfNecessary();

        handleIntent();
    }

    private void init() {
        masterSwitchEnabled = PreferenceHelper.getBoolean(this, Constants.PREF_MASTER_SWITCH);
    }

    private void viewInit() {
        // Clear container
        LinearLayout container = findViewById(containerId);
        container.removeAllViews();

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        fragmentTransaction.add(containerId, new MasterSwitchFragment()).commit();
    }

    @Override
    public void onSwitchClicked(boolean status) {
        if (taskerError && status) {
            taskerError = false;
            Intent intent = new Intent(this, ProfilesActivity.class);
            intent.putExtra(Constants.TASKER_ERROR_STATUS, false);
            startActivityForResult(intent, REQ_CODE);
        }
        setViews(status);
    }

    @Override
    public void onStateChanged(boolean newState) {
        // Sync the force switch in ForceSwitch fragment
        for (Fragment fragment: getSupportFragmentManager().getFragments()) {
            if (fragment instanceof ForceSwitchFragment) {
                ((ForceSwitchFragment) fragment).updateSwitch(newState);
                break;
            }
        }
    }

    @Override
    public void onModeChanged(int newMode) {
        for (Fragment fragment: getSupportFragmentManager().getFragments()) {
            if (fragment instanceof FilterFragment) ((FilterFragment) fragment).onStateChanged(newMode);
            if (fragment instanceof ColorTemperatureFragment) ((ColorTemperatureFragment) fragment).onStateChanged(newMode);
        }
    }

    private void setViews(boolean show) {
        NightLightAppService.getInstance()
                .resetViewCount();

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        if (show) {
            // Add all others conditionally
            if (isSupported(R.bool.filters_enabled)) fragmentTransaction.add(containerId, new FilterFragment());
            if (isSupported(R.bool.color_temperature_enabled)) fragmentTransaction.add(containerId, new ColorTemperatureFragment());
            if (isSupported(R.bool.set_on_boot_delay_enabled) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) fragmentTransaction.add(containerId, new SetOnBootDelayFragment());
            if (isSupported(R.bool.automation_enabled)) fragmentTransaction.add(containerId, new AutoFragment());
            if (isSupported(R.bool.force_switch_enabled)) fragmentTransaction.add(containerId, new ForceSwitchFragment());
        } else {
            List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
            for (Fragment fragment: fragmentList) {
                if (!(fragment instanceof MasterSwitchFragment)) fragmentTransaction.remove(fragment);
            }
        }

        fragmentTransaction.commit();

        NightLightAppService.getInstance().notifyNewSettingMode(PreferenceHelper.getInt(this, Constants.PREF_SETTING_MODE, Constants.NL_SETTING_MODE_FILTER));
    }

    private boolean isSupported (int id) {
        return BuildConfig.DEBUG || getResources().getBoolean(id);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.about_menu) showAbout();
        if (id == R.id.about_profiles) showProfiles();

        return super.onOptionsItemSelected(item);
    }

    private void showAbout() {
        startActivity(new Intent(this, AboutActivity.class));
    }

    private void showProfiles() {
        startActivity(new Intent(this, ProfilesActivity.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment fragment: fragments) {
            if (fragment instanceof AutoFragment)
                fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NightLightAppService.getInstance().destroy();
    }

    /**
     * Checks if the last user setting was a profile or not
     * If it is, then it applies it
     */
    private void applyProfileIfNecessary() {
        int lastApplyType = PreferenceHelper.getInt(this, Constants.PREF_CUR_APPLY_TYPE, Constants.APPLY_TYPE_NON_PROFILE);
        if (lastApplyType == Constants.APPLY_TYPE_PROFILE) {
            Log.d("NL_Main", "Apply profile");
            Core.applyNightModeAsync(
                    PreferenceHelper.getBoolean(this, Constants.PREF_CUR_APPLY_EN, false),
                    this,
                    PreferenceHelper.getInt(this, Constants.PREF_CUR_PROF_MODE, Constants.NL_SETTING_MODE_FILTER),
                    StringUtils.stringToIntArray(PreferenceHelper.getString(this, Constants.PREF_CUR_PROF_VAL, null))
            );
        }
    }

    private void handleIntent() {
        if (getIntent().getBooleanExtra(Constants.TASKER_ERROR_STATUS, false)) {
            taskerError = true;
            showTaskerErrorMessage();
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQ_CODE) return;
        setResult(RESULT_OK, data);
        finish();
    }

    private void showTaskerErrorMessage() {
        BottomSheetAlertDialog bottomSheetAlertDialog = new BottomSheetAlertDialog(this);
        bottomSheetAlertDialog.setTitle(R.string.tasker_error_title);
        bottomSheetAlertDialog.setMessage(R.string.tasker_error_desc);
        bottomSheetAlertDialog.setPositiveButton(android.R.string.ok, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        bottomSheetAlertDialog.show();
    }
}
