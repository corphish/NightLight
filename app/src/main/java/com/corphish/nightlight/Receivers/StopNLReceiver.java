package com.corphish.nightlight.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.corphish.nightlight.Data.Constants;
import com.corphish.nightlight.Engine.Core;

/**
 * Created by Avinaba on 10/4/2017.
 * Broadcast receiver to stop night light
 */

public class StopNLReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // At first check whether night light should really be turned off or not
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        boolean masterSwitchEnabled = sharedPreferences.getBoolean(Constants.PREF_MASTER_SWITCH, false);
        boolean autoSwitchEnabled = sharedPreferences.getBoolean(Constants.PREF_AUTO_SWITCH, false);

        // Both of the switches must be on to proceed
        if (!autoSwitchEnabled || !masterSwitchEnabled) return;

        int intensity = PreferenceManager.getDefaultSharedPreferences(context).getInt(Constants.PREF_CUSTOM_VAL, Constants.DEFAULT_INTENSITY);

        Core.applyNightModeAsync(false, intensity);
    }
}
