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
     * @param blueIntensity - Intensity of blue light to be filtered out.
     * @param greenIntensity - Intensity of green light to be filtered out.
     */
    private static void enableNightMode(int blueIntensity, int greenIntensity) {
        RootUtils.writeToFile("1", Constants.KCAL_SWITCH);
        RootUtils.writeToFile("256 " + (Constants.MAX_GREEN_LIGHT - greenIntensity) + " " + (Constants.MAX_BLUE_LIGHT - blueIntensity),Constants.KCAL_ADJUST);
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
     * @param blueIntensity - Intensity of blue light to be filtered out.
     * @param greenIntensity - Intensity of green light to be filtered out.
     */
    public static void applyNightMode(boolean e, int blueIntensity, int greenIntensity) {
        if (e) enableNightMode(blueIntensity, greenIntensity);
        else disableNightMode();
    }

    /**
     * Driver method to enable/disable night light asynchronously.
     * This is used by QS Tile, AlarmManagers and BroadcastReceivers to do the changes in background
     * @param b - A boolean indicating whether night light should be turned on or off
     * @param blueIntensity - Intensity of blue light to be filtered out
     * @param greenIntensity - Intensity of green light to be filtered out.
     */
    public static void applyNightModeAsync(boolean b, int blueIntensity, int greenIntensity) {
        new NightModeApplier(b, blueIntensity, greenIntensity).execute();
    }

    /**
     * Driver method to enable/disable night light asynchronously.
     * This should only be used to turn off night mode.
     * @param b - A boolean indicating whether night light should be turned on or off
     */
    public static void applyNightModeAsync(boolean b) {
        applyNightModeAsync(b, Constants.DEFAULT_BLUE_INTENSITY, Constants.DEFAULT_GREEN_INTENSITY);
    }

    /**
     * Driver method to enable/disable night light asynchronously.
     * @param b - A boolean indicating whether night light should be turned on or off
     * @param context - A context parameter to read the intensity values from preferences
     */
    public static void applyNightModeAsync(boolean b, Context context) {
        applyNightModeAsync(b,
                PreferenceHelper.getBlueIntensity(context),
                PreferenceHelper.getGreenIntensity(context));
    }

    /**
     * AsyncTask to enable/disable night light
     */
    private static class NightModeApplier extends AsyncTask<Object, Object, Object> {
        boolean enabled;
        int blueIntensity, greenIntensity;

        NightModeApplier(boolean enabled, int blueIntensity, int greenIntensity) {
            this.enabled = enabled;
            this.blueIntensity = blueIntensity;
            this.greenIntensity = greenIntensity;
        }

        @Override
        protected Object doInBackground(Object... bubbles) {
            applyNightMode(enabled, blueIntensity, greenIntensity);
            return null;
        }
    }
}
