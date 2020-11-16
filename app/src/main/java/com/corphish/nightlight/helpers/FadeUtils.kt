package com.corphish.nightlight.helpers

import android.content.Context
import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.extensions.fromColorTemperatureToRGBIntArray
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
     * Calculates the scaled down RGB for current time.
     * This scales down from the default KCAL value (256, 256, 256) or the
     * set backup values to the selected color.
     * This method will be called every time when night light
     * would be enabled, so it is very necessary to perform the
     * necessary checks whether the scaled setting must be applied
     * at all or not.
     *
     * @param context Context is needed for getting user preferences.
     * @return Scaled colored temperature. Null is returned when this
     *         setting should not be applied.
     */
    fun getColorRGBForCurrentTime(context: Context): IntArray? {
        // Check if fading can take place or not
        if (!isFadingEnabled(context)) {
            return null
        }

        // Get the from and to colors
        // If KCAL backup is on, we use the backed up values
        val rFrom: Int
        val gFrom: Int
        val bFrom: Int

        if (PreferenceHelper.getBoolean(context, Constants.KCAL_PRESERVE_SWITCH, false)) {
            val values = PreferenceHelper.getString(context, Constants.KCAL_PRESERVE_VAL, Constants.DEFAULT_KCAL_VALUES)!!.split(" ".toRegex())
            rFrom = values[0].toInt()
            gFrom = values[1].toInt()
            bFrom = values[2].toInt()
        } else {
            rFrom = 256
            gFrom = 256
            bFrom = 256
        }

        // To values
        val rTo: Int
        val gTo: Int
        val bTo: Int

        if (PreferenceHelper.getInt(context, Constants.PREF_SETTING_MODE, Constants.NL_SETTING_MODE_TEMP) == Constants.NL_SETTING_MODE_TEMP) {
            val temp = PreferenceHelper.getInt(context, Constants.PREF_COLOR_TEMP, Constants.DEFAULT_COLOR_TEMP).fromColorTemperatureToRGBIntArray()
            rTo = temp[0]
            gTo = temp[1]
            bTo = temp[2]
        } else {
            rTo = PreferenceHelper.getInt(context, Constants.PREF_RED_COLOR, Constants.DEFAULT_RED_COLOR)
            gTo = PreferenceHelper.getInt(context, Constants.PREF_GREEN_COLOR, Constants.DEFAULT_BLUE_COLOR)
            bTo = PreferenceHelper.getInt(context, Constants.PREF_BLUE_COLOR, Constants.DEFAULT_BLUE_COLOR)
        }

        // Get start and end times
        val startTime = PreferenceHelper.getString(context, Constants.PREF_START_TIME, Constants.DEFAULT_START_TIME)
                ?: Constants.DEFAULT_START_TIME
        val endTime = PreferenceHelper.getString(context, Constants.PREF_DARK_HOURS_START, Constants.DEFAULT_START_TIME)
                ?: Constants.DEFAULT_START_TIME

        // Check if current time is in schedule or not.
        // If not then we return max temp.
        if (!TimeUtils.isInRange(startTime, endTime)) {
            return intArrayOf(rFrom, gFrom, bFrom)
        }

        val pollMinutes = PreferenceHelper.getString(context, Constants.PREF_FADE_POLL_RATE_MINS, "5")?.toInt() ?: 5

        // Poll minutes can now be 0 by value, indicating infinite selection.
        // In such cases we return the min temp, as outside the fade schedule, max will be applied.
        if (pollMinutes == 0) {
            return intArrayOf(rTo, gTo, bTo)
        }

        val difference = TimeUtils.getTimeDifference(startTime, endTime)
        val minuteDifference = difference[0] * 60 + difference[1]
        val step = (minuteDifference.toFloat()/pollMinutes).roundToInt()
        val timeStep = (TimeUtils.currentTimeAsMinutes - TimeUtils.getTimeInMinutes(startTime))/pollMinutes

        val rStep = (rTo - rFrom)/step
        val gStep = (gTo - gFrom)/step
        val bStep = (bTo - bFrom)/step

        return intArrayOf(
                rFrom - (rStep * timeStep),
                gFrom - (gStep * timeStep),
                bFrom - (bStep * timeStep),
        )
    }

    /**
     * Method to check if all the necessary settngs are enabled or
     * not so that fade can take place.
     *
     * @param context Context is needed for getting user preferences.
     * @return        Boolean indicating whether fading can take place
     *                or not.
     */
    fun isFadingEnabled(context: Context, fadeSettingOverride: Boolean? = null): Boolean {
        // Get the switch status
        val autoEnabled = PreferenceHelper.getBoolean(context, Constants.PREF_AUTO_SWITCH, false)
        val fadeEnabled = autoEnabled && PreferenceHelper.getBoolean(context, Constants.PREF_DARK_HOURS_ENABLE, false)

        // If fading is not enabled, return null
        if (!fadeEnabled) {
            return false
        }

        // Check if fade setting is enabled
        val fadeSettingEnabled = fadeSettingOverride ?: PreferenceHelper.getBoolean(context, Constants.PREF_FADE_ENABLED, false)
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
        if (!TimeUtils.isInRange(startTime, endTime, currentTime)) {
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