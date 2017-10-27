package com.corphish.nightlight.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.corphish.nightlight.Engine.Core;
import com.corphish.nightlight.Engine.TwilightManager;
import com.corphish.nightlight.Helpers.PreferenceHelper;

/**
 * Created by Avinaba on 10/4/2017.
 * Broadcast receiver to stop night light
 */

public class StopNLReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // At first check whether night light should really be turned off or not

        boolean masterSwitchEnabled = PreferenceHelper.getMasterSwitchStatus(context);
        boolean autoSwitchEnabled = PreferenceHelper.getAutoSwitchStatus(context);

        // Both of the switches must be on to proceed
        if (!autoSwitchEnabled || !masterSwitchEnabled) return;

        int blueIntensity = PreferenceHelper.getBlueIntensity(context);
        int greenIntensity = PreferenceHelper.getGreenIntensity(context);

        Core.applyNightModeAsync(false, blueIntensity, greenIntensity);

        // Also if sunset sunrise is used, reset the timing
        if (PreferenceHelper.getSunSwitchStatus(context)) {
            TwilightManager.newInstance()
                    .atLocation(PreferenceHelper.getLocation(context))
                    .computeAndSaveTime(context);
        }
    }
}
