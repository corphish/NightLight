package com.corphish.nightlight.helpers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

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
    fun setAlarms(context: Context, startTime: String, endTime: String, repeating: Boolean) {
        // Set start alarm
        setAlarm(context, startTime, repeating, StartNLReceiver::class.java, REQUEST_CODE_START)

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
        var timeInMillis: Long

        val intent = Intent(context, receiverClass)
        val alarmIntent = PendingIntent.getBroadcast(context, requestCode, intent, 0)

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.set(Calendar.HOUR_OF_DAY, TimeUtils.getTimeAsHourAndMinutes(time)[0])
        calendar.set(Calendar.MINUTE, TimeUtils.getTimeAsHourAndMinutes(time)[1])

        timeInMillis = calendar.timeInMillis
        if (TimeUtils.currentTimeAsMinutes > TimeUtils.getTimeInMinutes(time)) timeInMillis += 86400000L

        if (repeating)
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeInMillis, AlarmManager.INTERVAL_DAY, alarmIntent)
        else
            alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, alarmIntent)
    }
}
