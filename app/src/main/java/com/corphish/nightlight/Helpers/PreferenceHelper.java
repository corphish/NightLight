package com.corphish.nightlight.Helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.corphish.nightlight.Data.Constants;

/**
 * Created by Avinaba on 10/13/2017.
 * A helper class to deal with preferences
 */

public class PreferenceHelper {

    /**
     * Gets current master switch status
     * @param context - ¯\_(ツ)_/¯
     * @return - Master switch status
     */
    public static boolean getMasterSwitchStatus(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPreferences.getBoolean(Constants.PREF_MASTER_SWITCH, false);
    }

    /**
     * Saves master switch status
     * @param context - ¯\_(ツ)_/¯
     * @param status - status of master switch
     */
    public static void putMasterSwitchStatus(Context context, boolean status) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(Constants.PREF_MASTER_SWITCH, status)
                .apply();
    }

    /**
     * Gets current force switch status
     * @param context - ¯\_(ツ)_/¯
     * @return - Master switch status
     */
    public static boolean getForceSwitchStatus(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPreferences.getBoolean(Constants.PREF_FORCE_SWITCH, false);
    }

    /**
     * Saves force switch status
     * @param context - ¯\_(ツ)_/¯
     * @param status - status of master switch
     */
    public static void putForceSwitchStatus(Context context, boolean status) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(Constants.PREF_FORCE_SWITCH, status)
                .apply();
    }

    /**
     * Gets current force switch status after toggling it
     * @param context - ¯\_(ツ)_/¯
     * @return - Toggled force switch status
     */
    public static boolean getToggledForceSwitchStatus(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        boolean currentStatus = sharedPreferences.getBoolean(Constants.PREF_FORCE_SWITCH, false);

        // Toggle it
        currentStatus = !currentStatus;

        // Save it
        sharedPreferences.edit()
                .putBoolean(Constants.PREF_FORCE_SWITCH, currentStatus)
                .apply();

        return currentStatus;
    }

    /**
     * Gets current intensity of blue light
     * @param context - ¯\_(ツ)_/¯
     * @return - Blue light intensity
     */
    public static int getBlueIntensity(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPreferences.getInt(Constants.PREF_BLUE_INTENSITY, Constants.DEFAULT_BLUE_INTENSITY);
    }

    /**
     * Saves user defined blue intensity
     * @param context - ¯\_(ツ)_/¯
     * @param intensity - Intensity to be saved
     */
    public static void putBlueIntensity(Context context, int intensity) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        sharedPreferences.edit()
                .putInt(Constants.PREF_BLUE_INTENSITY, intensity)
                .apply();
    }

    /**
     * Gets current intensity of green light
     * @param context - ¯\_(ツ)_/¯
     * @return - Blue light intensity
     */
    public static int getGreenIntensity(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPreferences.getInt(Constants.PREF_GREEN_INTENSITY, Constants.DEFAULT_GREEN_INTENSITY);
    }

    /**
     * Saves user defined green intensity
     * @param context - ¯\_(ツ)_/¯
     * @param intensity - Intensity to be saved
     */
    public static void putGreenIntensity(Context context, int intensity) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        sharedPreferences.edit()
                .putInt(Constants.PREF_GREEN_INTENSITY, intensity)
                .apply();
    }

    /**
     * Gets current status of auto-switch
     * @param context - ¯\_(ツ)_/¯
     * @return - Current status of auto-switch
     */
    public static boolean getAutoSwitchStatus(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPreferences.getBoolean(Constants.PREF_AUTO_SWITCH, false);
    }

    /**
     * Saves user defined auto-switch
     * @param context - ¯\_(ツ)_/¯
     * @param status - Status of auto-switch
     */
    public static void putAutoSwitchStatus(Context context, boolean status) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        sharedPreferences.edit()
                .putBoolean(Constants.PREF_AUTO_SWITCH, status)
                .apply();
    }

    /**
     * Gets current status of sun-switch
     * @param context - ¯\_(ツ)_/¯
     * @return - Current status of sun-switch
     */
    public static boolean getSunSwitchStatus(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPreferences.getBoolean(Constants.PREF_SUN_SWITCH, false);
    }

    /**
     * Saves user defined sun-switch
     * @param context - ¯\_(ツ)_/¯
     * @param status - Status of sun-switch
     */
    public static void putSunSwitchStatus(Context context, boolean status) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        sharedPreferences.edit()
                .putBoolean(Constants.PREF_SUN_SWITCH, status)
                .apply();
    }

    /**
     * Gets start time for automatic scheduling
     * @param context - ¯\_(ツ)_/¯
     * @param type - Type of start time
     * @return - Start time
     */
    public static String getStartTime(Context context, String type) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPreferences.getString(type, Constants.DEFAULT_START_TIME);
    }

    /**
     * Gets end time for automatic scheduling
     * @param context - ¯\_(ツ)_/¯
     * @param type - Type of end time
     * @return - End time
     */
    public static String getEndTime(Context context, String type) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPreferences.getString(type, Constants.DEFAULT_END_TIME);
    }

    /**
     * Saves user defined end time for auto scheduling
     * @param context - ¯\_(ツ)_/¯
     * @param timeType - Can be either of startTime or endTime (preference key)
     * @param time - End time to be saved
     */
    public static void putTime(Context context, String timeType, String time) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        sharedPreferences.edit()
                .putString(timeType, time)
                .apply();
    }
}
