package com.corphish.nightlight.helpers

import android.content.Context
import com.corphish.nightlight.data.Constants
import kotlin.math.roundToInt

/**
 * Utility class to calculate fade temperature.
 * Fading works only with color temperature setting.
 * Fading starts from auto start time and ends at the
 * time set by the user, which would be the dark start
 * time previously.
 */
object FadeUtils {
    /**
     * Calculates the color temperature for current time.
     * This method will be called every time when night light
     * would be enabled, so it is very necessary to perform the
     * necessary checks whether the scaled setting must be applied
     * at all or not.
     *
     * @param context Context is needed for getting user preferences.
     * @return Scaled colored temperature. Null is returned when this
     *         setting should not be applied.
     */
    fun getColorTemperatureForCurrentTime(context: Context): Int? {
        // Check if fading can take place or not
        if (!isFadingEnabled(context)) {
            return null
        }

        // Get the temperatures
        var minTemp = PreferenceHelper.getInt(context, Constants.PREF_MIN_COLOR_TEMP, Constants.DEFAULT_MIN_COLOR_TEMP)
        var maxTemp = PreferenceHelper.getInt(context, Constants.PREF_MAX_COLOR_TEMP, Constants.DEFAULT_MAX_COLOR_TEMP)

        // Usually, by value, max temperature is lower than min temperature.
        // But in case if it is not, we have to swap.
        if (maxTemp > minTemp) {
            minTemp += maxTemp
            maxTemp = minTemp - maxTemp
            minTemp -= maxTemp
        }

        // Get start and end times
        val startTime = PreferenceHelper.getString(context, Constants.PREF_START_TIME, Constants.DEFAULT_START_TIME)
                ?: Constants.DEFAULT_START_TIME
        val endTime = PreferenceHelper.getString(context, Constants.PREF_DARK_HOURS_START, Constants.DEFAULT_START_TIME)
                ?: Constants.DEFAULT_START_TIME

        // Check if current time is in schedule or not.
        // If not then we return max temp.
        if (!TimeUtils.determineWhetherNLShouldBeOnOrNot(startTime, endTime)) {
            return maxTemp
        }

        val pollMinutes = PreferenceHelper.getString(context, Constants.PREF_FADE_POLL_RATE_MINS, "5")?.toInt() ?: 5

        // Poll minutes can now be 0 by value, indicating infinite selection.
        // In such cases we return the min temp, as outside the fade schedule, max will be applied.
        if (pollMinutes == 0) {
            return minTemp
        }

        val difference = TimeUtils.getTimeDifference(startTime, endTime)
        val minuteDifference = difference[0] * 60 + difference[1]
        val step = (minuteDifference.toFloat()/pollMinutes).roundToInt()
        val tempStep = (minTemp - maxTemp)/step
        val timeStep = (TimeUtils.currentTimeAsMinutes - TimeUtils.getTimeInMinutes(startTime))/pollMinutes

        return minTemp - (tempStep * timeStep)
    }

    /**
     * Method to check if all the necessary settngs are enabled or
     * not so that fade can take place.
     *
     * @param context Context is needed for getting user preferences.
     * @return        Boolean indicating whether fading can take place
     *                or not.
     */
    fun isFadingEnabled(context: Context): Boolean {
        // Get the switch status
        val autoEnabled = PreferenceHelper.getBoolean(context, Constants.PREF_AUTO_SWITCH, false)
        val fadeEnabled = autoEnabled && PreferenceHelper.getBoolean(context, Constants.PREF_DARK_HOURS_ENABLE, false)

        // If fading is not enabled, return null
        if (!fadeEnabled) {
            return false
        }

        // Check if fade setting is enabled
        val fadeSettingEnabled = PreferenceHelper.getBoolean(context, Constants.PREF_FADE_ENABLED, false)
        if (!fadeSettingEnabled) {
            return false
        }

        // Get min and max temperatures
        val settingType = PreferenceHelper.getInt(context, Constants.PREF_SETTING_MODE, Constants.NL_SETTING_MODE_TEMP)

        // If temperature is not used, bail out
        if (settingType != Constants.NL_SETTING_MODE_TEMP) {
            return false
        }

        return true
    }

    /**
     * Method to test fade logic.
     *
     * @param currentTime Test time
     * @param minTemp (Optional)
     * @param maxTemp (Optional)
     * @param startTime (Optional)
     * @param endTime (Optional)
     * @param pollMinutes (Optional)
     * @return Temperature
     */
    fun fadeTest(
            currentTime: String,
            minTemp: Int = 4000,
            maxTemp: Int = 3000,
            startTime: String = "18:00",
            endTime: String = "00:00",
            pollMinutes: Int = 5
    ): Int? {
        // Check if current time is in schedule or not
        if (!TimeUtils.determineWhetherNLShouldBeOnOrNot(startTime, endTime, currentTime)) {
            return null
        }

        val difference = TimeUtils.getTimeDifference(startTime, endTime)
        val minuteDifference = difference[0] * 60 + difference[1]
        val step = (minuteDifference.toFloat()/pollMinutes).toInt()
        val tempStep = (minTemp - maxTemp)/step
        val timeStep = (TimeUtils.getTimeInMinutes(currentTime) - TimeUtils.getTimeInMinutes(startTime))/pollMinutes

        return minTemp - (tempStep * timeStep)
    }
}