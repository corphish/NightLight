package com.corphish.nightlight.helpers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.corphish.nightlight.receivers.AutomateSignalReceiver

import java.util.Calendar

/**
 * Created by Avinaba on 10/6/2017.
 * Helper class to deal with alarms.
 *
 * As part of 3.0 update in 2020, alarms set will no longer will be
 * repeating, as only one alarm will be set at any given time. When this alarm
 * is fired, it will set subsequent alarms accordingly.
 */

object AlarmUtils {
    // Request code
    private const val REQUEST_CODE = 42

    /**
     * Sets a non-repeating alarm in a given time of day.
     * If the time specified is lesser than current time, alarm will be set for
     * next day.
     *
     * @param context Context.
     * @param time    Absolute time.
     */
    fun setAlarmAbsolute(context: Context, time: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AutomateSignalReceiver::class.java)
        val alarmIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, 0)

        val calendar = Calendar.getInstance()
        val splitTime = TimeUtils.getTimeAsHourAndMinutes(time)
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.set(Calendar.HOUR_OF_DAY, splitTime[0])
        calendar.set(Calendar.MINUTE, splitTime[1])

        var timeInMillis = calendar.timeInMillis
        if (TimeUtils.currentTimeAsMinutes > TimeUtils.getTimeInMinutes(time)) {
            timeInMillis += 86400000L
        }

        alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, alarmIntent)
    }

    /**
     * Sets a non-repeating alarm after given minutes from current time.
     *
     * @param context Context.
     * @param minutes Minutes after which alarm must be set.
     */
    fun setAlarmRelative(context: Context, minutes: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AutomateSignalReceiver::class.java)
        val alarmIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, 0)

        val targetMillis = System.currentTimeMillis() + (minutes * 60L * 1000)

        alarmManager.set(AlarmManager.RTC_WAKEUP, targetMillis, alarmIntent)
    }
}
