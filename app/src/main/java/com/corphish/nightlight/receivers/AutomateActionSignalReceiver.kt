package com.corphish.nightlight.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.engine.ProfilesManager
import com.corphish.nightlight.helpers.PreferenceHelper

class AutomateActionSignalReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        // At first check whether night light should really be turned off or not
        val masterSwitchEnabled = PreferenceHelper.getBoolean(context, Constants.PREF_MASTER_SWITCH)
        val autoSwitchEnabled = PreferenceHelper.getBoolean(context, Constants.PREF_AUTO_SWITCH)

        // Both of the switches must be on to proceed
        if (!autoSwitchEnabled || !masterSwitchEnabled) return

        // Initialize profile manager
        val profilesManager = ProfilesManager(context)
        profilesManager.loadProfiles()

        // Get parameters
        // Bail out if profileName is not found
        val profileName = intent.getStringExtra(Constants.AUTOMATE_PROFILE_NAME) ?: return


        // Get profile based on parameter
        // If profile is not present, return
        val profile = profilesManager.getProfileByName(profileName) ?: return

        // TODO: Add safety checks
        // Apply profile
        profile.apply(context)
    }
}