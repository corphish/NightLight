package com.corphish.nightlight.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.engine.ProfilesManager
import com.corphish.nightlight.helpers.PreferenceHelper

import java.util.Arrays

/**
 * Created by avinabadalal on 03/03/18.
 * Tasker service
 */

class TaskerIntentReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val masterSwitchEnabled = PreferenceHelper.getBoolean(context, Constants.PREF_MASTER_SWITCH)
        if (!masterSwitchEnabled) return
        val profilesManager = ProfilesManager(context)
        profilesManager.loadProfiles()
        val bundle = intent.getBundleExtra("com.twofortyfouram.locale.intent.extra.BUNDLE")
                ?: return

        val name = bundle.getString(Constants.TASKER_SETTING) ?: return

        val profile = profilesManager.getProfileByName(name)
        if (profile != null) {
            profile.apply(context)

            PreferenceHelper.putInt(context, Constants.PREF_CUR_APPLY_TYPE, Constants.APPLY_TYPE_PROFILE)
            PreferenceHelper.putBoolean(context, Constants.PREF_CUR_APPLY_EN, profile.isSettingEnabled)
            PreferenceHelper.putInt(context, Constants.PREF_CUR_PROF_MODE, profile.settingMode)
            PreferenceHelper.putString(context, Constants.PREF_CUR_PROF_VAL, Arrays.toString(profile.settings))
        }
    }
}
