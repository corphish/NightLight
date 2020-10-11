package com.corphish.nightlight.engine.models

import android.content.Context
import androidx.annotation.IntRange
import com.corphish.nightlight.engine.Core
import com.corphish.nightlight.extensions.fromColorTemperatureToRGBIntArray

/**
 * Data class to hold RGB values of KCAL.
 * Also provides some useful helpers and loaders.
 */
data class RGB(
        /**
         * Red value of the color.
         */
        @IntRange(from = 0, to = 256) val redValue: Int = 256,

        /**
         * Green value of the color.
         */
        @IntRange(from = 0, to = 256) val greenValue: Int = 256,

        /**
         * Blue value of the color.
         */
        @IntRange(from = 0, to = 256) val blueValue: Int = 256,
) {
    /**
     * Applies this RGB value as KCAL.
     *
     * @param context Context
     */
    fun apply(context: Context) {
        Core.applyNightModeAsync(true, context, redValue, greenValue, blueValue)
    }

    companion object {
        /**
         * Builds an RGB object from a given temperature value.
         *
         * @param temperature Temperature value.
         * @return RGB.
         */
        fun fromTemperature(temperature: Int): RGB {
            val rgbArray = temperature.fromColorTemperatureToRGBIntArray()

            return RGB(
                    redValue = rgbArray[0],
                    greenValue = rgbArray[1],
                    blueValue = rgbArray[2]
            )
        }
    }
}