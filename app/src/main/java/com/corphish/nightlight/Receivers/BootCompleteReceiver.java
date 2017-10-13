package com.corphish.nightlight.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.corphish.nightlight.Engine.Core;
import com.corphish.nightlight.Helpers.AlarmUtils;
import com.corphish.nightlight.Helpers.PreferenceHelper;
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

        boolean masterSwitch = PreferenceHelper.getMasterSwitchStatus(context);
        boolean autoSwitch = PreferenceHelper.getAutoSwitchStatus(context);

        int intensity = PreferenceHelper.getIntensity(context);

        String sStartTime = PreferenceHelper.getStartTime(context);
        String sEndTime = PreferenceHelper.getEndTime(context);

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
