package com.corphish.nightlight.helpers;

import android.content.Context;

import com.corphish.nightlight.data.Constants;
import com.corphish.nightlight.engine.Core;
import com.corphish.nightlight.engine.TwilightManager;

/**
 * Created by avinabadalal on 13/02/18.
 * Helper for apply on boot
 */

public class BootUtils {

    public interface OnApplyCompleteListener {
        void onComplete();
    }

    /**
     * Necessary things to be performed on boot
     * @param context Context received by the receiver
     * @param onApplyCompleteListener What needs to be done after this is finished
     */
    public static void applyOnBoot(Context context, OnApplyCompleteListener onApplyCompleteListener) {
        PreferenceHelper.putBoolean(context, Constants.COMPATIBILITY_TEST, false);

        boolean masterSwitch = PreferenceHelper.getBoolean(context, Constants.PREF_MASTER_SWITCH);
        boolean autoSwitch = PreferenceHelper.getBoolean(context, Constants.PREF_AUTO_SWITCH);
        boolean sunSwitch = PreferenceHelper.getBoolean(context, Constants.PREF_SUN_SWITCH);

        String sStartTime = PreferenceHelper.getString(context, Constants.PREF_START_TIME, Constants.DEFAULT_START_TIME);
        String sEndTime = PreferenceHelper.getString(context, Constants.PREF_END_TIME, Constants.DEFAULT_END_TIME);

        if (!masterSwitch) return;

        if (!autoSwitch) {
            Core.applyNightModeAsync(true, context);
            return;
        }

        boolean state = TimeUtils.determineWhetherNLShouldBeOnOrNot(sStartTime, sEndTime);
        Core.applyNightModeAsync(state, context);

        if (!sunSwitch) AlarmUtils.setAlarms(context, sStartTime, sEndTime, true);
        else TwilightManager.newInstance()
                .atLocation(PreferenceHelper.getLocation(context))
                .computeAndSaveTime(context);

        if (onApplyCompleteListener != null) onApplyCompleteListener.onComplete();
    }

    /**
     * Necessary things to be performed on boot
     * @param context Context received by the receiver
     */
    public static void applyOnBoot(Context context) {
        applyOnBoot(context, null);
    }
}
