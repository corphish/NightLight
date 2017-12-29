package com.corphish.nightlight;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ShortcutManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;

import com.corphish.nightlight.Data.Constants;
import com.corphish.nightlight.Engine.Core;
import com.corphish.nightlight.Engine.KCALManager;
import com.corphish.nightlight.Helpers.PreferenceHelper;
import com.corphish.nightlight.Helpers.RootUtils;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class StartActivity extends AppCompatActivity {

    /**
     * Declare the shortcut intent strings and id
     */
    private final String SHORTCUT_INTENT_STRING = "android.intent.action.TOGGLE";
    private final String SHORTCUT_ID            = "toggle";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash);

        if (handleIntent()) finish();
        else {
            if (getResources().getBoolean(R.bool.forced_compatibility_test_enabled) ||
                    (!BuildConfig.DEBUG && !PreferenceHelper.getBoolean(this, Constants.COMPATIBILITY_TEST)))
                new CompatibilityChecker().execute();
            else switchToMain();
        }
    }

    /**
     * Handle the incoming intent of shortcut
     * Returns true if shortcut was handled, false otherwise
     */
    private boolean handleIntent() {

        String shortcutID;

        if (getIntent().getAction() != null && getIntent().getAction().equals(SHORTCUT_INTENT_STRING)) {
            shortcutID = SHORTCUT_ID;
            doToggle();
        } else return false;

        /*
         * On Android 7.0 or below, bail out from now
         * This is because app shortcuts are not supported by default in those android versions
         * It however is supported in 3rd party launchers like nova launcher.
         * As android API guidelines suggest to reportShortcutUsed(), but that can be done only on API 25
         */
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1) return true;

        ShortcutManager shortcutManager = this.getSystemService(ShortcutManager.class);
        shortcutManager.reportShortcutUsed(shortcutID);

        return true;
    }

    /**
     * Actual night light toggling happens here
     */
    private void doToggle() {
        boolean state = PreferenceHelper.getToggledBoolean(this, Constants.PREF_FORCE_SWITCH);
        boolean masterSwitch = PreferenceHelper.getBoolean(this, Constants.PREF_MASTER_SWITCH);

        /*
         * If state is on, while masterSwitch is off, turn on masterSwitch as well
         */
        if (state && !masterSwitch)
            PreferenceHelper.putBoolean(this, Constants.PREF_MASTER_SWITCH ,true);

        int blueIntensity = PreferenceHelper.getInt(this, Constants.PREF_BLUE_INTENSITY, Constants.DEFAULT_BLUE_INTENSITY);
        int greenIntensity = PreferenceHelper.getInt(this, Constants.PREF_GREEN_INTENSITY, Constants.DEFAULT_GREEN_INTENSITY);

        Core.applyNightModeAsync(state, blueIntensity, greenIntensity);
    }

    private void showAlertDialog(int caption, int msg) {
        if (isFinishing()) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AppTheme));
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
        View progressBar, alertImage;

        @Override
        protected void onPreExecute() {
            progressBar = findViewById(R.id.progressBar);
            alertImage = findViewById(R.id.alertPlaceholder);
        }

        @Override
        protected String doInBackground(String... booms) {
            rootAccessAvailable = RootUtils.getRootAccess();
            kcalSupported = KCALManager.isKCALAvailable();
            return null;
        }

        @Override
        protected void onPostExecute(String boom) {
            progressBar.setVisibility(View.GONE);
            if (!rootAccessAvailable) {
                showAlertDialog(R.string.no_root_access, R.string.no_root_desc);
                alertImage.setVisibility(View.VISIBLE);
            }
            else if (!kcalSupported) {
                showAlertDialog(R.string.no_kcal, R.string.no_kcal_desc);
                alertImage.setVisibility(View.VISIBLE);
            }
            else {
                PreferenceHelper.putBoolean(getApplicationContext(), Constants.COMPATIBILITY_TEST, true);
                switchToMain();
            }
        }
    }

    private void switchToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
