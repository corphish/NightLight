package com.corphish.nightlight.engine

import android.content.Context

import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.engine.kcal.DummyKCALManager
import com.corphish.nightlight.engine.kcal.GenericKCALManager
import com.corphish.nightlight.engine.kcal.KCALAbstraction
import com.corphish.nightlight.engine.kcal.SDM845KCALManager
import com.corphish.nightlight.extensions.fromColorTemperatureToRGBString
import com.corphish.nightlight.helpers.PreferenceHelper

/**
 * Created by avinabadalal on 30/12/17.
 * All KCAL stuff are done here
 */

object KCALManager {

    private var kcalImplementation: KCALAbstraction

    init {
        kcalImplementation = GenericKCALManager()
        if (!kcalImplementation.isSupported()) kcalImplementation = SDM845KCALManager()
        if (!kcalImplementation.isSupported()) kcalImplementation = DummyKCALManager()
    }

    /**
     * Checks whether KCAL is supported by the kernel
     * @return A boolean indicating whether KCAL support is available or not
     */
    val isKCALAvailable: Boolean
        get() = kcalImplementation.isSupported()

    private val isKCALEnabled: Boolean
        get() = kcalImplementation.isEnabled()

    /**
     * Gets current KCAL RGB values irrespective of whether it is enabled or not
     * @return Current KCAL RGB values as written by driver
     */
    private val kcalValuesAsRawString: String
        get() {
            val reading = kcalImplementation.getColorReadings()

            return "${reading[0]} ${reading[1]} ${reading[2]}"
        }

    /**
     * Enables KCAL
     */
    fun enableKCAL() {
        kcalImplementation.turnOn()
    }

    /**
     * Updates KCAL for given RGB values
     * No need to do sanity checks as the user controls are well controlled and hence less likely to result invalid data
     * Even if input is invalid, driver takes care of it
     * @param rawValue Input values in form of "<Red> <Green> <Blue>" as supported by driver
     * @return Whether setting KCAL values was success or not
    </Blue></Green></Red> */
    fun updateKCALValues(rawValue: String?): Boolean {
        // If operation is successful, there should be no output
        // Otherwise we would get output like "Permission denied"
        if (rawValue == null) return false

        val colors = rawValue.split(" ".toRegex())

        return updateKCALValues(colors[0].toInt(), colors[1].toInt(), colors[2].toInt())
    }

    /**
     * Updates KCAL for given RGB values
     * No need to do sanity checks as the user controls are well controlled and hence less likely to result invalid data
     * Even if input is invalid, driver takes care of it
     * @param red Red color value
     * @param green Green color value
     * @param blue Blue color value
     * @return Whether setting KCAL values was success or not
     */
    fun updateKCALValues(red: Int, green: Int, blue: Int): Boolean {
        return kcalImplementation.setColors(red, green, blue)
    }

    /**
     * Updates KCAL for given RGB values
     * No need to do sanity checks as the user controls are well controlled and hence less likely to result invalid data
     * Even if input is invalid, driver takes care of it
     * @param rgb RGB colors as int array
     * @return Whether setting KCAL values was success or not
     */
    fun updateKCALValues(rgb: IntArray): Boolean {
        return updateKCALValues(rgb[0], rgb[1], rgb[2])
    }

    fun updateKCALWithDefaultValues(): Boolean {
        return updateKCALValues(256, 256, 256)
    }

    /**
     * Safely back up current KCAL values
     * Safely in the sense, if the current KCAL reading is same as user selected ones for Night Light, then don't backup
     * Situations may occur that night light KCAL values are tried to be backed up, avoid those situations
     * If current KCAL value same as night light values, then it will be avoided, but that would be purely coincidental if user uses that setting by default
     * This should only be called **before** turning on Night Light
     * @param context Context is needed for PreferenceHelper
     */
    fun backupCurrentKCALValues(context: Context?) {
        // Dont backup if KCAL is off currently
        if (!isKCALEnabled) return

        // Don't backup if KCAL preserve switch is off (less likely case tho)
        if (!PreferenceHelper.getBoolean(context, Constants.KCAL_PRESERVE_SWITCH, true)) return

        val currentReading = kcalValuesAsRawString

        // Bail out if currentReading is same as nightlight setting reading
        // That is night light values are being backed up
        if (currentReading == getCurrentNightLightSettingReading(context)) return

        // Else backup the values
        PreferenceHelper.putString(context, Constants.KCAL_PRESERVE_VAL, currentReading)
    }

    private fun getCurrentNightLightSettingReading(context: Context?) =
            if (PreferenceHelper.getInt(context, Constants.PREF_SETTING_MODE, Constants.NL_SETTING_MODE_TEMP) == Constants.NL_SETTING_MODE_TEMP)
                PreferenceHelper.getInt(context, Constants.PREF_COLOR_TEMP, Constants.DEFAULT_COLOR_TEMP).fromColorTemperatureToRGBString()
            else
                "${PreferenceHelper.getInt(context, Constants.PREF_RED_COLOR, Constants.DEFAULT_RED_COLOR)} ${PreferenceHelper.getInt(context, Constants.PREF_GREEN_COLOR, Constants.DEFAULT_GREEN_COLOR)} ${PreferenceHelper.getInt(context, Constants.PREF_BLUE_COLOR, Constants.DEFAULT_BLUE_COLOR)}"
}
