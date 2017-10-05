package com.corphish.nightlight.Engine;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.corphish.nightlight.Data.Constants;
import com.corphish.nightlight.Helpers.RootUtils;
import com.corphish.nightlight.Helpers.TimeUtils;

/**
 * Created by Avinaba on 10/4/2017.
 * Basic functions of the app
 */

public class Core {
    private static void enableNightMode(int intensity) {
        RootUtils.writeToFile("1", Constants.KCAL_SWITCH);
        RootUtils.writeToFile("256 256 "+(Constants.MAX_BLUE_LIGHT - intensity),Constants.KCAL_ADJUST);
    }

    private static void disableNightMode() {
        RootUtils.writeToFile("256 256 256", Constants.KCAL_ADJUST);
    }

    public static void applyNightMode(boolean e, int intensity) {
        if (e) enableNightMode(intensity);
        else disableNightMode();
    }

    public static void applyNightModeAsync(boolean b, int i) {
        new NightModeApplier(b, i).execute();
    }

    public static boolean getNightLightState(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean masterSwitch = sharedPreferences.getBoolean(Constants.PREF_MASTER_SWITCH, false);

        // Return false if masterSwitch is off
        if (!masterSwitch) return false;

        boolean autoSwitch = sharedPreferences.getBoolean(Constants.PREF_AUTO_SWITCH, false);

        // Return true if autoSwitch is off, because masterSwitch is on already.
        if (!autoSwitch) return true;

        // At this point of time, both masterSwitch and autoSwitch is on
        // Check if the current time lies between the time range
        // Return true if in range, otherwise false
        int currentTime = TimeUtils.getCurrentTimeAsMinutes();

        int startTime = TimeUtils.getTimeInMinutes(sharedPreferences.getString(Constants.PREF_START_TIME, Constants.DEFAULT_START_TIME));
        int endTime = TimeUtils.getTimeInMinutes(sharedPreferences.getString(Constants.PREF_END_TIME, Constants.DEFAULT_END_TIME));

        return currentTime >= startTime && currentTime <= endTime;
    }

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
