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

import com.corphish.nightlight.data.Constants;
import com.corphish.nightlight.engine.Core;
import com.corphish.nightlight.engine.KCALManager;
import com.corphish.nightlight.helpers.PreferenceHelper;
import com.corphish.nightlight.helpers.RootUtils;
import com.corphish.nightlight.helpers.CrashlyticsHelper;

public class StartActivity extends AppCompatActivity {

    /**
     * Declare the shortcut intent strings and id
     */
    private final String SHORTCUT_INTENT_STRING = "android.intent.action.TOGGLE";
    private final String SHORTCUT_ID            = "toggle";


    private final String TASKER_PLUGIN_INTENT   = "com.twofortyfouram.locale.intent.action.EDIT_SETTING";
    private final int TASKER_INTENT_RQC         = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CrashlyticsHelper.start(this);
        setContentView(R.layout.activity_splash);

        if (handleIntent()) finish();
        else if(handleTaskerIntent()) {}
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

    private boolean handleTaskerIntent() {
        if (!TASKER_PLUGIN_INTENT.equals(getIntent().getAction())) return false;
        // Check if master switch is enabled
        // If enabled redirect to ProfilesActivity
        // Otherwise redirect to MainActivity with appropriate intent message
        // MainActivity will then use this message to show a prompt to user to enable night light
        boolean masterSwitchEnabled = PreferenceHelper.getBoolean(this, Constants.PREF_MASTER_SWITCH);
        Intent intent;

        if (masterSwitchEnabled) {
            intent = new Intent(this, ProfilesActivity.class);
            intent.putExtra(Constants.TASKER_ERROR_STATUS, false);
            startActivityForResult(intent, TASKER_INTENT_RQC);
        } else {
            intent = new Intent(this, MainActivity.class);
            intent.putExtra(Constants.TASKER_ERROR_STATUS, true);
            startActivity(intent);
        }

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

        Core.applyNightModeAsync(state, this);
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

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != TASKER_INTENT_RQC) return;
        setResult(RESULT_OK, data);
        finish();
    }
}
