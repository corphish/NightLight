package com.corphish.nightlight.extensions

/**
 * Extension function to convert an int (whose unit is presumable in Kelvins) to RGB int array equivalent
 * Based on http://www.tannerhelland.com/4435/convert-temperature-rgb-algorithm-code/
 */
fun Int.fromColorTemperatureToRGBIntArray(): IntArray {
    var colorTemperature = this
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

    return intArrayOf(red.fixColorBounds(), green.fixColorBounds(), blue.fixColorBounds())
}

fun Int.fromColorTemperatureToRGBString(): String {
    val rgb = this.fromColorTemperatureToRGBIntArray()

    return "${rgb[0]} ${rgb[1]} ${rgb[2]}"
}

/**
 * Fixes range of int to [0, 255]
 */
fun Int.fixColorBounds(): Int {
    return when {
        (this < 0) -> 0
        (this > 255) -> 255
        else -> this
    }
}