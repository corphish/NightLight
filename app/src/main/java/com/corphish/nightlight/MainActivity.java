package com.corphish.nightlight;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;

import com.corphish.nightlight.Helpers.PreferenceHelper;
import com.corphish.nightlight.Helpers.RootUtils;
import com.corphish.nightlight.UI.Fragments.AboutFragment;
import com.corphish.nightlight.UI.Fragments.AutoFragment;
import com.corphish.nightlight.UI.Fragments.DonateFragment;
import com.corphish.nightlight.UI.Fragments.FilterFragment;
import com.corphish.nightlight.UI.Fragments.ForceSwitchFragment;
import com.corphish.nightlight.UI.Fragments.MasterSwitchFragment;
import com.corphish.nightlight.Data.Constants;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MasterSwitchFragment.MasterSwitchClickListener {

    private boolean masterSwitchEnabled;
    private final int containerId = R.id.layout_container;

    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (!BuildConfig.DEBUG) new CompatibilityChecker().execute();

        init();
        viewInit();
        setViews(masterSwitchEnabled);
    }

    private void init() {
        masterSwitchEnabled = PreferenceHelper.getMasterSwitchStatus(this);
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

            fragmentTransaction.add(containerId, new AboutFragment());

            if (isSupported(R.bool.donation_enabled)) fragmentTransaction.add(containerId, new DonateFragment());
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

    private void showAlertDialog(int caption, int msg) {
        if (isFinishing()) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(caption);
        builder.setMessage(msg);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.show();
    }

    private class CompatibilityChecker extends AsyncTask<String, String, String> {
        boolean rootAccessAvailable = false, kcalSupported = false;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage(getString(R.string.compat_check));
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... booms) {
            rootAccessAvailable = RootUtils.getRootAccess();
            kcalSupported = new File(Constants.KCAL_ADJUST).exists();
            return null;
        }

        @Override
        protected void onPostExecute(String boom) {
            progressDialog.hide();
            if (!rootAccessAvailable) showAlertDialog(R.string.no_root_access, R.string.no_root_desc);
            else if (!kcalSupported) showAlertDialog(R.string.no_kcal, R.string.no_kcal_desc);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment fragment: fragments) fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
