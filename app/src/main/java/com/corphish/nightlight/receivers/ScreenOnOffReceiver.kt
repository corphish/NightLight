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
        if (context == null || intent == null) {
            return
        }

        // Validate intent
        if (Intent.ACTION_USER_PRESENT != intent.action && Intent.ACTION_SCREEN_OFF != intent.action) {
            return
        }

        // Check whether master switch and the setting is turned or not
        val masterSwitchEnabled = PreferenceHelper.getBoolean(context, Constants.PREF_MASTER_SWITCH)
        val lockSwitchEnabled = PreferenceHelper.getBoolean(context, Constants.PREF_DISABLE_IN_LOCK_SCREEN)

        // Both of the switches must be on to proceed
        if (!lockSwitchEnabled || !masterSwitchEnabled) {
            return
        }

        if (Intent.ACTION_SCREEN_OFF == intent.action) {
            Log.d("NL_ScreenOnOff", "Screen off")
            // Turn off Night Light when screen is off if it is on
            if (PreferenceHelper.getBoolean(context, Constants.PREF_FORCE_SWITCH, false)) {
                Core.applyNightModeAsync(false, context)

                // Also notify that it was turned off as part of the event.
                // This is done so that there is a way to know that the
                // night light must not be turned on when it is set off
                // by the user.
                PreferenceHelper.putBoolean(context, Constants.PREF_DISABLED_BY_LOCK_SCREEN, true)
            }
        } else {
            Log.d("NL_ScreenOnOff", "Screen unlocked")

            // Only turn on when it was turned off by lock screen setting.
            if (PreferenceHelper.getBoolean(context, Constants.PREF_DISABLED_BY_LOCK_SCREEN, false)) {
                Core.applyNightModeAsync(true, context)
                PreferenceHelper.putBoolean(context, Constants.PREF_DISABLED_BY_LOCK_SCREEN, false)
            }
        }
    }
}