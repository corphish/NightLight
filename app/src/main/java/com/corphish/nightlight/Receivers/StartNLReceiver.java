package com.corphish.nightlight.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.corphish.nightlight.Engine.Core;
import com.corphish.nightlight.Helpers.PreferenceHelper;

/**
 * Created by Avinaba on 10/4/2017.
 * Broadcast Receiver to start night light
 */

public class StartNLReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // At first check whether night light should really be turned on or not

        boolean masterSwitchEnabled = PreferenceHelper.getMasterSwitchStatus(context);
        boolean autoSwitchEnabled = PreferenceHelper.getAutoSwitchStatus(context);

        // Both of the switches must be on to proceed
        if (!autoSwitchEnabled || !masterSwitchEnabled) return;

        int blueIntensity = PreferenceHelper.getBlueIntensity(context);
        int greenIntensity = PreferenceHelper.getGreenIntensity(context);

        Core.applyNightModeAsync(true, blueIntensity, greenIntensity);
    }
}
