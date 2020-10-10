package com.corphish.nightlight.engine.kcal

import com.corphish.nightlight.helpers.RootUtils
import java.io.File

/*
 * KCAL implementation for newer kernels (starting with v4.4)\
 * This implementation was first seen in kernels in devices with SDM845, hence the name.
 */

// File paths
private const val KCAL_RED = "/sys/module/msm_drm/parameters/kcal_red"
private const val KCAL_GREEN = "/sys/module/msm_drm/parameters/kcal_green"
private const val KCAL_BLUE = "/sys/module/msm_drm/parameters/kcal_blue"
private const val KCAL_SAT = "/sys/module/msm_drm/parameters/kcal_sat"

class SDM845KCALManager : KCALAbstraction {
    /**
     * A function to determine whether the implementation is supported by device
     */
    override fun isSupported(): Boolean =
            (File(KCAL_RED).exists() || RootUtils.doesFileExist(KCAL_RED)) && (File(KCAL_GREEN).exists() || RootUtils.doesFileExist(KCAL_GREEN)) && (File(KCAL_BLUE).exists() || RootUtils.doesFileExist(KCAL_BLUE))

    /**
     * A function to determine whether KCAL is enabled or not
     */
    override fun isEnabled() : Boolean = true

    /**
     * A function to turn on KCAL
     */
    override fun turnOn() {
        // KCAL is always on?
    }

    /**
     * A function to adjust KCAL colors
     */
    override fun setColors(red: Int, green: Int, blue: Int) : Boolean {
        return RootUtils.writeToMultipleFilesAtOnce(
                listOf(red.toString(), green.toString(), blue.toString()),
                listOf(KCAL_RED, KCAL_GREEN, KCAL_BLUE)
        )
    }

    /**
     * A function get current KCAL color readings
     */
    override fun getColorReadings(): IntArray {
        val redStr = RootUtils.readOneLine(KCAL_RED)
        val greenStr = RootUtils.readOneLine(KCAL_GREEN)
        val blueStr = RootUtils.readOneLine(KCAL_BLUE)

        return intArrayOf(
                if (redStr.isEmpty()) 255 else redStr.toInt(),
                if (greenStr.isEmpty()) 255 else greenStr.toInt(),
                if (blueStr.isEmpty()) 255 else blueStr.toInt()
        )
    }

    /**
     * Function to determine whether saturation is supported.
     * Saturation does not work as expected on SDM845 drivers.
     */
    override fun isSaturationSupported() = false

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
    override fun setSaturation(value: Int): Boolean {
        return false
    }

    override fun getImplementationName() = "KCAL for v4.4 kernels"

    override fun getImplementationSwitchPath() = "Not Available"

    override fun getImplementationFilePaths() = "$KCAL_RED\n$KCAL_GREEN\n$KCAL_BLUE"

    override fun getImplementationFormat() = "%d"

    override fun getSaturationPath() = KCAL_SAT
}