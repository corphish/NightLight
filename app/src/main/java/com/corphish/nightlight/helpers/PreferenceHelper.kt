package com.corphish.nightlight.helpers

import android.content.Context
import android.preference.PreferenceManager
import androidx.core.content.edit

import com.corphish.nightlight.data.Constants

/**
 * Created by Avinaba on 10/13/2017.
 * A helper class to deal with preferences
 */

object PreferenceHelper {

    /**
     * Gets boolean value of Preference for given key
     * @param context Context is needed for accessing SharedPreferences
     * @param key Key of preference to get
     * @param defaultValue Default value to return if preference for given key is not found
     * @return The boolean value of shared preference for given key
     */
    fun getBoolean(context: Context?, key: String, defaultValue: Boolean = false): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(key, defaultValue)
    }

    /**
     * Puts boolean value for key as shared preference
     * @param context Context is needed for accessing SharedPreferences
     * @param key Key for shared preference
     * @param value Value to be put
     */
    fun putBoolean(context: Context?, key: String, value: Boolean) {
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
    fun getInt(context: Context?, key: String, defaultValue: Int): Int {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(key, defaultValue)
    }

    /**
     * Puts int in required shared preference
     * @param context Context is needed for accessing SharedPreferences
     * @param key Shared Preference key
     * @param value Value to put
     */
    fun putInt(context: Context?, key: String, value: Int) {
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
    fun getString(context: Context?, key: String, defaultValue: String? = null): String? {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(key, defaultValue)
    }

    /**
     * Puts String in required shared preference
     * @param context Context is needed for accessing SharedPreferences
     * @param key Shared Preference key
     * @param value Value to put
     */
    fun putString(context: Context?, key: String, value: String) {
        PreferenceManager.getDefaultSharedPreferences(context).edit {
            putString(key, value)
        }
    }

    /**
     * Gets saved location
     * @param context ¯\_(ツ)_/¯
     * @return A double array indicating the location as {Longitude, Latitude}
     */
    fun getLocation(context: Context?): DoubleArray {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        return doubleArrayOf(
                sharedPreferences.getString(Constants.LAST_LOC_LONGITUDE, Constants.DEFAULT_LONGITUDE).toDouble(),
                sharedPreferences.getString(Constants.LAST_LOC_LATITUDE, Constants.DEFAULT_LATITUDE).toDouble()
        )
    }

    /**
     * Saves location co-ordinates as string
     * @param context ¯\_(ツ)_/¯
     * @param longitude Longitude to be saved
     * @param latitude Latitude to be saved
     */
    fun putLocation(context: Context?, longitude: Double, latitude: Double) {
        PreferenceManager.getDefaultSharedPreferences(context).edit {
            putString(Constants.LAST_LOC_LONGITUDE, longitude.toString())
            putString(Constants.LAST_LOC_LATITUDE, latitude.toString())
        }
    }
}
