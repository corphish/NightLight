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

    val REQUEST_CODE_START = 0
    val REQUEST_CODE_STOP = 0

    /**
     * Sets the start and end alarms on user specified time
     * @param context Needed by intent, pendingIntent and to get the AlarmManager service
     * @param startTime Starting time for alarm
     * @param endTime Ending time for alarm
     */
    fun setAlarms(context: Context, startTime: String, endTime: String, repeating: Boolean) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        var timeInMillis: Long

        val startIntent = Intent(context, StartNLReceiver::class.java)
        val startAlarmIntent = PendingIntent.getBroadcast(context, REQUEST_CODE_START, startIntent, 0)

        val endIntent = Intent(context, StopNLReceiver::class.java)
        val endAlarmIntent = PendingIntent.getBroadcast(context, REQUEST_CODE_STOP, endIntent, 0)

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.set(Calendar.HOUR_OF_DAY, TimeUtils.getTimeAsHourAndMinutes(startTime)[0])
        calendar.set(Calendar.MINUTE, TimeUtils.getTimeAsHourAndMinutes(startTime)[1])

        timeInMillis = calendar.timeInMillis
        if (TimeUtils.currentTimeAsMinutes > TimeUtils.getTimeInMinutes(startTime)) timeInMillis += 86400000L

        if (repeating)
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeInMillis, AlarmManager.INTERVAL_DAY, startAlarmIntent)
        else
            alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, startAlarmIntent)

        calendar.timeInMillis = System.currentTimeMillis()
        calendar.set(Calendar.HOUR_OF_DAY, TimeUtils.getTimeAsHourAndMinutes(endTime)[0])
        calendar.set(Calendar.MINUTE, TimeUtils.getTimeAsHourAndMinutes(endTime)[1])

        timeInMillis = calendar.timeInMillis
        if (TimeUtils.currentTimeAsMinutes > TimeUtils.getTimeInMinutes(endTime)) timeInMillis += 86400000L

        if (repeating)
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeInMillis, AlarmManager.INTERVAL_DAY, endAlarmIntent)
        else
            alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, endAlarmIntent)


    }
}
