package com.corphish.nightlight.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.engine.AutomationRoutineManager
import com.corphish.nightlight.engine.Core
import com.corphish.nightlight.engine.models.AutomationRoutine
import com.corphish.nightlight.engine.models.AutomationRoutine.Companion.resolved
import com.corphish.nightlight.engine.models.FadeBehavior
import com.corphish.nightlight.helpers.AlarmUtils
import com.corphish.nightlight.helpers.FadeUtils
import com.corphish.nightlight.helpers.PreferenceHelper

/**
 * Earlier we would have 3 separate receivers, one to turn on the night light,
 * one to enable the dark hours and other to turn off.
 * With this approach, we will only have one receiver, and this receiver will
 * automatically determine what to do on a given time, and then set alarm for the next
 * hit. With this we will be able to all the functions along with fading.
 */
class AutomateSignalReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val masterSwitchEnabled = PreferenceHelper.getBoolean(context, Constants.PREF_MASTER_SWITCH)
        val autoSwitchEnabled = PreferenceHelper.getBoolean(context, Constants.PREF_AUTO_SWITCH)

        // Poll rate
        val pollRate = PreferenceHelper.getString(context, Constants.PREF_FADE_POLL_RATE_MINS, "5")?.toInt() ?: 5

        // Both of the switches must be on to proceed
        if (!autoSwitchEnabled || !masterSwitchEnabled) {
            return
        }

        // We only work with routines here.
        // Load the routines
        AutomationRoutineManager.loadRoutines(context!!)

        // We get the current routine
        val currentRoutine = AutomationRoutineManager.getCurrentRoutine(context)

        // If we are in a routine period, current routine will not be null.
        if (currentRoutine != null) {
            // Apply the current routine.
            val state = currentRoutine.switchState

            if (!state) {
                Core.applyNightModeAsync(state, context)

                // Schedule the alarm for next routine if current routine does not have
                // end time set.
                if (currentRoutine.endTime != AutomationRoutine.TIME_UNSET) {
                    AlarmUtils.setAlarmAbsolute(context, currentRoutine.endTime.resolved(context))
                } else {
                    val nextRoutine = AutomationRoutineManager.getUpcomingRoutine(context)
                    AlarmUtils.setAlarmAbsolute(context, nextRoutine.startTime.resolved(context))
                }
            } else {
                // Else we apply the faded RGB
                val rgb = FadeUtils.getFadedRGB(context, currentRoutine)
                Core.applyNightModeAsync(state, context, rgb[0], rgb[1], rgb[2])

                // Depending on whether fading is on or off, we set alarms accordingly.
                if (currentRoutine.fadeBehavior.type == FadeBehavior.FADE_OFF) {
                    // We set alarm for end.
                    AlarmUtils.setAlarmAbsolute(context, currentRoutine.endTime.resolved(context))
                } else {
                    // We set relative alarm for fade polling.
                    AlarmUtils.setAlarmRelative(context, pollRate)
                }
            }
        } else {
            // We are not in routine, we apply default behavior and set alarm for
            // upcoming routine.
            // TODO: Apply default behavior

            // Schedule the alarm for next routine
            val nextRoutine = AutomationRoutineManager.getUpcomingRoutine(context)
            AlarmUtils.setAlarmAbsolute(context, nextRoutine.startTime.resolved(context))
        }
    }
}