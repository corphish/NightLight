package com.corphish.nightlight.activities

import android.app.Activity
import android.content.pm.ShortcutManager
import android.os.Build
import android.os.Bundle
import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.engine.Core
import com.corphish.nightlight.helpers.PreferenceHelper

/**
 * Dummy activity to handle shortcut actions.
 */
class ShortcutActivity : Activity() {
    /*
     * Declare the shortcut intent strings and id
     */
    private val _intentNLToggle = "android.intent.action.NL_TOGGLE"
    private val _toggleId = "toggle"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (handleIntent()) {
            finish()
        }
    }

    /**
     * Handle the incoming intent of shortcut
     * Returns true if shortcut was handled, false otherwise
     */
    private fun handleIntent(): Boolean {
        val shortcutID: String

        if (intent.action != null) {
            when (intent.action) {
                _intentNLToggle -> {
                    shortcutID = _toggleId
                    doToggle()
                }
                else -> {
                    shortcutID = ""
                }
            }
        } else
            return false

        if (shortcutID.isEmpty()) return false


        /*
         * On Android 7.0 or below, bail out from now
         * This is because app shortcuts are not supported by default in those android versions
         * It however is supported in 3rd party launchers like nova launcher.
         * As android API guidelines suggest to reportShortcutUsed(), but that can be done only on API 25
         */
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1) return true
        else {
            val shortcutManager = this.getSystemService(ShortcutManager::class.java)
            shortcutManager!!.reportShortcutUsed(shortcutID)
        }

        return true
    }

    /**
     * Actual night light toggling happens here.
     */
    private fun doToggle() {
        val state = !PreferenceHelper.getBoolean(this, Constants.PREF_FORCE_SWITCH)
        val masterSwitch = PreferenceHelper.getBoolean(this, Constants.PREF_MASTER_SWITCH)

        /*
         * If state is on, while masterSwitch is off, turn on masterSwitch as well
         */
        if (state && !masterSwitch)
            PreferenceHelper.putBoolean(this, Constants.PREF_MASTER_SWITCH, true)

        Core.applyNightModeAsync(state, this)
    }
}