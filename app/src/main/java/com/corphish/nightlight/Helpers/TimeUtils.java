package com.corphish.nightlight.Helpers;

import java.util.Calendar;

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

    public static int getCurrentTimeAsMinutes() {
        Calendar calendar = Calendar.getInstance();

        return getTimeInMinutes(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
    }

    public static int[] getCurrentTimeAsHourAndMinutes() {
        int currentTime = getCurrentTimeAsMinutes();

        return new int[]{currentTime/60, currentTime%60};
    }
}
