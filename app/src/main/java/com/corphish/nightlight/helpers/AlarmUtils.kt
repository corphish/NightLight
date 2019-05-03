package com.corphish.nightlight.helpers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.corphish.nightlight.receivers.DarkStartNLReceiver

import com.corphish.nightlight.receivers.StartNLReceiver
import com.corphish.nightlight.receivers.StopNLReceiver

import java.util.Calendar

/**
 * Created by Avinaba on 10/6/2017.
 * Helper class to deal with alarms
 */

object AlarmUtils {

    private const val REQUEST_CODE_START = 0
    private const val REQUEST_CODE_STOP = 0

    /**
     * Sets the start and end alarms on user specified time
     * @param context Needed by intent, pendingIntent and to get the AlarmManager service
     * @param startTime Starting time for alarm
     * @param endTime Ending time for alarm
     */
    fun setAlarms(context: Context, startTime: String, endTime: String, darkHoursEnabled: Boolean = false, darkStartTime: String? = null, repeating: Boolean) {
        // Set start alarm
        setAlarm(context, startTime, repeating, StartNLReceiver::class.java, REQUEST_CODE_START)

        if (darkHoursEnabled && darkStartTime != null) // Set start alarm
            setAlarm(context, darkStartTime, repeating, DarkStartNLReceiver::class.java, REQUEST_CODE_START)

        // Set end alarm
        setAlarm(context, endTime, repeating, StopNLReceiver::class.java, REQUEST_CODE_STOP)
    }

    /**
     * Sets alarm based on given arguments
     */
    private fun setAlarm(context: Context,
                 time: String,
                 repeating: Boolean,
                 receiverClass: Class<*>,
                 requestCode: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val timeInMillis = TimeUtils.getTimeAsMSecs(time)

        val intent = Intent(context, receiverClass)
        val alarmIntent = PendingIntent.getBroadcast(context, requestCode, intent, 0)

        if (repeating)
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeInMillis, AlarmManager.INTERVAL_DAY, alarmIntent)
        else
            alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, alarmIntent)
    }
}
