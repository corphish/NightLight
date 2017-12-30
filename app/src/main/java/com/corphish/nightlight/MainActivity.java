package com.corphish.nightlight;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.corphish.nightlight.Data.Constants;
import com.corphish.nightlight.Helpers.PreferenceHelper;
import com.corphish.nightlight.Interfaces.NightLightStateListener;
import com.corphish.nightlight.Services.NightLightAppService;
import com.corphish.nightlight.UI.Fragments.AutoFragment;
import com.corphish.nightlight.UI.Fragments.FilterFragment;
import com.corphish.nightlight.UI.Fragments.ForceSwitchFragment;
import com.corphish.nightlight.UI.Fragments.MasterSwitchFragment;

import java.util.List;

public class MainActivity
        extends AppCompatActivity
        implements MasterSwitchFragment.MasterSwitchClickListener {

    private boolean masterSwitchEnabled;
    private final int containerId = R.id.layout_container;

    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
        viewInit();
        setViews(masterSwitchEnabled);
    }

    private void init() {
        masterSwitchEnabled = PreferenceHelper.getBoolean(this, Constants.PREF_MASTER_SWITCH);
        NightLightAppService.getInstance().setNightLightStateListener(new NightLightStateListener() {
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
        });
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
        setViews(status);
    }

    private void setViews(boolean show) {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        if (show) {
            // Add all others conditionally
            if (isSupported(R.bool.filters_enabled)) fragmentTransaction.add(containerId, new FilterFragment());
            if (isSupported(R.bool.automation_enabled)) fragmentTransaction.add(containerId, new AutoFragment());
            if (isSupported(R.bool.force_switch_enabled)) fragmentTransaction.add(containerId, new ForceSwitchFragment());
        } else {
            List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
            for (Fragment fragment: fragmentList) {
                if (!(fragment instanceof MasterSwitchFragment)) fragmentTransaction.remove(fragment);
            }
        }

        fragmentTransaction.commit();
    }

    private boolean isSupported (int id) {
        return getResources().getBoolean(id);
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

        if(id == R.id.about_menu) showAbout();

        return super.onOptionsItemSelected(item);
    }

    private void showAbout() {
        startActivity(new Intent(this, AboutActivity.class));
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
}
