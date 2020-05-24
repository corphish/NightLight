package com.corphish.nightlight.engine.kcal

import com.corphish.nightlight.helpers.RootUtils
import java.io.File

/*
 * Generic KCAL implementation.
 * Good old KCAL
 */

/*
 * File paths
 */
// File paths
private const val KCAL_SWITCH = "/sys/devices/platform/kcal_ctrl.0/kcal_enable"
private const val KCAL_COLOR = "/sys/devices/platform/kcal_ctrl.0/kcal"
private const val KCAL_SAT = "/sys/devices/platform/kcal_ctrl.0/kcal_sat"

class GenericKCALManager : KCALAbstraction {
    /**
     * A function to determine whether the implementation is supported by device
     */
    override fun isSupported(): Boolean = File(KCAL_SWITCH).exists() || RootUtils.doesFileExist(KCAL_SWITCH)

    /**
     * A function to determine whether KCAL is enabled or not
     */
    override fun isEnabled() : Boolean = RootUtils.readOneLine(KCAL_SWITCH) == "1"

    /**
     * A function to turn on KCAL
     */
    override fun turnOn() {
        RootUtils.writeToFile("1", KCAL_SWITCH)
    }

    /**
     * A function to adjust KCAL colors
     */
    override fun setColors(red: Int, green: Int, blue: Int) : Boolean {
        return RootUtils.writeToFile("$red $green $blue", KCAL_COLOR)
    }

    /**
     * A function get current KCAL color readings
     */
    override fun getColorReadings(): IntArray {
        val reading = RootUtils.readOneLine(KCAL_COLOR)
        val colorReadings = reading.trim().split(" ".toRegex())

        // Error case, return default value instead
        if (colorReadings.size != 3) {
            return intArrayOf(255, 255, 255)
        }

        return intArrayOf(
                colorReadings[0].toInt(),
                colorReadings[1].toInt(),
                colorReadings[2].toInt()
        )
    }

    /**
     * Function to determine whether saturation is supported
     */
    override fun isSaturationSupported() = true

    /**
     * Function to get saturation
     */
    override fun getSaturation(): Int {
        val reading = RootUtils.readOneLine(KCAL_SAT)

        return reading.toInt()
    }

    /**
     * Function to set saturation
     */
    override fun setSaturation(value: Int) {
        RootUtils.writeToFile("$value", KCAL_SAT)
    }

    override fun getImplementationName() = "Generic KCAL"

    override fun getImplementationSwitchPath() = KCAL_SWITCH

    override fun getImplementationFilePaths() = KCAL_COLOR

    override fun getImplementationFormat() = "%d %d %d"

    override fun getSaturationPath() = KCAL_SAT
}