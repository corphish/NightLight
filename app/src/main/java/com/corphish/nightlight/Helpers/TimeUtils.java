package com.corphish.nightlight.Helpers;

/**
 * Created by Avinaba on 10/4/2017.
 * Time related helper functions
 */

public class TimeUtils {
    public static int getTimeInMinutes(int hour, int minutes) {
        return hour*60 + minutes;
    }

    public static int getTimeInMinutes(String time) {
        int hour = Integer.parseInt(time.split(":")[0]);
        int minutes = Integer.parseInt(time.split(":")[1]);

        return getTimeInMinutes(hour, minutes);
    }

    public static int[] getTimeAsHourAndMinutes(String time) {
        int hour = Integer.parseInt(time.split(":")[0]);
        int minutes = Integer.parseInt(time.split(":")[1]);

        return new int[]{hour, minutes};
    }
}
