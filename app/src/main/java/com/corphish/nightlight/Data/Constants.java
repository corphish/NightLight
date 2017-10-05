package com.corphish.nightlight.Data;

/**
 * Created by Avinaba on 10/4/2017.
 * Constants
 */

public class Constants {
    private static final String KCAL_DIR =      "/sys/devices/platform/kcal_ctrl.0/";
    public static final String KCAL_ADJUST =    KCAL_DIR + "kcal";
    public static final String KCAL_SWITCH =    KCAL_DIR + "kcal_enable";

    public static final String PREF_MASTER_SWITCH   =   "switch";
    public static final String PREF_CUSTOM_VAL      =   "custom_val";
    public static final String PREF_AUTO_SWITCH     =   "auto_switch";
    public static final String PREF_START_TIME      =   "start_time";
    public static final String PREF_END_TIME        =   "end_time";
    public static final String PREF_QS_SWITCH       =   "qs_switch";

    public static final String DEFAULT_START_TIME   =   "00:00";
    public static final String DEFAULT_END_TIME     =   "06:00";

    public static final int MAX_BLUE_LIGHT          = 224;
    public static final int DEFAULT_INTENSITY       = 64;
}
