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

    private static final int REQUEST_CODE_START     = 0;
    private static final int REQUEST_CODE_STOP      = 0;

    /**
     * Sets the start and end alarms on user specified time
     * @param context - Needed by intent, pendingIntent and to get the AlarmManager service
     * @param startTime - Starting time for alarm
     * @param endTime - Ending time for alarm
     */
    public static void setAlarms(Context context, String startTime, String endTime) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long timeInMillis;

        Intent startIntent = new Intent(context, StartNLReceiver.class);
        PendingIntent startAlarmIntent = PendingIntent.getBroadcast(context, REQUEST_CODE_START, startIntent, 0);

        Intent endIntent = new Intent(context, StopNLReceiver.class);
        PendingIntent endAlarmIntent = PendingIntent.getBroadcast(context, REQUEST_CODE_STOP, endIntent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, TimeUtils.getTimeAsHourAndMinutes(startTime)[0]);
        calendar.set(Calendar.MINUTE, TimeUtils.getTimeAsHourAndMinutes(startTime)[1]);

        timeInMillis = calendar.getTimeInMillis();
        if (TimeUtils.getCurrentTimeAsMinutes() > TimeUtils.getTimeInMinutes(startTime)) timeInMillis += 86400000L;

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeInMillis, AlarmManager.INTERVAL_DAY, startAlarmIntent);

        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, TimeUtils.getTimeAsHourAndMinutes(endTime)[0]);
        calendar.set(Calendar.MINUTE, TimeUtils.getTimeAsHourAndMinutes(endTime)[1]);

        timeInMillis = calendar.getTimeInMillis();
        if (TimeUtils.getCurrentTimeAsMinutes() > TimeUtils.getTimeInMinutes(endTime)) timeInMillis += 86400000L;

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeInMillis, AlarmManager.INTERVAL_DAY, endAlarmIntent);
    }
}
