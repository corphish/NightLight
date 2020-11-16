package com.corphish.nightlight.helpers

import java.util.Calendar
import kotlin.math.roundToInt

/**
 * Created by Avinaba on 10/4/2017.
 * Time related helper functions
 */

object TimeUtils {

    /**
     * Returns current time as minutes converted using the above methods.
     * @return Current time as minutes.
     */
    val currentTimeAsMinutes: Int
        get() {
            val calendar = Calendar.getInstance()

            return getTimeInMinutes(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
        }

    /**
     * Returns current time as hours and minutes in an int array.
     * @return Current time as hour and minutes in an int array, as [hour, minutes].
     */
    val currentTimeAsHourAndMinutes: IntArray
        get() {
            val currentTime = currentTimeAsMinutes

            return intArrayOf(currentTime / 60, currentTime % 60)
        }

    /**
     * Converts the given hour and minutes into minutes.
     * It makes use of the genius formula => 1 hour = 60 minutes.
     * Thus, after hours of mind boggling calculations and derivations, the calculation becomes => reqMinutes = hour*60 + minutes.
     * Kindergarten stuff eh? :p
     * @param hour Value of 'hour'.
     * @param minutes Value of 'minutes'.
     * @return The resultant value of minutes after conversion
     */
    private fun getTimeInMinutes(hour: Int, minutes: Int): Int {
        return hour * 60 + minutes
    }

    /**
     * Converts time as String in format "HH:MM" into minutes.
     * @param time Time as string in specified format.
     * @return Minutes after converting the input string.
     */
    fun getTimeInMinutes(time: String): Int {
        val hour = Integer.parseInt(time.split(":".toRegex())[0])
        val minutes = Integer.parseInt(time.split(":".toRegex())[1])

        return getTimeInMinutes(hour, minutes)
    }

    /**
     * Parses the time as String in format "HH:MM" and returns the hour and minutes as int array.
     * @param time Time as String in above format.
     * @return An int array containing parsed hour and minute, as [hour, minutes].
     */
    fun getTimeAsHourAndMinutes(time: String): IntArray {
        val hour = Integer.parseInt(time.split(":".toRegex())[0])
        val minutes = Integer.parseInt(time.split(":".toRegex())[1])

        return intArrayOf(hour, minutes)
    }

    /**
     * Determines whether the target time is within the given start and end times or not.
     *
     * @param startTime Start time.
     * @param endTime End time.
     * @param targetTime Target time to test. Null if current time is to be tested
     * @param startExclusive [Boolean] to indicate whether the start time needs to be considered
     *                       while comparison or not.
     * @param endExclusive [Boolean] to indicate whether the end time needs to be considered
     *                       while comparison or not.
     * @return [Boolean] indicating whether or target time is in range or not.
     */
    fun isInRange(startTime: String,
                  endTime: String,
                  targetTime: String? = null,
                  startExclusive: Boolean = false,
                  endExclusive: Boolean = true): Boolean {
        val iCurrentTime = if (targetTime == null) currentTimeAsMinutes else getTimeInMinutes(targetTime)
        val iStartTime = getTimeInMinutes(startTime)
        val iEndTime = getTimeInMinutes(endTime)

        // Offset.
        val startOffset = if (startExclusive) 1 else 0
        val endOffset = if (endExclusive) 1 else 0

        // Borked case: if start and end times are same, return false
        if (iStartTime == iEndTime) return false

        // Simple case
        if (iStartTime < iEndTime) return iCurrentTime in IntRange(iStartTime + startOffset, iEndTime - endOffset)

        // Complex case: endTime < startTime

        // if currentTime > starTime, it must be lesser than endTime, coz endTime is a time of next day, so return true
        // if currentTime < startTime, it must be false if it is > endTime (for example: startTime - 0400, endTime - 0300, currentTime - 0330)
        // Otherwise return true (for example: startTime - 0400, endTime - 0300, currentTime - 0230)
        return if (iCurrentTime > iStartTime) true else iCurrentTime < iStartTime && iCurrentTime < iEndTime
    }

    /**
     * Returns time remaining in the auto schedule
     */
    fun getRemainingTimeInSchedule(endTime: String):Int {
        val curTime = currentTimeAsMinutes
        var endTimeMinutes = getTimeInMinutes(endTime)

        // Means endTime is next day and curTime is today
        if (endTimeMinutes < curTime) endTimeMinutes += 24 * 60

        return ((endTimeMinutes - curTime).toDouble() / 60).roundToInt()
    }

    /**
     * Returns time remaining to the auto schedule
     */
    fun getRemainingTimeToSchedule(startTime: String):Int {
        val curTime = currentTimeAsMinutes
        var startTimeMinutes = getTimeInMinutes(startTime)

        // Means endTime is next day and curTime is today
        if (startTimeMinutes < curTime) startTimeMinutes += 24 * 60

        return ((startTimeMinutes - curTime).toDouble() / 60).roundToInt()
    }

    /**
     * Returns the time difference of 2 given times.
     * If the end time is, by value, lesser than start time, it
     * is assumed that the end time falls on next day.
     *
     * @param startTime Start time.
     * @param endTime   End time.
     * @return          Time difference in form of hour and minutes as array.
     */
    fun getTimeDifference(startTime: String, endTime: String): IntArray {
        val start = getTimeAsHourAndMinutes(startTime)
        val end = getTimeAsHourAndMinutes(endTime)

        // Check if end time is lesser than start or not,
        // if so, assume it in next day.
        if (end[0] < start[0] || (end[0] == start[0] && end[1] < start[1])) {
            // We break the process into 2 parts.
            // We first find diff of start time and day end.
            // Then we find diff between day start and end time.
            // We finally sum up the differences and return.
            val res1 = getTimeDifference(startTime, "23:59")
            val res2 = getTimeDifference("00:00", endTime)

            res1[0] += res2[0]
            res1[1] += res2[1]

            return res1
        }

        // Calculate diff
        var hourDiff = end[0] - start[0]
        var minDiff = end[1] - start[1]

        // If minute difference is negative, that is
        // minute value of end time less than start time,
        // adjust accordingly
        if (minDiff < 0) {
            minDiff += 60
            hourDiff--
        }

        return intArrayOf(hourDiff, minDiff)
    }
}
