package com.corphish.nightlight.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.corphish.nightlight.Data.Constants;
import com.corphish.nightlight.Engine.Core;
import com.corphish.nightlight.Engine.TwilightManager;
import com.corphish.nightlight.Helpers.PreferenceHelper;
import com.corphish.nightlight.Services.NightLightAppService;

/**
 * Created by Avinaba on 10/4/2017.
 * Broadcast receiver to stop night light
 */

public class StopNLReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // At first check whether night light should really be turned off or not

        boolean masterSwitchEnabled = PreferenceHelper.getBoolean(context, Constants.PREF_MASTER_SWITCH);
        boolean autoSwitchEnabled = PreferenceHelper.getBoolean(context, Constants.PREF_AUTO_SWITCH);

        // Both of the switches must be on to proceed
        if (!autoSwitchEnabled || !masterSwitchEnabled) return;

        int blueIntensity = PreferenceHelper.getInt(context, Constants.PREF_BLUE_INTENSITY, Constants.DEFAULT_BLUE_INTENSITY);
        int greenIntensity = PreferenceHelper.getInt(context, Constants.PREF_GREEN_INTENSITY, Constants.DEFAULT_GREEN_INTENSITY);

        Core.applyNightModeAsync(false, context, blueIntensity, greenIntensity);

        // Update app UI if its running
        NightLightAppService nightLightAppService = NightLightAppService.getInstance();
        if (nightLightAppService.isAppServiceRunning()) nightLightAppService.notifyUpdatedState(false);

        // Also if sunset sunrise is used, reset the timing
        if (PreferenceHelper.getBoolean(context, Constants.PREF_SUN_SWITCH)) {
            TwilightManager.newInstance()
                    .atLocation(PreferenceHelper.getLocation(context))
                    .computeAndSaveTime(context);
        }
    }
}
