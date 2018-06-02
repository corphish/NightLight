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

class StartNLReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // At first check whether night light should really be turned on or not

        val masterSwitchEnabled = PreferenceHelper.getBoolean(context, Constants.PREF_MASTER_SWITCH)
        val autoSwitchEnabled = PreferenceHelper.getBoolean(context, Constants.PREF_AUTO_SWITCH)

        // Both of the switches must be on to proceed
        if (!autoSwitchEnabled || !masterSwitchEnabled) return

        Core.applyNightModeAsync(true, context)
    }
}
