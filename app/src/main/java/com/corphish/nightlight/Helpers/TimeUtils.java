package com.corphish.nightlight.Helpers;

import java.util.Calendar;

/**
 * Created by Avinaba on 10/4/2017.
 * Time related helper functions
 */

public class TimeUtils {
    /**
     * Converts the given hour and minutes into minutes.
     * It makes use of the genius formula => 1 hour = 60 minutes.
     * Thus, after hours of mind boggling calculations and derivations, the calculation becomes => reqMinutes = hour*60 + minutes.
     * Kindergarten stuff eh? :p
     * @param hour - Value of 'hour'.
     * @param minutes - Value of 'minutes'.
     * @return - The resultant value of minutes after conversion
     */
    public static int getTimeInMinutes(int hour, int minutes) {
        return hour*60 + minutes;
    }

    /**
     * Converts time as String in format "HH:MM" into minutes.
     * @param time - Time as string in specified format.
     * @return - Minutes after converting the input string.
     */
    public static int getTimeInMinutes(String time) {
        int hour = Integer.parseInt(time.split(":")[0]);
        int minutes = Integer.parseInt(time.split(":")[1]);

        return getTimeInMinutes(hour, minutes);
    }

    /**
     * Parses the time as String in format "HH:MM" and returns the hour and minutes as int array.
     * @param time - Time as String in above format.
     * @return - An int array containing parsed hour and minute, as [hour, minutes].
     */
    public static int[] getTimeAsHourAndMinutes(String time) {
        int hour = Integer.parseInt(time.split(":")[0]);
        int minutes = Integer.parseInt(time.split(":")[1]);

        return new int[]{hour, minutes};
    }

    /**
     * Returns current time as minutes converted using the above methods.
     * @return - Current time as minutes.
     */
    public static int getCurrentTimeAsMinutes() {
        Calendar calendar = Calendar.getInstance();

        return getTimeInMinutes(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
    }

    /**
     * Returns current time as hours and minutes in an int array.
     * @return - Current time as hour and minutes in an int array, as [hour, minutes].
     */
    public static int[] getCurrentTimeAsHourAndMinutes() {
        int currentTime = getCurrentTimeAsMinutes();

        return new int[]{currentTime/60, currentTime%60};
    }

    /**
     * Determines whether or not Night Light should be on based on given times.
     * Alarm times are unaffected tho, coz android will fire alarms at start and end time every day if enabled
     * We need not worry about that.
     * @param startTime - Start time for automatic scheduling selected by user
     * @param endTime - End time selected by user
     * @return - boolean indicating whether or not night light should be on
     */
    public static boolean determineWhetherNLShouldBeOnOrNot(String startTime, String endTime) {
        int iCurrentTime = getCurrentTimeAsMinutes(), iStartTime = getTimeInMinutes(startTime), iEndTime = getTimeInMinutes(endTime);

        // Borked case: if start and end times are same, return false
        if (iStartTime == iEndTime) return false;

        // Simple case
        if (iStartTime < iEndTime) return iCurrentTime >= iStartTime && iCurrentTime < iEndTime;

        // Complex case: endTime < startTime

        // if currentTime > starTime, it must be lesser than endTime, coz endTime is a time of next day, so return true
        if (iCurrentTime > iStartTime) return true;

        // if currentTime < startTime, it must be false if it is > endTime (for example: startTime - 0400, endTime - 0300, currentTime - 0330)
        // Otherwise return true (for example: startTime - 0400, endTime - 0300, currentTime - 0230)
        return iCurrentTime < iStartTime && iCurrentTime < iEndTime;
    }
}
