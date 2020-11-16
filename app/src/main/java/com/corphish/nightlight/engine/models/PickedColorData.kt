package com.corphish.nightlight.engine.models

import android.content.Context
import android.content.Intent
import com.corphish.nightlight.R
import com.corphish.nightlight.data.Constants

/**
 * This data class wraps up picked color so that the key aspects from
 * it can be easily accessed.
 * Colors are picked from [ColorControlActivity].
 */
data class PickedColorData(
        /**
         * Setting mode of the picked color.
         * Can be either temperature or manual.
         */
        val settingMode: Int,

        /**
         * Selected settings.
         * In case of temperature, this array is of length containing only the
         * picked temperature.
         * In case of manual, this array is of length 3, containing the RGB values.
         */
        val settings: IntArray,
) {
    /**
     * Updates an intent with picked data.
     *
     * @param intent Intent to update.
     */
    fun updateIntent(intent: Intent?) {
        if (intent != null) {
            intent.putExtra(Constants.PREF_SETTING_MODE, settingMode)
            if (settingMode == Constants.NL_SETTING_MODE_TEMP) {
                intent.putExtra(Constants.PREF_COLOR_TEMP, settings[0])
            } else {
                intent.putExtra(Constants.PREF_RED_COLOR, settings[0])
                intent.putExtra(Constants.PREF_GREEN_COLOR, settings[1])
                intent.putExtra(Constants.PREF_BLUE_COLOR, settings[2])
            }
        }
    }

    /**
     * Shows summary of the following picked data.
     *
     * @param context Context.
     * @return Summary.
     */
    fun summarise(context: Context): String {
        return if (settingMode == Constants.NL_SETTING_MODE_TEMP) {
            "${context.getString(R.string.color_temperature_title)}: ${settings[0]}K"
        } else {
            "RGB(${settings[0]}, ${settings[1]}, ${settings[2]})"
        }
    }

    /*
     * Auto-generated stuff.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PickedColorData

        if (settingMode != other.settingMode) return false
        if (!settings.contentEquals(other.settings)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = settingMode
        result = 31 * result + settings.contentHashCode()
        return result
    }

    companion object {
        /**
         * Builds a [PickedColorData] object from intent.
         *
         * @param data Intent.
         * @return [PickedColorData] object from the intent.
         */
        fun fromIntent(data: Intent): PickedColorData {
            val mode = data.getIntExtra(Constants.PREF_SETTING_MODE, Constants.NL_SETTING_MODE_TEMP)
            val settings = if (mode == Constants.NL_SETTING_MODE_TEMP) {
                intArrayOf(
                        data.getIntExtra(Constants.PREF_COLOR_TEMP, Constants.DEFAULT_COLOR_TEMP)
                )
            } else {
                intArrayOf(
                        data.getIntExtra(Constants.PREF_RED_COLOR, Constants.DEFAULT_RED_COLOR),
                        data.getIntExtra(Constants.PREF_GREEN_COLOR, Constants.DEFAULT_GREEN_COLOR),
                        data.getIntExtra(Constants.PREF_BLUE_COLOR, Constants.DEFAULT_BLUE_COLOR)
                )
            }

            return PickedColorData(mode, settings)
        }
    }
}