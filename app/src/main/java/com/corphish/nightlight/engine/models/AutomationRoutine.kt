package com.corphish.nightlight.engine.models

import android.content.Context
import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.engine.TwilightManager
import com.corphish.nightlight.helpers.PreferenceHelper

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

        /**
         * Extension function to resolve sunset/sunrise timings.
         */
        fun String.resolved(context: Context): String {
            // We only resolve strings identified the sunset/sunrise identifiers.
            if (this == TIME_SUNSET || this == TIME_SUNRISE) {
                /*TwilightManager.newInstance()
                        .atLocation(PreferenceHelper.getLocation(context))
                        .computeAndSaveTime(context) { sunset, sunrise ->
                            return when(this) {
                                TIME_SUNRISE -> sunrise
                                TIME_SUNSET -> sunset
                                else -> this
                            }
                        }*/
                // TODO
            }

            return this
        }
    }
}