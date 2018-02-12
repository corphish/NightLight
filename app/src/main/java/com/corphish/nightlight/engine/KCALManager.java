package com.corphish.nightlight.engine;

import android.content.Context;
import android.util.Log;

import com.corphish.nightlight.data.Constants;
import com.corphish.nightlight.helpers.PreferenceHelper;
import com.corphish.nightlight.helpers.RootUtils;

import java.io.File;
import java.util.Arrays;

/**
 * Created by avinabadalal on 30/12/17.
 * All KCAL stuff are done here
 */

public class KCALManager {

    /*
     * For quick reference, sysfs nodes for KCAL are -
     * KCAL_SWITCH -> /sys/devices/platform/kcal_ctrl.0/kcal_enable
     * KCAL_ADJUST -> /sys/devices/platform/kcal_ctrl.0/kcal
     */

    /**
     * Checks whether KCAL is supported by the kernel
     * @return A boolean indicating whether KCAL support is available or not
     */
    public static boolean isKCALAvailable() {
        return new File(Constants.KCAL_SWITCH).exists();
    }

    /**
     * Checks whether KCAL is enabled or not
     * @return A boolean indicating whether KCAL is enabled or not
     */
    public static boolean isKCALEnabled() {
        return RootUtils.readOneLine(Constants.KCAL_SWITCH)
                .equals("1");
    }

    /**
     * Enables KCAL
     */
    public static void enableKCAL() {
        RootUtils.writeToFile("1", Constants.KCAL_SWITCH);
    }

    /**
     * Disables KCAL
     */
    public static void disableKCAL() {
        RootUtils.writeToFile("0", Constants.KCAL_SWITCH);
    }

    /**
     * Updates KCAL for given RGB values
     * No need to do sanity checks as the user controls are well controlled and hence less likely to result invalid data
     * Even if input is invalid, driver takes care of it
     * @param rawValue Input values in form of "<Red> <Green> <Blue>" as supported by driver
     */
    public static void updateKCALValues(String rawValue) {
        RootUtils.writeToFile(rawValue, Constants.KCAL_ADJUST);
    }

    /**
     * Updates KCAL for given RGB values
     * No need to do sanity checks as the user controls are well controlled and hence less likely to result invalid data
     * Even if input is invalid, driver takes care of it
     * @param red Red color value
     * @param green Green color value
     * @param blue Blue color value
     */
    public static void updateKCALValues(int red, int green, int blue) {
        updateKCALValues(red + " " +  green + " " + blue);
    }

    /**
     * Updates KCAL for given RGB values
     * No need to do sanity checks as the user controls are well controlled and hence less likely to result invalid data
     * Even if input is invalid, driver takes care of it
     * @param rgb RGB colors as int array
     */
    public static void updateKCALValues(int[] rgb) {
        Log.i("NL_KCALManager","Got values - " + Arrays.toString(rgb));
        updateKCALValues(rgb[0], rgb[1], rgb[2]);
    }

    public static void updateKCALWithDefaultValues() {
        updateKCALValues(256, 256, 256);
    }

    /**
     * Gets current KCAL RGB values irrespective of whether it is enabled or not
     * @return Current KCAL RGB values as written by driver
     */
    public static String getKCALValuesAsRawString() {
        return RootUtils.readOneLine(Constants.KCAL_ADJUST);
    }

    /**
     * Gets current KCAL RGB values irrespective of whether it is enabled or not
     * @return Current KCAL RGB values as int array in form of [red, green, blue]
     */
    public static int[] getKCALValuesAsIntRGB() {
        String values[] = getKCALValuesAsRawString().split(" ");

        return new int[] {
                Integer.parseInt(values[0]), // Red
                Integer.parseInt(values[1]), // Green
                Integer.parseInt(values[2]), // Blue
        };
    }

    /**
     * Safely back up current KCAL values
     * Safely in the sense, if the current KCAL reading is same as user selected ones for Night Light, then don't backup
     * Situations may occur that night light KCAL values are tried to be backed up, avoid those situations
     * If current KCAL value same as night light values, then it will be avoided, but that would be purely coincidental if user uses that setting by default
     * This should only be called <strong>before</strong> turning on Night Light
     * @param context Context is needed for PreferenceHelper
     */
    public static void backupCurrentKCALValues(Context context) {
        // Dont backup if KCAL is off currently
        if (!isKCALEnabled()) return;

        // Don't backup if KCAL preserve switch is off (less likely case tho)
        if (!PreferenceHelper.getBoolean(context, Constants.KCAL_PRESERVE_SWITCH, true)) return;

        String currentReading = getKCALValuesAsRawString();
        String nightLightSettingReading = "256 " +  // Red
                PreferenceHelper.getInt(context, Constants.PREF_GREEN_INTENSITY, Constants.DEFAULT_GREEN_INTENSITY) + " " + // Green
                PreferenceHelper.getInt(context, Constants.PREF_BLUE_INTENSITY, Constants.DEFAULT_BLUE_INTENSITY);  // Blue

        // Bail out if currentReading is same as nightlight setting reading
        // That is night light values are being backed up
        if (currentReading.equals(nightLightSettingReading)) return;

        // Else backup the values
        PreferenceHelper.putString(context, Constants.KCAL_PRESERVE_VAL, currentReading);
    }
}
