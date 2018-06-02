package com.corphish.nightlight.helpers

/**
 * Created by avinabadalal on 12/02/18.
 * Color Temperature Util helper class to convert temperature to RGB
 * Based on http://www.tannerhelland.com/4435/convert-temperature-rgb-algorithm-code/
 */

object ColorTemperatureUtil {

    /**
     * Converts color temperature to RGB
     * @param colorTemp Color Temperature in kelvin
     * @return RGB color int
     */
    fun colorTemperatureToIntRGB(colorTemp: Int): IntArray {
        var colorTemperature = colorTemp
        // Max permissible range of temperature (1000-10000K)
        if (colorTemperature < 1000 || colorTemperature > 10000) return intArrayOf(255, 255, 255)

        val red: Int
        val green: Int
        val blue: Int

        // We are not interested in last 2 digits
        colorTemperature /= 100

        if (colorTemperature < 67) {
            red = 255
            green = (99.4708025861f * Math.log(colorTemperature.toDouble()) - 161.1195681661f).toInt()

            blue = if (colorTemperature < 20) 0 else (138.5177312231 * Math.log((colorTemperature - 10).toDouble()) - 305.0447927307f).toInt()
        } else {
            red = (329.698727446f * Math.pow((colorTemperature - 60).toDouble(), -0.1332047592)).toInt()
            green = (288.1221695283f * Math.pow((colorTemperature - 60).toDouble(), -0.0755148492)).toInt()
            blue = 255
        }

        return intArrayOf(fixColorBounds(red), fixColorBounds(green), fixColorBounds(blue))
    }

    private fun fixColorBounds(color: Int): Int {
        return if (color < 0) 0 else if (color > 255) 255 else color
    }
}
