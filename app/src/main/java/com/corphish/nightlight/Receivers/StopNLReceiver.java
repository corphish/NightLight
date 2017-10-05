package com.corphish.nightlight.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;

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
        boolean autoSwitchEnabled = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(Constants.PREF_AUTO_SWITCH, false);
        if (!autoSwitchEnabled) return;

        Log.d("NL","Stopping NL");

        int intensity = PreferenceManager.getDefaultSharedPreferences(context).getInt(Constants.PREF_CUSTOM_VAL, Constants.DEFAULT_INTENSITY);

        Core.applyNightModeAsync(false, intensity);
    }
}
