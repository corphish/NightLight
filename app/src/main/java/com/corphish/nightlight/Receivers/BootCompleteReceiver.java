package com.corphish.nightlight.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.corphish.nightlight.Data.Constants;
import com.corphish.nightlight.Engine.Core;
import com.corphish.nightlight.Helpers.AlarmUtils;
import com.corphish.nightlight.Helpers.TimeUtils;

/**
 * Created by Avinaba on 10/5/2017.
 * Broadcast listener for boot completion
 */

public class BootCompleteReceiver extends BroadcastReceiver {
    private static final String BOOT_COMPLETE_ANDROID_STRING = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!intent.getAction().equals(BOOT_COMPLETE_ANDROID_STRING)) return;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        boolean masterSwitch = sharedPreferences.getBoolean(Constants.PREF_MASTER_SWITCH, false);
        boolean autoSwitch = sharedPreferences.getBoolean(Constants.PREF_AUTO_SWITCH, false);

        int intensity = sharedPreferences.getInt(Constants.PREF_CUSTOM_VAL, Constants.DEFAULT_INTENSITY);

        String sStartTime = sharedPreferences.getString(Constants.PREF_START_TIME, Constants.DEFAULT_START_TIME);
        String sEndTime = sharedPreferences.getString(Constants.PREF_END_TIME, Constants.DEFAULT_END_TIME);

        int currentTime = TimeUtils.getCurrentTimeAsMinutes();
        int startTime = TimeUtils.getTimeInMinutes(sStartTime);
        int endTime = TimeUtils.getTimeInMinutes(sEndTime);

        if (!masterSwitch) return;

        if (!autoSwitch) {
            Core.applyNightModeAsync(true, intensity);
            return;
        }

        if (currentTime >= startTime && currentTime <= endTime) Core.applyNightModeAsync(true, intensity);

        AlarmUtils.setAlarms(context, sStartTime, sEndTime);
    }
}
