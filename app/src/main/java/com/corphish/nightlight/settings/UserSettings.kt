package com.corphish.nightlight.settings

import android.content.Context
import com.corphish.nightlight.data.Constants

/**
 * This class will provide easy way to access user settings.
 * Before this, to achieve the same, the PreferenceHelper class
 * would be used.
 * The problems with PreferenceHelper were:
 * 1. Non-primitives returned were of null type. We are aiming
 *    to completely get rid of null values.
 * 2. Would make tracking setting type difficult.
 * 3. Inconsistency in default values.
 *
 * While this class will still use the PreferenceHelper class
 * internally, the PreferenceHelper class will become a part
 * of this class so that it cannot be accessed from anywhere else.
 * Which would mean, getting and setting user settings can and must
 * take place from this class.
 *
 * @param context Context.
 */
class UserSettings(private val context: Context) {
    /*
     * Master switch: Main switch which enables the functionality of the
     * app.
     */
    val masterSwitch = Setting(context, Constants.PREF_MASTER_SWITCH, false)

    /*
     * Force switch: Force switch represents the actual state of Night Light
     * co-ordinated by the app. This lets user to force turn on/off Night
     * Light anytime.
     */
    val forceSwitch = Setting(context, Constants.PREF_FORCE_SWITCH, false)

    /*
     * Auto switch: Indicates whether automation is enabled or not.
     */
    val autoSwitch = Setting(context, Constants.PREF_AUTO_SWITCH, false)

    /*
     * Sun switch: Indicates whether sunset/sunrise is enabled or not.
     */
    val sunSwitch = Setting(context, Constants.PREF_SUN_SWITCH, false)

    /*
     * Start time: Indicates automation start time.
     */
    val startTime = Setting(context, Constants.PREF_START_TIME, Constants.DEFAULT_START_TIME)

    /*
     * End time: Indicates automation end time.
     */
    val endTime = Setting(context, Constants.PREF_END_TIME, Constants.DEFAULT_END_TIME)

    /*
     * Last start time: Indicates the last automation start time. Used for backing up
     * and restoring when sunset/sunrise is turned on and off.
     */
    val lastStartTime = Setting(context, Constants.PREF_LAST_START_TIME, Constants.DEFAULT_START_TIME)

    /*
     * Last start time: Indicates the last automation end time. Used for backing up
     * and restoring when sunset/sunrise is turned on and off.
     */
    val lastEndTime = Setting(context, Constants.PREF_LAST_END_TIME, Constants.DEFAULT_END_TIME)

    /*
     * Latitude value of current location.
     */
    val latitude = Setting(context, Constants.LAST_LOC_LATITUDE, Constants.DEFAULT_LATITUDE)

    /*
     * Longitude value of current location.
     */
    val longitude = Setting(context, Constants.LAST_LOC_LONGITUDE, Constants.DEFAULT_LONGITUDE)

    /*
     * KCAL preservation: An option to use a different KCAL setting apart from the default
     * RGB(256, 256, 256) when Night Light is off.
     */
    val kcalPreserveSwitch = Setting(context, Constants.KCAL_PRESERVE_SWITCH, true)
    val kcalAlwaysBackupSwitch = Setting(context, Constants.PREF_KCAL_BACKUP_EVERY_TIME, true)
    val preservedKCAl = Setting(context, Constants.KCAL_PRESERVE_VAL, Constants.DEFAULT_KCAL_VALUES)

    /*
     * Set on boot config.
     * Set on boot switch: Whether set on boot is enabled or not.
     * Boot delay: Provides a delay in applying the Night Light settings after boot.
     */
    val setOnBootSwitch = Setting(context, Constants.PREF_SET_ON_BOOT, Constants.DEFAULT_SET_ON_BOOT)
    val bootDelay = Setting(context, Constants.PREF_BOOT_DELAY, Constants.DEFAULT_BOOT_DELAY)

    /*
     * Fading indicators.
     * Fade switch: Indicates whether fading is enabled.
     * Fade stop time: Indicates the time when fade stops.
     * Fade poll rate: Interval of the chosen minutes by which Night Light will fade in.
     * This is string because that is how Android framework stores. It must be converted
     * to Int to use it.
     */
    val fadeSwitch = Setting(context, Constants.PREF_DARK_HOURS_ENABLE, false)
    val fadeStopTime = Setting(context, Constants.PREF_DARK_HOURS_START, Constants.DEFAULT_END_TIME)
    val fadePollRate = Setting(context, Constants.PREF_FADE_POLL_RATE_MINS, "5")

    /*
     * App UI customisation options.
     * Light theme: Switch indicating whether light theme is enabled or not.
     * Shape: Chosen shape
     */
    val lightThemeSwitch = Setting(context, Constants.PREF_LIGHT_THEME, Constants.DEFAULT_LIGHT_THEME)
    val iconShape = Setting(context, Constants.PREF_ICON_SHAPE, Constants.DEFAULT_ICON_SHAPE)

    /*
     * Wind down switch.
     */
    val windDownSwitch = Setting(context, Constants.PREF_WIND_DOWN, false)

    /*
     * Lock screen service switches.
     */
    val disabledInLockScreenSwitch = Setting(context, Constants.PREF_DISABLE_IN_LOCK_SCREEN, false)
}