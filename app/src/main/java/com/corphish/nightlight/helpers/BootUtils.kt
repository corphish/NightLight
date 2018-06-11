package com.corphish.nightlight.helpers

import android.content.Context

import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.engine.Core
import com.corphish.nightlight.engine.TwilightManager

/**
 * Created by avinabadalal on 13/02/18.
 * Helper for apply on boot
 */

object BootUtils {

    /**
     * Necessary things to be performed on boot
     * @param context Context received by the receiver
     * @param onApplyCompleteListener Callback fired when apply on boot is complete
     */
    fun applyOnBoot(context: Context, onApplyCompleteListener: (() -> Unit)? = null) {
        PreferenceHelper.putBoolean(context, Constants.COMPATIBILITY_TEST, false)

        val masterSwitch = PreferenceHelper.getBoolean(context, Constants.PREF_MASTER_SWITCH)
        val autoSwitch = PreferenceHelper.getBoolean(context, Constants.PREF_AUTO_SWITCH)
        val sunSwitch = PreferenceHelper.getBoolean(context, Constants.PREF_SUN_SWITCH)

        val sStartTime = PreferenceHelper.getString(context, Constants.PREF_START_TIME, Constants.DEFAULT_START_TIME)
        val sEndTime = PreferenceHelper.getString(context, Constants.PREF_END_TIME, Constants.DEFAULT_END_TIME)

        if (!masterSwitch) return
        if (sStartTime == null || sEndTime == null) return

        if (!autoSwitch) {
            Core.applyNightModeAsync(true, context)
            return
        }

        val state = TimeUtils.determineWhetherNLShouldBeOnOrNot(sStartTime, sEndTime)
        Core.applyNightModeAsync(state, context)

        if (!sunSwitch)
            AlarmUtils.setAlarms(context, sStartTime, sEndTime, true)
        else
            TwilightManager.newInstance()
                    .atLocation(PreferenceHelper.getLocation(context))
                    .computeAndSaveTime(context)

        onApplyCompleteListener?.invoke()
    }
}
