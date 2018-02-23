package com.corphish.nightlight.helpers;

import android.content.Context;
import android.util.Log;

import com.corphish.nightlight.data.Constants;
import com.corphish.nightlight.engine.Core;
import com.corphish.nightlight.receivers.AdvancedAutomationReceiver;

import java.util.Arrays;

/**
 * Created by avinabadalal on 21/02/18.
 * Various helpers related to advanced automation
 */

public class AdvancedAutomationUtils {

    /**
     * Time period constants
     */
    private static final int TIME_PERIOD_SCALE_DOWN    = -1;
    private static final int TIME_PERIOD_PEAK          = 0;
    private static final int TIME_PERIOD_SCALE_UP      = 1;
    private static final int TIME_PERIOD_INVALID       = Integer.MAX_VALUE;

    /**
     * Calculates the change value
     * This is the change that must be made in temperature for gradual scaling down/up
     * @param temperatureDiff Temperature difference between max and min temperature
     * @param startTime Start time
     * @param endTime End time
     * @return Change value
     */
    private static int getChangeValue(int temperatureDiff,
                                      String startTime,
                                      String endTime) {
        // Get hours and minutes of given time
        int startTimeParts[] = TimeUtils.getTimeAsHourAndMinutes(startTime);
        int endTimeParts[] = TimeUtils.getTimeAsHourAndMinutes(endTime);

        // Hour difference between to times
        // End times which maybe in next day are also considered
        int hourDiff = (endTimeParts[0] - startTimeParts[0] + 24) % 24;

        /*
         * We will disregard the minute value of start time, and will only consider the minute value of end time
         * For example - If start and end times are 17.39 and 01:30, alarms will be set as follows:
         * 18.30, 19.30, 20.30, 21.30, 22.30, 23.30, 00.30, 01.30 (8 alarms)
         * hourDiff = (1 - 17 + 24) % 24 = 8 (hooray)
         * But when times are say 17.39 and 01:50, then we would want to set alarms as follows:
         * 17.50, 18.50, ..., 00.50, 01.50 (9 alarms)
         * So we need to increment hourDiff
         */
        if (endTimeParts[1] > startTimeParts[1]) hourDiff++;

        return hourDiff == 0 ? temperatureDiff : temperatureDiff/hourDiff;
    }

    /**
     * Calculates and sets scale down change value
     * @param context Needed for shared preferences
     */
    public static void calculateAndSetScaleDownChangeValue(Context context) {
        int changeVal = getChangeValue(PreferenceHelper.getInt(context, Constants.PREF_ADV_AUTO_MAX_TEMP, Constants.DEFAULT_MAX_TEMP) - PreferenceHelper.getInt(context, Constants.PREF_ADV_AUTO_MIN_TEMP, Constants.DEFAULT_MIN_TEMP),
                PreferenceHelper.getString(context, Constants.PREF_ADV_AUTO_SCALE_DOWN_START, Constants.DEFAULT_START_TIME),
                PreferenceHelper.getString(context, Constants.PREF_ADV_AUTO_SCALE_DOWN_END, Constants.DEFAULT_END_TIME));

        PreferenceHelper.putInt(context, Constants.PREF_ADV_AUTO_SCALE_DOWN_VAL, changeVal);
    }

    /**
     * Calculates and sets scale up change value
     * @param context Needed for shared preferences
     */
    public static void calculateAndSetScaleUpChangeValue(Context context) {
        int changeVal = getChangeValue(PreferenceHelper.getInt(context, Constants.PREF_ADV_AUTO_MAX_TEMP, Constants.DEFAULT_MAX_TEMP) - PreferenceHelper.getInt(context, Constants.PREF_ADV_AUTO_MIN_TEMP, Constants.DEFAULT_MIN_TEMP),
                PreferenceHelper.getString(context, Constants.PREF_ADV_AUTO_SCALE_UP_START, Constants.DEFAULT_START_TIME),
                PreferenceHelper.getString(context, Constants.PREF_ADV_AUTO_SCALE_UP_START, Constants.DEFAULT_END_TIME));

        PreferenceHelper.putInt(context, Constants.PREF_ADV_AUTO_SCALE_UP_VAL, changeVal);
    }

    public static void setNextAlarm(Context context) {
        Log.d("NL_Adv", "set next alarm");
        String currentTime = TimeUtils.getCurrentTimeAsString();

        String scaleDownStartTime = PreferenceHelper.getString(context, Constants.PREF_ADV_AUTO_SCALE_DOWN_START, Constants.DEFAULT_START_TIME);
        String scaleDownEndTime = PreferenceHelper.getString(context, Constants.PREF_ADV_AUTO_SCALE_DOWN_END, Constants.DEFAULT_END_TIME);
        String peakStartTime = PreferenceHelper.getString(context, Constants.PREF_ADV_AUTO_PEAK_START, Constants.DEFAULT_PEAK_START_TIME);
        String peakEndTime = PreferenceHelper.getString(context, Constants.PREF_ADV_AUTO_PEAK_END, Constants.DEFAULT_PEAK_END_TIME);
        String scaleUpStartTime = PreferenceHelper.getString(context, Constants.PREF_ADV_AUTO_SCALE_UP_START, Constants.DEFAULT_START_TIME);
        String scaleUpEndTime = PreferenceHelper.getString(context, Constants.PREF_ADV_AUTO_SCALE_UP_END, Constants.DEFAULT_END_TIME);

        // Determine current time period
        int currentTimePeriod = TIME_PERIOD_INVALID;

        if (TimeUtils.isWithinTimeRange(currentTime, scaleDownStartTime, scaleDownEndTime)) currentTimePeriod = TIME_PERIOD_SCALE_DOWN;
        else if (TimeUtils.isWithinTimeRange(currentTime, peakStartTime, peakEndTime)) currentTimePeriod = TIME_PERIOD_PEAK;
        else if (TimeUtils.isWithinTimeRange(currentTime, scaleUpStartTime, scaleUpEndTime)) currentTimePeriod = TIME_PERIOD_SCALE_UP;

        // Return if in invalid time period
        if (currentTimePeriod == TIME_PERIOD_INVALID) return;

        int currentTemp = PreferenceHelper.getInt(context, Constants.PREF_COLOR_TEMP, Constants.DEFAULT_COLOR_TEMP);

        if (currentTimePeriod == TIME_PERIOD_SCALE_DOWN) {
            Log.d("NL_Adv", "scale down");
            // Check if first alarm was set
            int scaleDownEndTimeParts[] = TimeUtils.getTimeAsHourAndMinutes(scaleDownEndTime);
            int scaleDownStartTimeParts[] = TimeUtils.getTimeAsHourAndMinutes(scaleDownStartTime);

            String firstAlarmTime = String.format("%02d:%02d", scaleDownEndTimeParts[1] < scaleDownStartTimeParts[1] ?
                    scaleDownStartTimeParts[0] + 1 : scaleDownStartTimeParts[0], scaleDownEndTimeParts[1]);

            if (TimeUtils.isWithinTimeRange(currentTime, scaleDownStartTime, firstAlarmTime)) {
                // First alarm not set
                // Set it
                Log.d("NL_Adv", "first alarm " + firstAlarmTime);
                AlarmUtils.setAlarms(context, firstAlarmTime, null, false, AdvancedAutomationReceiver.class, null);
                PreferenceHelper.putString(context, Constants.PREF_ADV_AUTO_LAST_ALARM, firstAlarmTime);

                currentTemp = PreferenceHelper.getInt(context, Constants.PREF_ADV_AUTO_MAX_TEMP, Constants.DEFAULT_MAX_TEMP);
                PreferenceHelper.putInt(context, Constants.PREF_COLOR_TEMP, currentTemp);

                Core.applyNightModeAsync(true, context);
            } else {
                // Set the next alarm if within range
                int lastAlarmParts[] = TimeUtils.getTimeAsHourAndMinutes(PreferenceHelper.getString(context, Constants.PREF_ADV_AUTO_LAST_ALARM, Constants.DEFAULT_START_TIME));
                Log.d("NL_Adv", Arrays.toString(lastAlarmParts));
                String nextAlarmTime = String.format("%02d:%02d", lastAlarmParts[0], lastAlarmParts[1]);
                if (!TimeUtils.isWithinTimeRange(nextAlarmTime, scaleDownStartTime, scaleDownEndTime)) {
                    // This will happen if nextAlarmTime hits peak period
                    // So set it at start of scale up period
                    nextAlarmTime = scaleUpStartTime;
                }
                Log.d("NL_Adv", "not first alarm " + nextAlarmTime);
                AlarmUtils.setAlarms(context, nextAlarmTime, null, false, AdvancedAutomationReceiver.class, null);
                PreferenceHelper.putString(context, Constants.PREF_ADV_AUTO_LAST_ALARM, nextAlarmTime);

                currentTemp -= PreferenceHelper.getInt(context, Constants.PREF_ADV_AUTO_SCALE_DOWN_VAL, 0);
                PreferenceHelper.putInt(context, Constants.PREF_COLOR_TEMP, currentTemp);
                Core.applyNightModeAsync(true, context);
            }
        } else if (currentTimePeriod == TIME_PERIOD_PEAK) {
            // Set peak period
            Log.d("NL_Adv", "peak");
            AlarmUtils.setAlarms(context, scaleUpStartTime, null, false, AdvancedAutomationReceiver.class, null);
            PreferenceHelper.putString(context, Constants.PREF_ADV_AUTO_LAST_ALARM, scaleUpStartTime);

            currentTemp = PreferenceHelper.getInt(context, Constants.PREF_ADV_AUTO_MIN_TEMP, Constants.DEFAULT_MIN_TEMP);
            PreferenceHelper.putInt(context, Constants.PREF_COLOR_TEMP, currentTemp);
            Core.applyNightModeAsync(true, context);
        } else {
            Log.d("NL_Adv", "scale up");
            // TODO
        }
    }
}
