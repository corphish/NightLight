package com.corphish.nightlight.Data;

/**
 * Created by Avinaba on 10/4/2017.
 * Constants
 */

public class Constants {
    /**
     * KCAL sysfs nodes
     */
    private static final String KCAL_DIR            =    "/sys/devices/platform/kcal_ctrl.0/";
    public static final String KCAL_ADJUST          =    KCAL_DIR + "kcal";
    public static final String KCAL_SWITCH          =    KCAL_DIR + "kcal_enable";

    /**
     * SharedPreference keys
     */
    public static final String PREF_MASTER_SWITCH   =   "switch";
    public static final String PREF_FORCE_SWITCH    =   "force_switch";
    public static final String PREF_BLUE_INTENSITY  =   "custom_val";
    public static final String PREF_GREEN_INTENSITY =   "green_intensity";
    public static final String PREF_AUTO_SWITCH     =   "auto_switch";
    public static final String PREF_SUN_SWITCH      =   "sun_switch";
    public static final String PREF_START_TIME      =   "start_time";
    public static final String PREF_END_TIME        =   "end_time";
    public static final String PREF_LAST_START_TIME =   "last_start_time";
    public static final String PREF_LAST_END_TIME   =   "last_end_time";
    public static final String LAST_LOC_LONGITUDE   =   "last_longitude";
    public static final String LAST_LOC_LATITUDE    =   "last_latitude";
    public static final String COMPATIBILITY_TEST   =   "compatibility_test";
    public static final String KCAL_PRESERVE_SWITCH =   "kcal_preserve_switch";
    public static final String KCAL_PRESERVE_VAL    =   "kcal_preserve_val";


    /**
     * Fixed values (default, max values)
     */
    public static final String DEFAULT_START_TIME   =   "00:00";
    public static final String DEFAULT_END_TIME     =   "06:00";
    public static final int MAX_BLUE_LIGHT          =   224;
    public static final int DEFAULT_BLUE_INTENSITY  =   64;
    public static final int MAX_GREEN_LIGHT         =   256;
    public static final int DEFAULT_GREEN_INTENSITY =   0;
    public static final String DEFAULT_LONGITUDE    =   "0.00";
    public static final String DEFAULT_LATITUDE     =   "0.00";
    public static final String DEFAULT_KCAL_VALUES  =   "256 256 256";
}
