package com.corphish.nightlight.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.corphish.nightlight.data.Constants;
import com.corphish.nightlight.engine.Core;
import com.corphish.nightlight.helpers.AdvancedAutomationUtils;
import com.corphish.nightlight.helpers.PreferenceHelper;

/**
 * Created by Avinaba on 2/21/2018.
 * Broadcast Receiver to start night light advanced automation
 */

public class AdvancedAutomationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("NL_AdvReceiver", "Received!");
        // At first check whether night light should really be turned on or not

        /*boolean masterSwitchEnabled = PreferenceHelper.getBoolean(context, Constants.PREF_MASTER_SWITCH);
        boolean autoSwitchEnabled = PreferenceHelper.getBoolean(context, Constants.PREF_AUTO_SWITCH);

        // Both of the switches must be on to proceed
        if (!autoSwitchEnabled || !masterSwitchEnabled) return;

        Core.applyNightModeAsync(true, context);*/
        AdvancedAutomationUtils.setNextAlarm(context);
    }
}
