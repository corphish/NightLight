package com.corphish.nightlight.Engine;

import android.content.Context;
import android.util.Log;

import com.corphish.nightlight.Data.Constants;
import com.corphish.nightlight.Helpers.PreferenceHelper;
import com.corphish.nightlight.Helpers.RootUtils;

import java.io.File;

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
     * Backs up current KCAL values
     * This should only be called <strong>before</strong> turning on Night Light
     * @param context Context is needed for PreferenceHelper
     */
    public static void backupCurrentKCALValues(Context context) {
        // Dont backup if KCAL is off currently
        if (!isKCALEnabled()) return;

        // Don't backup if KCAL preserve switch is off (less likely case tho)
        if (!PreferenceHelper.getBoolean(context, Constants.KCAL_PRESERVE_SWITCH, true)) return;

        // Backup the values
        PreferenceHelper.putString(context, Constants.KCAL_PRESERVE_VAL, getKCALValuesAsRawString());
    }
}
