package com.corphish.nightlight.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.engine.Core
import com.corphish.nightlight.helpers.PreferenceHelper

/**
 * Created by Avinaba on 10/4/2017.
 * Broadcast Receiver to start night light
 */

class DarkStartNLReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // At first check whether night light should really be turned on or not

        val masterSwitchEnabled = PreferenceHelper.getBoolean(context, Constants.PREF_MASTER_SWITCH)
        val autoSwitchEnabled = PreferenceHelper.getBoolean(context, Constants.PREF_AUTO_SWITCH)
        val darkHoursEnabled = PreferenceHelper.getBoolean(context, Constants.PREF_DARK_HOURS_ENABLE)

        if (!darkHoursEnabled) return

        PreferenceHelper.putInt(context, Constants.PREF_INTENSITY_TYPE,Constants.INTENSITY_TYPE_MAXIMUM)

        // Both of the switches must be on to proceed
        if (!autoSwitchEnabled || !masterSwitchEnabled) return

        Core.applyNightModeAsync(true, context, intensityType =  Constants.INTENSITY_TYPE_MAXIMUM)
    }
}
