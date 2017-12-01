package com.corphish.nightlight.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.corphish.nightlight.Data.Constants;
import com.corphish.nightlight.Engine.Core;
import com.corphish.nightlight.Engine.TwilightManager;
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

        PreferenceHelper.putCompatibilityStatusTest(context, false);

        boolean masterSwitch = PreferenceHelper.getMasterSwitchStatus(context);
        boolean autoSwitch = PreferenceHelper.getAutoSwitchStatus(context);
        boolean sunSwitch = PreferenceHelper.getSunSwitchStatus(context);

        int blueIntensity = PreferenceHelper.getBlueIntensity(context);
        int greenIntensity = PreferenceHelper.getGreenIntensity(context);

        String sStartTime = PreferenceHelper.getTime(context, Constants.PREF_START_TIME);
        String sEndTime = PreferenceHelper.getTime(context, Constants.PREF_END_TIME);

        if (!masterSwitch) return;

        if (!autoSwitch) {
            Core.applyNightModeAsync(true, blueIntensity, greenIntensity);
            return;
        }

        Core.applyNightModeAsync(TimeUtils.determineWhetherNLShouldBeOnOrNot(sStartTime, sEndTime), blueIntensity, greenIntensity);

        if (!sunSwitch) AlarmUtils.setAlarms(context, sStartTime, sEndTime, true);
        else TwilightManager.newInstance()
                .atLocation(PreferenceHelper.getLocation(context))
                .computeAndSaveTime(context);
    }
}
