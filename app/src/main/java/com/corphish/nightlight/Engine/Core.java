package com.corphish.nightlight.Engine;

import android.content.Context;
import android.os.AsyncTask;

import com.corphish.nightlight.Data.Constants;
import com.corphish.nightlight.Helpers.PreferenceHelper;

/**
 * Created by Avinaba on 10/4/2017.
 * Basic functions of the app
 */

public class Core {
    /**
     * Enables night light.
     * It enables KCAL, and writes the intensity
     * However, here we don't backup current user set KCAL values, otherwise it may backup night light values
     * @param blueIntensity Intensity of blue light to be filtered out.
     * @param greenIntensity Intensity of green light to be filtered out.
     */
    private static void enableNightMode(int blueIntensity, int greenIntensity) {
        KCALManager.enableKCAL();
        KCALManager.updateKCALValues(256, Constants.MAX_GREEN_LIGHT - greenIntensity, Constants.MAX_BLUE_LIGHT - blueIntensity);
    }

    /**
     * Disables night light by setting default color values
     * It does not disable KCAL switch though
     * @param context Context is needed to read Preference values
     */
    private static void disableNightMode(Context context) {
        // First check if KCAL value backup is enabled or not
        boolean kcalPreserved = PreferenceHelper.getBoolean(context, Constants.KCAL_PRESERVE_SWITCH, true);

        // If KCAL was preserved (enabled by default), set preserved values
        // Otherwise set default values
        if (kcalPreserved) KCALManager.updateKCALValues(PreferenceHelper.getString(context, Constants.KCAL_PRESERVE_VAL, Constants.DEFAULT_KCAL_VALUES));
        else KCALManager.updateKCALWithDefaultValues();
    }

    /**
     * Driver method to enable/disable night light
     * @param e A boolean indicating whether night light should be turned on or off
     * @param context Context is needed to read Preference values
     * @param blueIntensity Intensity of blue light to be filtered out.
     * @param greenIntensity Intensity of green light to be filtered out.
     */
    public static void applyNightMode(boolean e, Context context, int blueIntensity, int greenIntensity) {
        if (e) enableNightMode(blueIntensity, greenIntensity);
        else disableNightMode(context);
    }

    /**
     * Driver method to enable/disable night light asynchronously.
     * This is used by QS Tile, AlarmManagers and BroadcastReceivers to do the changes in background
     * @param b A boolean indicating whether night light should be turned on or off
     * @param context Context is needed to read Preference values
     * @param blueIntensity Intensity of blue light to be filtered out
     * @param greenIntensity Intensity of green light to be filtered out.
     */
    public static void applyNightModeAsync(boolean b, Context context, int blueIntensity, int greenIntensity) {
        new NightModeApplier(b, context, blueIntensity, greenIntensity).execute();
    }

    /**
     * Driver method to enable/disable night light asynchronously.
     * @param b A boolean indicating whether night light should be turned on or off
     * @param context A context parameter to read the intensity values from preferences
     */
    public static void applyNightModeAsync(boolean b, Context context) {
        applyNightModeAsync(b,
                context,
                PreferenceHelper.getInt(context, Constants.PREF_BLUE_INTENSITY, Constants.DEFAULT_BLUE_INTENSITY),
                PreferenceHelper.getInt(context, Constants.PREF_GREEN_INTENSITY, Constants.DEFAULT_GREEN_INTENSITY));
    }

    /**
     * AsyncTask to enable/disable night light
     */
    private static class NightModeApplier extends AsyncTask<Object, Object, Object> {
        boolean enabled;
        int blueIntensity, greenIntensity;
        Context context;

        NightModeApplier(boolean enabled, Context context, int blueIntensity, int greenIntensity) {
            this.enabled = enabled;
            this.context = context;
            this.blueIntensity = blueIntensity;
            this.greenIntensity = greenIntensity;
        }

        @Override
        protected Object doInBackground(Object... bubbles) {
            applyNightMode(enabled, context, blueIntensity, greenIntensity);
            return null;
        }
    }
}
