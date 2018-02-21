package com.corphish.nightlight.helpers;

import android.content.Context;

import com.corphish.nightlight.data.Constants;

/**
 * Created by avinabadalal on 21/02/18.
 * Various helpers related to advanced automation
 */

public class AdvancedAutomationUtils {

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
}
