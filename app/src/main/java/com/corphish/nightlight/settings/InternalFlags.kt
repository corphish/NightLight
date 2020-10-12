package com.corphish.nightlight.settings

import android.content.Context
import com.corphish.nightlight.data.Constants

/**
 * Class to store and manage internal flags.
 *
 * @property context Context.
 */
class InternalFlags (private val context: Context) {
    /*
     * Root status check which is done for the first time after reboot. Root check is
     * not done further in order to speed app startup times.
     */
    val compatibilityTest = Setting(context, Constants.COMPATIBILITY_TEST, false)

    /*
     * Internal boot flags.
     * Boot mode = Flag indicating whether we are attempting access root related
     * functions, right after boot.
     * Last boot res = Flag indicating whether set on boot was successful or not.
     */
    val bootMode = Setting(context, Constants.PREF_BOOT_MODE, false)
    val lastBootStatus = Setting(context, Constants.PREF_LAST_BOOT_RES, false)

    /*
     * Setting apply type.
     * Indicates whether a profile was applied or not.
     * So that it can be restored when necessary.
     */
    val lastApplyType = Setting(context, Constants.PREF_CUR_APPLY_TYPE, Constants.APPLY_TYPE_NON_PROFILE)
    val lastApplyEnabled = Setting(context, Constants.PREF_CUR_APPLY_EN, false)
    val lastApplyMode = Setting(context, Constants.PREF_CUR_PROF_MODE, Constants.NL_SETTING_MODE_TEMP)
    val lastApplySettings = Setting(context, Constants.PREF_CUR_PROF_VAL, "256,256,256")

    /*
     * Internal fading indicator to allow overriding fade mode.
     */
    val fadeIndicator = Setting(context, Constants.PREF_FADE_ENABLED, false)

    /*
     * Internal flags to indicate foreground service status and change
     * caused by lock screen event.
     */
    val disabledByLockScreen = Setting(context, Constants.PREF_DISABLED_BY_LOCK_SCREEN, false)
    val foregroundServiceStatus = Setting(context, Constants.PREF_SERVICE_STATE, false)
}