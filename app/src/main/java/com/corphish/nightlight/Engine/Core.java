package com.corphish.nightlight.Engine;

import android.content.Context;
import android.os.AsyncTask;

import com.corphish.nightlight.Data.Constants;
import com.corphish.nightlight.Helpers.PreferenceHelper;
import com.corphish.nightlight.Helpers.RootUtils;

/**
 * Created by Avinaba on 10/4/2017.
 * Basic functions of the app
 */

public class Core {
    /**
     * Enables night light.
     * It enables KCAL, and writes the intensity
     * @param intensity - Intensity of blue light to be filtered out
     */
    private static void enableNightMode(int intensity) {
        RootUtils.writeToFile("1", Constants.KCAL_SWITCH);
        RootUtils.writeToFile("256 256 "+(Constants.MAX_BLUE_LIGHT - intensity),Constants.KCAL_ADJUST);
    }

    /**
     * Disables night light by setting default color values
     * It does not disable KCAL switch though
     */
    private static void disableNightMode() {
        RootUtils.writeToFile("256 256 256", Constants.KCAL_ADJUST);
    }

    /**
     * Driver method to enable/disable night light
     * @param e - A boolean indicating whether night light should be turned on or off
     * @param intensity - Intensity of blue light to be filtered out
     */
    public static void applyNightMode(boolean e, int intensity) {
        if (e) enableNightMode(intensity);
        else disableNightMode();
    }

    /**
     * Driver method to enable/disable night light asynchronously.
     * This is used by QS Tile, AlarmManagers and BroadcastReceivers to do the changes in background
     * @param b - A boolean indicating whether night light should be turned on or off
     * @param i - Intensity of blue light to be filtered out
     */
    public static void applyNightModeAsync(boolean b, int i) {
        new NightModeApplier(b, i).execute();
    }

    /**
     * Gets the current state of night light from SharedPreferences
     * @param context - Ok where was the shrug emoji again?
     * @return - A boolean indicating the current night light state
     */
    public static boolean getNightLightState(Context context) {
        return PreferenceHelper.getMasterSwitchStatus(context);
    }

    /**
     * AsyncTask to enable/disable night light
     */
    private static class NightModeApplier extends AsyncTask<Object, Object, Object> {
        boolean enabled;
        int intensity;

        NightModeApplier(boolean enabled, int intensity) {
            this.enabled = enabled;
            this.intensity = intensity;
        }

        @Override
        protected Object doInBackground(Object... bubbles) {
            applyNightMode(enabled, intensity);
            return null;
        }
    }
}
