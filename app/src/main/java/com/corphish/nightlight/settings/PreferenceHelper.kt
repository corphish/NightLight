package com.corphish.nightlight.settings

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager

/**
 * Object to make working with SharedPreferences easier.
 */
internal object PreferenceHelper {
    /**
     * Gets boolean value of Preference for given key
     * @param context Context is needed for accessing SharedPreferences
     * @param key Key of preference to get
     * @param defaultValue Default value to return if preference for given key is not found
     * @return The boolean value of shared preference for given key
     */
    private fun getBoolean(context: Context, key: String, defaultValue: Boolean = false) = PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(key, defaultValue)

    /**
     * Puts boolean value for key as shared preference
     * @param context Context is needed for accessing SharedPreferences
     * @param key Key for shared preference
     * @param value Value to be put
     */
    private fun putBoolean(context: Context, key: String, value: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context).edit {
            putBoolean(key, value)
        }
    }

    /**
     * Gets int value of Shared Preference of given key
     * @param context Context is needed for accessing SharedPreferences
     * @param key Shared Preference key
     * @param defaultValue Default value to return if shared preference for given key is not found
     * @return Value as int of required shared preference
     */
    private fun getInt(context: Context, key: String, defaultValue: Int) = PreferenceManager.getDefaultSharedPreferences(context)
            .getInt(key, defaultValue)

    /**
     * Puts int in required shared preference
     * @param context Context is needed for accessing SharedPreferences
     * @param key Shared Preference key
     * @param value Value to put
     */
    private fun putInt(context: Context, key: String, value: Int) {
        PreferenceManager.getDefaultSharedPreferences(context).edit {
            putInt(key, value)
        }
    }

    /**
     * Gets String value of Shared Preference of given key
     * @param context Context is needed for accessing SharedPreferences
     * @param key Shared Preference key
     * @param defaultValue Default value to return if shared preference for given key is not found
     * @return Value as String of required shared preference
     */
    private fun getString(context: Context, key: String, defaultValue: String): String =
            PreferenceManager.getDefaultSharedPreferences(context)
                    .getString(key, defaultValue) ?: defaultValue

    /**
     * Puts String in required shared preference
     * @param context Context is needed for accessing SharedPreferences
     * @param key Shared Preference key
     * @param value Value to put
     */
    private fun putString(context: Context, key: String, value: String) {
        PreferenceManager.getDefaultSharedPreferences(context).edit {
            putString(key, value)
        }
    }

    /**
     * Generic wrapper for getting preference values.
     *
     * @param T Type.
     * @param context Context.
     * @param prefKey Preference key.
     * @param defaultValue Default value.
     * @return Preference value
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> get(context: Context, prefKey: String, defaultValue: T): T =
            when (defaultValue) {
                is Int -> getInt(context, prefKey, defaultValue) as T
                is Boolean -> getBoolean(context, prefKey, defaultValue) as T
                is String -> getString(context, prefKey, defaultValue) as T
                else -> defaultValue
            }

    /**
     * Generic wrapper for putting preference values.
     *
     * @param T Type.
     * @param context Context.
     * @param prefKey Preference key.
     * @param value Value.
     * @return Preference value
     */
    fun <T> put(context: Context, prefKey: String, value: T) {
        when (value) {
            is Int -> putInt(context, prefKey, value)
            is Boolean -> putBoolean(context, prefKey, value)
            is String -> putString(context, prefKey, value)
        }
    }
}