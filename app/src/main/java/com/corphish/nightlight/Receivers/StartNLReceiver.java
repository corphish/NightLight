package com.corphish.nightlight.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;

import com.corphish.nightlight.Data.Constants;
import com.corphish.nightlight.Engine.Core;
import com.corphish.nightlight.Helpers.TimeUtils;

import java.util.Calendar;

/**
 * Created by Avinaba on 10/4/2017.
 * Broadcast Receiver to start night light
 */

public class StartNLReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("NL","Starting NL");
        // At first check whether night light should really be turned on or not
        boolean autoSwitchEnabled = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(Constants.PREF_AUTO_SWITCH, false);
        if (!autoSwitchEnabled) return;

        Calendar calendar = Calendar.getInstance();
        int currentTime = TimeUtils.getTimeInMinutes(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));

        int startTime = TimeUtils.getTimeInMinutes(PreferenceManager.getDefaultSharedPreferences(context).getString(Constants.PREF_START_TIME, Constants.DEFAULT_START_TIME));
        int endTime = TimeUtils.getTimeInMinutes(PreferenceManager.getDefaultSharedPreferences(context).getString(Constants.PREF_END_TIME, Constants.DEFAULT_END_TIME));

        int intensity = PreferenceManager.getDefaultSharedPreferences(context).getInt(Constants.PREF_CUSTOM_VAL, Constants.DEFAULT_INTENSITY);

        if (currentTime >= startTime && currentTime <= endTime) Core.applyNightModeAsync(true, intensity);
    }
}
