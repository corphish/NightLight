package com.corphish.nightlight;

import android.content.pm.ShortcutManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.corphish.nightlight.Engine.Core;
import com.corphish.nightlight.Helpers.PreferenceHelper;

/**
 * Created by Avinaba on 10/12/2017.
 * Base application class
 */

public class ShortcutActivity extends AppCompatActivity {

    /**
     * Declare the shortcut intent strings and id
     */
    private final String SHORTCUT_INTENT_STRING = "android.intent.action.TOGGLE";
    private final String SHORTCUT_ID            = "toggle";

    @Override
    public void onCreate(Bundle s) {
        super.onCreate(s);

        handleIntent();

        finish();
    }

    /**
     * Handle the incoming intent of shortcut
     */
    private void handleIntent() {
        // Return if the action is null
        if (getIntent().getAction() == null || getIntent().getAction().isEmpty()) return;

        String shortcutID = null;

        if (getIntent().getAction().equals(SHORTCUT_INTENT_STRING)) {
            shortcutID = SHORTCUT_ID;
            doToggle();
        }

        /*
         * On Android 7.0 or below, bail out from now
         * This is because app shortcuts are not supported by default in those android versions
         * It however is supported in 3rd party launchers like nova launcher.
         * As android API guidelines suggest to reportShortcutUsed(), but that can be done only on API 25
         */
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1) return;

        if (shortcutID != null) {
            ShortcutManager shortcutManager = this.getSystemService(ShortcutManager.class);
            shortcutManager.reportShortcutUsed(shortcutID);
        }
    }

    /**
     * Actual night light toggling happens here
     */
    private void doToggle() {
        boolean state = PreferenceHelper.getToggledMasterSwitchStatus(this);
        int blueIntensity = PreferenceHelper.getBlueIntensity(this);
        int greenIntensity = PreferenceHelper.getGreenIntensity(this);

        Core.applyNightModeAsync(state, blueIntensity, greenIntensity);
    }
}