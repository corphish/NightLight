package com.corphish.nightlight.engine;

import android.content.Context;
import android.os.AsyncTask;

import com.corphish.nightlight.data.Constants;
import com.corphish.nightlight.helpers.PreferenceHelper;
import com.corphish.nightlight.services.NightLightAppService;

/**
 * Created by Avinaba on 10/4/2017.
 * Basic functions of the app
 */

public class Core {
    /**
     * Enables night light.
     * It enables KCAL, and writes the intensity
     * Conditionally backup KCAL values if FORCE_SWITCH is off before turning it on
     * Also enable force switch when Night Light is enabled
     * @param blueIntensity Intensity of blue light to be filtered out.
     * @param greenIntensity Intensity of green light to be filtered out.
     */
    private static void enableNightMode(Context context, int blueIntensity, int greenIntensity) {
        KCALManager.enableKCAL();

        if (PreferenceHelper.getBoolean(context, Constants.KCAL_PRESERVE_SWITCH, true)) {
            // Check if FORCE_SWITCH is off or not
            // If off then only backup
            if (!PreferenceHelper.getBoolean(context, Constants.PREF_FORCE_SWITCH))
                KCALManager.backupCurrentKCALValues(context);
        }

        KCALManager.updateKCALValues(256, Constants.MAX_GREEN_LIGHT - greenIntensity, Constants.MAX_BLUE_LIGHT - blueIntensity);

        PreferenceHelper.putBoolean(context, Constants.PREF_FORCE_SWITCH, true);
    }

    /**
     * Disables night light by setting default color values
     * It does not disable KCAL switch though
     * But it does disable force switch
     * Set the user preserved values for KCAL only if it was enabled and only if <strong>FORCE_SWITCH was on.</strong>
     * @param context Context is needed to read Preference values
     */
    private static void disableNightMode(Context context) {
        // First check if KCAL value backup is enabled or not
        boolean kcalPreserved = PreferenceHelper.getBoolean(context, Constants.KCAL_PRESERVE_SWITCH, true);

        // If KCAL was preserved (enabled by default), set preserved values
        // Otherwise set default values
        if (PreferenceHelper.getBoolean(context, Constants.PREF_FORCE_SWITCH)) {
            if (kcalPreserved)
                KCALManager.updateKCALValues(PreferenceHelper.getString(context, Constants.KCAL_PRESERVE_VAL, Constants.DEFAULT_KCAL_VALUES));
            else KCALManager.updateKCALWithDefaultValues();
        }

        PreferenceHelper.putBoolean(context, Constants.PREF_FORCE_SWITCH, false);
    }

    /**
     * Driver method to enable/disable night light
     * @param e A boolean indicating whether night light should be turned on or off
     * @param context Context is needed to read Preference values
     * @param blueIntensity Intensity of blue light to be filtered out.
     * @param greenIntensity Intensity of green light to be filtered out.
     */
    public static void applyNightMode(boolean e, Context context, int blueIntensity, int greenIntensity) {
        if (e) enableNightMode(context, blueIntensity, greenIntensity);
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
        new NightModeApplier(b, context, blueIntensity, greenIntensity, true).execute();
    }

    /**
     * Driver method to enable/disable night light asynchronously.
     * This is used by QS Tile, AlarmManagers and BroadcastReceivers to do the changes in background
     * @param b A boolean indicating whether night light should be turned on or off
     * @param context Context is needed to read Preference values
     * @param blueIntensity Intensity of blue light to be filtered out
     * @param greenIntensity Intensity of green light to be filtered out.
     * @param toUpdateGlobalState Boolean indicating whether or not global state should be updated
     */
    public static void applyNightModeAsync(boolean b, Context context, int blueIntensity, int greenIntensity, boolean toUpdateGlobalState) {
        new NightModeApplier(b, context, blueIntensity, greenIntensity, toUpdateGlobalState).execute();
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
                PreferenceHelper.getInt(context, Constants.PREF_GREEN_INTENSITY, Constants.DEFAULT_GREEN_INTENSITY),
                true);
    }

    /**
     * Driver method to enable/disable night light asynchronously.
     * @param b A boolean indicating whether night light should be turned on or off
     * @param context A context parameter to read the intensity values from preferences
     * @param toUpdateGlobalState Boolean indicating whether or not global state should be updated
     */
    public static void applyNightModeAsync(boolean b, Context context, boolean toUpdateGlobalState) {
        applyNightModeAsync(b,
                context,
                PreferenceHelper.getInt(context, Constants.PREF_BLUE_INTENSITY, Constants.DEFAULT_BLUE_INTENSITY),
                PreferenceHelper.getInt(context, Constants.PREF_GREEN_INTENSITY, Constants.DEFAULT_GREEN_INTENSITY),
                toUpdateGlobalState);
    }

    /**
     * AsyncTask to enable/disable night light
     */
    private static class NightModeApplier extends AsyncTask<Object, Object, Object> {
        boolean enabled, toUpdateGlobalState;
        int blueIntensity, greenIntensity;
        Context context;

        NightModeApplier(boolean enabled, Context context, int blueIntensity, int greenIntensity, boolean toUpdateGlobalState) {
            this.enabled = enabled;
            this.context = context;
            this.blueIntensity = blueIntensity;
            this.greenIntensity = greenIntensity;
            this.toUpdateGlobalState = toUpdateGlobalState;
        }

        @Override
        protected Object doInBackground(Object... bubbles) {
            applyNightMode(enabled, context, blueIntensity, greenIntensity);
            return null;
        }

        @Override
        protected void onPostExecute(Object bubble) {
            if (NightLightAppService.getInstance().isAppServiceRunning() && toUpdateGlobalState)
                NightLightAppService.getInstance().notifyUpdatedState(enabled);
        }
    }
}
