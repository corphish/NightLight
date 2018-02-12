package com.corphish.nightlight.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.corphish.nightlight.data.Constants;
import com.corphish.nightlight.engine.Core;
import com.corphish.nightlight.engine.TwilightManager;
import com.corphish.nightlight.helpers.AlarmUtils;
import com.corphish.nightlight.helpers.PreferenceHelper;
import com.corphish.nightlight.helpers.TimeUtils;

/**
 * Created by Avinaba on 10/5/2017.
 * Broadcast listener for boot completion
 */

public class BootCompleteReceiver extends BroadcastReceiver {
    private static final String BOOT_COMPLETE_ANDROID_STRING = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!intent.getAction().equals(BOOT_COMPLETE_ANDROID_STRING)) return;

        PreferenceHelper.putBoolean(context, Constants.COMPATIBILITY_TEST, false);

        boolean masterSwitch = PreferenceHelper.getBoolean(context, Constants.PREF_MASTER_SWITCH);
        boolean autoSwitch = PreferenceHelper.getBoolean(context, Constants.PREF_AUTO_SWITCH);
        boolean sunSwitch = PreferenceHelper.getBoolean(context, Constants.PREF_SUN_SWITCH);

        String sStartTime = PreferenceHelper.getString(context, Constants.PREF_START_TIME, Constants.DEFAULT_START_TIME);
        String sEndTime = PreferenceHelper.getString(context, Constants.PREF_END_TIME, Constants.DEFAULT_END_TIME);

        if (!masterSwitch) return;

        if (!autoSwitch) {
            Core.applyNightModeAsync(true, context);
            return;
        }

        boolean state = TimeUtils.determineWhetherNLShouldBeOnOrNot(sStartTime, sEndTime);
        Core.applyNightModeAsync(state, context);

        if (!sunSwitch) AlarmUtils.setAlarms(context, sStartTime, sEndTime, true);
        else TwilightManager.newInstance()
                .atLocation(PreferenceHelper.getLocation(context))
                .computeAndSaveTime(context);
    }
}
