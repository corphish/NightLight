package com.corphish.nightlight.engine.models

import android.content.Context
import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.engine.TwilightManager
import com.corphish.nightlight.helpers.PreferenceHelper
import com.corphish.nightlight.helpers.TimeUtils

/**
 * Automation routine data class holds necessary data to define
 * an automation routine.
 * Every fields have default values, but the fields marked optional are the ones
 * where user input is not necessary.
 */
data class AutomationRoutine(
        /**
         * Routine name.
         * This field is optional.
         */
        val name: String = "",

        /**
         * Switch state.
         * This indicates whether the night light should be turned on or off.
         */
        val switchState: Boolean = false,

        /**
         * Start time of the routine.
         */
        val startTime: String = Constants.DEFAULT_START_TIME,

        /**
         * End time of the routine.
         * This is optional if the switchState is off, but user can supply an end time
         * for greater control on routines.
         */
        val endTime: String = Constants.DEFAULT_END_TIME,

        /**
         * FadeBehavior.
         * Will store the fading behavior for this routine.
         * This will also store the RGB from and to values.
         * Although, ideally we should be storing the RGB values here in this class
         * itself, in order to reduce clutter while persisting, we are using the
         * FadeBehavior class to store.
         */
        val fadeBehavior: FadeBehavior = FadeBehavior(),
) {
    // RGB value links
    val rgbFrom: IntArray
        get() = fadeBehavior.fadeFrom

    val rgbTo: IntArray
        get() = fadeBehavior.fadeTo

    /**
     * Method to check if some other routine is overlapping with this
     * routine.
     *
     * @param other Some other [AutomationRoutine]
     * @return True if the other routine overlaps with the current one, false otherwise.
     */
    fun isOverlappingWith(other: AutomationRoutine): Boolean {
        // Collect the timings.
        val startTimeA = TimeUtils.getTimeAsHourAndMinutes(startTime)
        val endTimeA = TimeUtils.getTimeAsHourAndMinutes(endTime)
        val startTimeB = TimeUtils.getTimeAsHourAndMinutes(other.startTime)
        val endTimeB = TimeUtils.getTimeAsHourAndMinutes(other.endTime)

        // Adjust the end times if they are lesser than start times, in such cases
        // we increase the end time by 24 hours to have easier comparison.
        if (endTimeA[0] < startTimeA[0] || (endTimeA[0] == startTimeA[0] && endTimeA[1] < startTimeA[1])) {
            endTimeA[0] += 24
        }

        if (endTimeB[0] < startTimeB[0] || (endTimeB[0] == startTimeB[0] && endTimeB[1] < startTimeB[1])) {
            endTimeB[0] += 24
        }

        return (startTimeA[0] < endTimeB[0] || (startTimeA[0] == endTimeB[0] && startTimeA[1] <= endTimeB[1])) && // startTimeA <= endTimeB
                (endTimeA[0] > startTimeB[0] || (endTimeA[0] == startTimeB[0] && endTimeA[1] >= startTimeB[1])) // endTimeA >= startTimeB
    }

    override fun toString() =
            "$switchState;;$startTime;;$endTime;;$fadeBehavior;;$name"

    companion object {
        /**
         * Parses an [AutomationRoutine] from string.
         *
         * @return [AutomationRoutine].
         */
        fun fromString(str: String?): AutomationRoutine {
            if (str == null) {
                return AutomationRoutine()
            }

            val parts = str.split(";;")

            if (parts.size != 5) {
                return AutomationRoutine()
            }

            return AutomationRoutine(
                    name = parts[4],
                    switchState = parts[0].toBoolean(),
                    startTime = parts[1],
                    endTime = parts[2],
                    fadeBehavior = FadeBehavior.fromString(parts[3])
            )
        }

        /**
         * Since the time set can be sunset or sunrise, and since those times
         * change everyday, we have to resolve the time every time, hence we use identifier
         * to note such selection.
         */
        const val TIME_SUNSET = "__sunset__"
        const val TIME_SUNRISE = "__sunrise__"
        const val TIME_UNSET = "__unset__"

        /**
         * Extension function to resolve sunset/sunrise timings.
         */
        fun String.resolved(context: Context): String {
            // We only resolve strings identified the sunset/sunrise identifiers.
            if (this == TIME_SUNSET || this == TIME_SUNRISE) {
                val times = TwilightManager.newInstance()
                        .atLocation(PreferenceHelper.getLocation(context))
                        .computeAndGet()

                return if (this == TIME_SUNSET) times.first else times.second
            }

            return this
        }

        /**
         * Returns an automation routine of default behavior.
         */
        fun whenOutside(context: Context): AutomationRoutine {
            // Get the switch value
            val switchStatus = PreferenceHelper.getBoolean(context, "pref_routine_disabled_switch", false)

            // Get the selected RGB
            val settingMode = PreferenceHelper.getInt(context, Constants.PREF_SETTING_MODE, Constants.NL_SETTING_MODE_TEMP)
            val settings = if (settingMode == Constants.NL_SETTING_MODE_TEMP) {
                intArrayOf(
                        PreferenceHelper.getInt(context, Constants.PREF_COLOR_TEMP, Constants.DEFAULT_COLOR_TEMP)
                )
            } else {
                intArrayOf(
                        PreferenceHelper.getInt(context, Constants.PREF_RED_COLOR, Constants.DEFAULT_RED_COLOR),
                        PreferenceHelper.getInt(context, Constants.PREF_GREEN_COLOR, Constants.DEFAULT_GREEN_COLOR),
                        PreferenceHelper.getInt(context, Constants.PREF_BLUE_COLOR, Constants.DEFAULT_BLUE_COLOR),
                )
            }

            return AutomationRoutine(
                    switchState = switchStatus,
                    fadeBehavior = FadeBehavior(
                            type = FadeBehavior.FADE_OFF,
                            settingType = settingMode,
                            fadeFrom = settings
                    )
            )
        }
    }
}