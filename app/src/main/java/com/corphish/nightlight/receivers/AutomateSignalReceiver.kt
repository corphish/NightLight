package com.corphish.nightlight.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.engine.Core
import com.corphish.nightlight.engine.TwilightManager
import com.corphish.nightlight.helpers.AlarmUtils
import com.corphish.nightlight.helpers.PreferenceHelper
import com.corphish.nightlight.helpers.TimeUtils

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
        val fadeEnabled = PreferenceHelper.getBoolean(context, Constants.PREF_DARK_HOURS_ENABLE)
        var toTurnOn: Boolean

        // Both of the switches must be on to proceed
        if (!autoSwitchEnabled || !masterSwitchEnabled) {
            return
        }
        val startTime = PreferenceHelper.getString(context, Constants.PREF_START_TIME, Constants.DEFAULT_START_TIME)
                ?: Constants.DEFAULT_START_TIME
        val endTime = PreferenceHelper.getString(context, Constants.PREF_END_TIME, Constants.DEFAULT_END_TIME)
                ?: Constants.DEFAULT_END_TIME
        val insideSchedule = TimeUtils.determineWhetherNLShouldBeOnOrNot(startTime, endTime)

        // If not inside schedule, we need to set a start alarm at start time.
        // Also, if sunset/sunrise is enabled, we have to calculate and set accordingly.
        if (!insideSchedule) {
            if (PreferenceHelper.getBoolean(context, Constants.PREF_SUN_SWITCH)) {
                TwilightManager.newInstance()
                        .atLocation(PreferenceHelper.getLocation(context))
                        .computeAndSaveTime(context!!) { sunsetTime, _ ->
                            // Set alarm for the new start time
                            AlarmUtils.setAlarmAbsolute(context, sunsetTime)
                        }
            } else {
                // Set alarm for the selected start time
                AlarmUtils.setAlarmAbsolute(context!!, startTime)
            }

            // Since we are going to turn off nigh light outside schedule
            toTurnOn = false
        } else {
            // If fade is enabled, we set relative alarms
            if (fadeEnabled) {
                // Check if we are in fade schedule
                val fadeEndTime = PreferenceHelper.getString(context, Constants.PREF_DARK_HOURS_START, Constants.DEFAULT_END_TIME)
                        ?: Constants.DEFAULT_END_TIME
                val insideFadeSchedule = TimeUtils.determineWhetherNLShouldBeOnOrNot(startTime, fadeEndTime)
                if (insideFadeSchedule) {
                    // Set relative alarms
                    val pollRate = PreferenceHelper.getString(context, Constants.PREF_FADE_POLL_RATE_MINS, "5")?.toInt() ?: 5

                    // If infinite poll is selected, then it will behave same as what
                    // dark hours start would behave. In this case we set an absolute alarm.
                    if (pollRate == 0) {
                        AlarmUtils.setAlarmAbsolute(context!!, endTime)
                    } else {
                        AlarmUtils.setAlarmRelative(context!!, pollRate)
                    }
                } else {
                    // We set alarm for end time
                    AlarmUtils.setAlarmAbsolute(context!!, endTime)
                }
            } else {
                // If fading is not enabled, we set alarm for end
                AlarmUtils.setAlarmAbsolute(context!!, endTime)
            }

            toTurnOn = true
        }

        // Finally we turn on/off night light
        Core.applyNightModeAsync(toTurnOn, context)
    }
}