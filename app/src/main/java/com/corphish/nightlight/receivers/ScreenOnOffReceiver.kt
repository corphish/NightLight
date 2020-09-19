package com.corphish.nightlight.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.engine.Core
import com.corphish.nightlight.helpers.PreferenceHelper

/**
 * Screen off receiver.
 * This receiver will be used to detect screen off events.
 */
class ScreenOnOffReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("NL_ScreenOff", "Hit")
        if (context == null || intent == null) {
            return
        }

        // Validate intent
        if (Intent.ACTION_USER_PRESENT != intent.action) {
            return
        }

        // Check whether master switch and the setting is turned or not
        val masterSwitchEnabled = PreferenceHelper.getBoolean(context, Constants.PREF_MASTER_SWITCH)
        val lockSwitchEnabled = PreferenceHelper.getBoolean(context, Constants.PREF_DISABLE_IN_LOCK_SCREEN)

        // Both of the switches must be on to proceed
        if (!lockSwitchEnabled || !masterSwitchEnabled) {
            return
        }

        // Turn off Night Light when screen is off
        Core.applyNightModeAsync(false, context)
    }
}