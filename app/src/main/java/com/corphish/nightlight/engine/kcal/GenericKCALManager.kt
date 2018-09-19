package com.corphish.nightlight.engine.kcal

import com.corphish.nightlight.helpers.RootUtils
import java.io.File

/*
 * Generic KCAL implementation.
 * Good old KCAL
 */
class GenericKCALManager : KCALAbstraction {

    // File paths
    private val KCAL_SWITCH = "/sys/devices/platform/kcal_ctrl.0/kcal_enable"
    private val KCAL_COLOR = "/sys/devices/platform/kcal_ctrl.0/kcal"

    /**
     * A function to determine whether the implementation is supported by device
     */
    override fun isSupported(): Boolean = File(KCAL_SWITCH).exists() || RootUtils.doesFileExist(KCAL_SWITCH)

    /**
     * A function to turn on KCAL
     */
    override fun turnOn() {
        RootUtils.writeToFile("1", KCAL_SWITCH)
    }

    /**
     * A function to adjust KCAL colors
     */
    override fun setColors(red: Int, green: Int, blue: Int) {
        RootUtils.writeToFile("$red $green $blue", KCAL_COLOR)
    }

    /**
     * A function get current KCAL color readings
     */
    override fun getColorReadings(): IntArray {
        val reading = RootUtils.readOneLine(KCAL_COLOR)
        val colorReadings = reading.split(" ".toRegex())

        return intArrayOf(
                colorReadings[0].toInt(),
                colorReadings[1].toInt(),
                colorReadings[2].toInt()
        )
    }
}