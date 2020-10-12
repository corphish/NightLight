package com.corphish.nightlight.settings

import android.content.Context
import android.util.Log

/**
 * Setting class to wrap up a setting.
 *
 * @param T Type.
 * @property prefKey Preference key.
 * @property defaultValue Default value.
 */
class Setting<T>(
        /**
         * Context.
         */
        private val context: Context,

        /**
         * Preference key of this setting.
         */
        private val prefKey: String,

        /**
         * Default value.
         */
        private val defaultValue: T,
) {
    // Value
    var value: T
        get() {
            Log.d("NL_Setting", "Get $prefKey")
            return PreferenceHelper.get(context, prefKey, defaultValue)
        }
        set(value) = PreferenceHelper.put(context, prefKey, value)
}