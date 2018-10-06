package com.corphish.nightlight.data

/**
 * Created by Avinaba on 10/4/2017.
 * Constants
 */

object Constants {
    /**
     * SharedPreference keys
     */
    const val PREF_MASTER_SWITCH = "switch"
    const val PREF_FORCE_SWITCH = "force_switch"
    const val PREF_BLUE_INTENSITY = "custom_const val"
    const val PREF_GREEN_INTENSITY = "green_intensity"
    const val PREF_AUTO_SWITCH = "auto_switch"
    const val PREF_SUN_SWITCH = "sun_switch"
    const val PREF_START_TIME = "start_time"
    const val PREF_END_TIME = "end_time"
    const val PREF_LAST_START_TIME = "last_start_time"
    const val PREF_LAST_END_TIME = "last_end_time"
    const val LAST_LOC_LONGITUDE = "last_longitude"
    const val LAST_LOC_LATITUDE = "last_latitude"
    const val COMPATIBILITY_TEST = "compatibility_test"
    const val KCAL_PRESERVE_SWITCH = "kcal_preserve_switch"
    const val KCAL_PRESERVE_VAL = "kcal_preserve_const val"
    const val PREF_SETTING_MODE = "nl_setting_mode"
    const val PREF_COLOR_TEMP = "color_temp_const val"
    const val PREF_BOOT_MODE = "boot_mode"
    const val PREF_LAST_BOOT_RES = "last_boot_result"
    const val PREF_BOOT_DELAY = "boot_delay"
    const val PREF_CUR_APPLY_TYPE = "cur_apply_type"
    const val PREF_CUR_APPLY_EN = "cur_apply_switch"
    const val PREF_CUR_PROF_MODE = "cur_profile_mode"
    const val PREF_CUR_PROF_VAL = "cur_profile_settings"
    const val PREF_KCAL_BACKUP_EVERY_TIME = "kcal_backup_everytime"


    /**
     * Fixed const values (default, max const values)
     */
    const val DEFAULT_START_TIME = "00:00"
    const val DEFAULT_END_TIME = "06:00"
    const val MAX_BLUE_LIGHT = 224
    const val DEFAULT_BLUE_INTENSITY = 64
    const val MAX_GREEN_LIGHT = 256
    const val DEFAULT_GREEN_INTENSITY = 0
    const val DEFAULT_LONGITUDE = "0.00"
    const val DEFAULT_LATITUDE = "0.00"
    const val DEFAULT_KCAL_VALUES = "256 256 256"
    const val DEFAULT_COLOR_TEMP = 4000
    const val DEFAULT_BOOT_DELAY = 20

    /**
     * Night light setting modes
     */
    const val NL_SETTING_MODE_TEMP = 0
    const val NL_SETTING_MODE_FILTER = 1

    /**
     * Apply types
     */
    const val APPLY_TYPE_PROFILE = 1
    const val APPLY_TYPE_NON_PROFILE = 0

    /**
     * Tasker error in-app communication msg
     */
    const val TASKER_ERROR_STATUS = "tasker_error"

    const val TASKER_SETTING = "tasker_setting"
}
