package com.corphish.nightlight;

/**
 * Created by Avinaba on 10/4/2017.
 * Constants
 */

class Constants {
    private static final String KCAL_DIR =      "/sys/devices/platform/kcal_ctrl.0/";
    static final String KCAL_ADJUST =    KCAL_DIR + "kcal";
    static final String KCAL_SWITCH =    KCAL_DIR + "kcal_enable";

    public static final String PREF_MASTER_SWITCH   =   "switch";
    public static final String PREF_CUSTOM_VAL      =   "custom_val";
    public static final int PREF_PRESET_CUSTOM      =   3;
}
