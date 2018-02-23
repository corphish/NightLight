package com.corphish.nightlight.helpers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.corphish.nightlight.receivers.StartNLReceiver;
import com.corphish.nightlight.receivers.StopNLReceiver;

import java.util.Calendar;

/**
 * Created by Avinaba on 10/6/2017.
 * Helper class to deal with alarms
 */

public class AlarmUtils {

    private static final int REQUEST_CODE_START     = 0;
    private static final int REQUEST_CODE_STOP      = 0;
    private static final String TAG                 = "NL_AlarmUtils";

    /**
     * Sets the start and end alarms on user specified time
     * @param context Needed by intent, pendingIntent and to get the AlarmManager service
     * @param startTime Starting time for alarm
     * @param endTime Ending time for alarm
     * @param repeating A boolean indicating whether nature of alarm is repeating or not
     * @param startReceiverClass Receiver class to be invoked when alarm is started
     * @param endReceiverClass Receiver class to be invoked when alarm is end
     */
    public static void setAlarms(@NonNull Context context,
                                 @Nullable String startTime,
                                 @Nullable String endTime,
                                 boolean repeating,
                                 @Nullable Class startReceiverClass,
                                 @Nullable Class endReceiverClass) {
        if (startTime == null || startReceiverClass == null) return;
        Log.d(TAG, "Setting start alarm - " + startTime + ", targetClass - " + startReceiverClass.getSimpleName());
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long timeInMillis;

        Intent startIntent = new Intent(context, startReceiverClass);
        PendingIntent startAlarmIntent = PendingIntent.getBroadcast(context, REQUEST_CODE_START, startIntent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, TimeUtils.getTimeAsHourAndMinutes(startTime)[0]);
        calendar.set(Calendar.MINUTE, TimeUtils.getTimeAsHourAndMinutes(startTime)[1]);

        timeInMillis = calendar.getTimeInMillis();
        if (TimeUtils.getCurrentTimeAsMinutes() > TimeUtils.getTimeInMinutes(startTime)) timeInMillis += 86400000L;

        if (repeating) alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeInMillis, AlarmManager.INTERVAL_DAY, startAlarmIntent);
        else alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis,startAlarmIntent);

        if (endTime == null || endReceiverClass == null) return;
        Log.d(TAG, "Setting start alarm - " + endTime + ", targetClass - " + endReceiverClass.getSimpleName());

        Intent endIntent = new Intent(context, endReceiverClass);
        PendingIntent endAlarmIntent = PendingIntent.getBroadcast(context, REQUEST_CODE_STOP, endIntent, 0);

        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, TimeUtils.getTimeAsHourAndMinutes(endTime)[0]);
        calendar.set(Calendar.MINUTE, TimeUtils.getTimeAsHourAndMinutes(endTime)[1]);

        timeInMillis = calendar.getTimeInMillis();
        if (TimeUtils.getCurrentTimeAsMinutes() > TimeUtils.getTimeInMinutes(endTime)) timeInMillis += 86400000L;

        if (repeating) alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeInMillis, AlarmManager.INTERVAL_DAY, endAlarmIntent);
        else alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis,endAlarmIntent);


    }

    /**
     * Sets the start and end alarms on user specified time
     * @param context Needed by intent, pendingIntent and to get the AlarmManager service
     * @param startTime Starting time for alarm
     * @param endTime Ending time for alarm
     * @param repeating A boolean indicating whether nature of alarm is repeating or not
     */
    public static void setAlarms(Context context, String startTime, String endTime, boolean repeating) {
        setAlarms(context, startTime, endTime, repeating, StartNLReceiver.class, StopNLReceiver.class);
    }
}
