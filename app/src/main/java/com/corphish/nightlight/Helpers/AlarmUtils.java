package com.corphish.nightlight.Helpers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.corphish.nightlight.Receivers.StartNLReceiver;
import com.corphish.nightlight.Receivers.StopNLReceiver;

import java.util.Calendar;

/**
 * Created by Avinaba on 10/6/2017.
 * Helper class to deal with alarms
 */

public class AlarmUtils {

    public static void setAlarms(Context context, String startTime, String endTime) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent startIntent = new Intent(context, StartNLReceiver.class);
        PendingIntent startAlarmIntent = PendingIntent.getBroadcast(context, 0, startIntent, 0);

        Intent endIntent = new Intent(context, StopNLReceiver.class);
        PendingIntent endAlarmIntent = PendingIntent.getBroadcast(context, 0, endIntent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, TimeUtils.getTimeAsHourAndMinutes(startTime)[0]);
        calendar.set(Calendar.MINUTE, TimeUtils.getTimeAsHourAndMinutes(startTime)[1]);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, startAlarmIntent);

        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, TimeUtils.getTimeAsHourAndMinutes(endTime)[0]);
        calendar.set(Calendar.MINUTE, TimeUtils.getTimeAsHourAndMinutes(endTime)[1]);

        long timeInMillis = calendar.getTimeInMillis();
        if (TimeUtils.getCurrentTimeAsMinutes() > TimeUtils.getTimeInMinutes(endTime)) timeInMillis += 86400000L;

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeInMillis, AlarmManager.INTERVAL_DAY, endAlarmIntent);
    }
}
