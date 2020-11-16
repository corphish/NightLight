package com.corphish.nightlight.engine.models

import com.corphish.nightlight.data.Constants

/**
 * This data class defines a Fade behavior.
 * Fade behavior can be off 3 types -
 * 1. Fade in - The RGB will fade in from certain value, to another value.
 * 2. Fade in - The RGB will fade out from certain value, to another value.
 * 1. Fade off - The RGB will not fade.
 */
data class FadeBehavior(
        /**
         * Behavior type.
         */
        val type: Int = FADE_OFF,

        /**
         * Night light setting type.
         */
        val settingType: Int = Constants.NL_SETTING_MODE_TEMP,

        /**
         * RGB to fade from.
         */
        val fadeFrom: IntArray = intArrayOf(256, 256, 256),

        /**
         * RGB to fade to.
         */
        val fadeTo: IntArray = intArrayOf(256, 256, 256)
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FadeBehavior

        if (type != other.type) return false
        if (!fadeFrom.contentEquals(other.fadeFrom)) return false
        if (!fadeTo.contentEquals(other.fadeTo)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type
        result = 31 * result + fadeFrom.contentHashCode()
        result = 31 * result + fadeTo.contentHashCode()
        return result
    }

    override fun toString() =
            "FadeBehavior($type; $settingType; ${fadeFrom.contentToString()}; ${fadeTo.contentToString()})"

    companion object {
        /**
         * Fade behaviors.
         */
        const val FADE_OFF = 0
        const val FADE_IN = 1
        const val FADE_OUT = 2

        /**
         * Unset RGB
         */
        val RGB_UNSET = intArrayOf(-1, -1, -1)

        /**
         * Parses a FadeBehavior from persisted string.
         */
        fun fromString(string: String?): FadeBehavior {
            // Return default fade behavior if string is null
            if (string == null) {
                return FadeBehavior()
            }

            // Trim and extract values
            val parts = string.substring("FadeBehavior(".length, string.length - 1).split(";")

            // Parts must be of length 3
            if (parts.size != 4) {
                return FadeBehavior()
            }

            val type = parts[0].toInt()
            val settingType = parts[1].trim().toInt()

            val fadeFrom = parts[1].trim()
                    .substring(1, parts[1].lastIndexOf("]"))
                    .split(",")
                    .map { it.trim().toInt() }
                    .toIntArray()

            val fadeTo = parts[2].trim()
                    .substring(1, parts[2].lastIndexOf("]"))
                    .split(",")
                    .map { it.trim().toInt() }
                    .toIntArray()

            return FadeBehavior(type, settingType, fadeFrom, fadeTo)
        }
    }
}