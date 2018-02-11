package com.corphish.nightlight.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.corphish.nightlight.data.Constants;

/**
 * Created by Avinaba on 10/13/2017.
 * A helper class to deal with preferences
 */

public class PreferenceHelper {

    /**
     * Gets boolean value of Preference for given key
     * @param context Context is needed for accessing SharedPreferences
     * @param key Key of preference to get
     * @param defaultValue Default value to return if preference for given key is not found
     * @return The boolean value of shared preference for given key
     */
    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(key, defaultValue);
    }

    /**
     * Gets boolean value of Preference for given key
     * @param context Context is needed for accessing SharedPreferences
     * @param key Key of preference to get
     * @return The boolean value of shared preference for given key
     */
    public static boolean getBoolean(Context context, String key) {
        return getBoolean(context, key, false);
    }

    /**
     * Puts boolean value for key as shared preference
     * @param context Context is needed for accessing SharedPreferences
     * @param key Key for shared preference
     * @param value Value to be put
     */
    public static void putBoolean(Context context, String key, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(key, value)
                .apply();
    }

    /**
     * Gets toggled boolean value of shared preference of given key
     * It also saves the toggled value
     * @param context Context is needed for accessing SharedPreferences
     * @param key Shared Preference key
     * @return Toggled value
     */
    public static boolean getToggledBoolean(Context context, String key) {
        boolean currentStatus = getBoolean(context, key, false);

        // Toggle it
        currentStatus = !currentStatus;

        // Save it
        putBoolean(context, key, currentStatus);

        return currentStatus;
    }

    /**
     * Gets int value of Shared Preference of given key
     * @param context Context is needed for accessing SharedPreferences
     * @param key Shared Preference key
     * @param defaultValue Default value to return if shared preference for given key is not found
     * @return Value as int of required shared preference
     */
    public static int getInt(Context context, String key, int defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(key, defaultValue);
    }

    /**
     * Puts int in required shared preference
     * @param context Context is needed for accessing SharedPreferences
     * @param key Shared Preference key
     * @param value Value to put
     */
    public static void putInt(Context context, String key, int value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(key, value)
                .apply();
    }

    /**
     * Gets String value of Shared Preference of given key
     * @param context Context is needed for accessing SharedPreferences
     * @param key Shared Preference key
     * @param defaultValue Default value to return if shared preference for given key is not found
     * @return Value as String of required shared preference
     */
    public static String getString(Context context, String key, String defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(key, defaultValue);
    }

    /**
     * Puts String in required shared preference
     * @param context Context is needed for accessing SharedPreferences
     * @param key Shared Preference key
     * @param value Value to put
     */
    public static void putString(Context context, String key, String value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(key, value)
                .apply();
    }

    /**
     * Gets saved location
     * @param context ¯\_(ツ)_/¯
     * @return A double array indicating the location as {Longitude, Latitude}
     */
    public static double[] getLocation(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        return new double[] {
                Double.parseDouble(sharedPreferences.getString(Constants.LAST_LOC_LONGITUDE, Constants.DEFAULT_LONGITUDE)),
                Double.parseDouble(sharedPreferences.getString(Constants.LAST_LOC_LATITUDE, Constants.DEFAULT_LATITUDE))
        };
    }

    /**
     * Saves location co-ordinates as string
     * @param context ¯\_(ツ)_/¯
     * @param longitude Longitude to be saved
     * @param latitude Latitude to be saved
     */
    public static void putLocation(Context context, double longitude, double latitude) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(Constants.LAST_LOC_LONGITUDE, "" + longitude)
                .putString(Constants.LAST_LOC_LATITUDE, "" + latitude)
                .apply();
    }
}
