package com.corphish.nightlight.services

import android.annotation.TargetApi
import android.graphics.drawable.Icon
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.engine.Core
import com.corphish.nightlight.helpers.PreferenceHelper
import com.corphish.nightlight.R

import com.corphish.nightlight.R.drawable.ic_lightbulb_outline
import com.corphish.nightlight.R.drawable.ic_lightbulb_outline_disabled

/**
 * Created by Avinaba on 10/4/2017.
 * Quick Settings Service
 */

@TargetApi(Build.VERSION_CODES.N)
class QuickSettingsService : TileService() {

    /**
     * Gets the current night light forceSwitch state, and returns its toggled value
     * @return - Toggled value of forceSwitch
     */
    private/*
         * If forceSwitch is on, while masterSwitch is off, turn on master switch as well
         */ val serviceStatus: Boolean
        get() {
            var forceSwitch = PreferenceHelper.getBoolean(applicationContext, Constants.PREF_FORCE_SWITCH)
            val masterSwitch = PreferenceHelper.getBoolean(applicationContext, Constants.PREF_MASTER_SWITCH)
            forceSwitch = !forceSwitch
            if (forceSwitch && !masterSwitch)
                PreferenceHelper.putBoolean(applicationContext, Constants.PREF_MASTER_SWITCH, true)

            return forceSwitch
        }

    /**
     * Called when the tile is added to the Quick Settings.
     */
    override fun onTileAdded() {
        syncTile()
    }

    /**
     * Called when this tile begins listening for events.
     */
    override fun onStartListening() {
        syncTile()
    }

    /**
     * Called when the user taps the tile.
     */
    override fun onClick() {
        val mode = serviceStatus
        updateTileUI(mode)
        doService(mode)
    }

    /**
     * Syncs QSTile with current state of night light
     */
    private fun syncTile() {
        val state = PreferenceHelper.getBoolean(applicationContext, Constants.PREF_FORCE_SWITCH)

        updateTileUI(state)
    }

    /**
     * Updates the QSTile icon and label
     * @param state - State of night light. True value means night light is on, and vice versa
     */
    private fun updateTileUI(state: Boolean) {
        val tile = this.qsTile ?: return

        val newIcon: Icon
        val newState: Int

        val title: String

        // Change the tile to match the service status.
        if (state) {
            newIcon = Icon.createWithResource(applicationContext, ic_lightbulb_outline)
            newState = Tile.STATE_ACTIVE
            title = getString(R.string.on)
        } else {
            newIcon = Icon.createWithResource(applicationContext, ic_lightbulb_outline_disabled)
            newState = Tile.STATE_INACTIVE

            title = getString(R.string.off)
        }

        // Change the UI of the tile.
        tile.label = title
        tile.icon = newIcon
        tile.state = newState

        // Need to call updateTile for the tile to pick up changes.
        tile.updateTile()
    }

    /**
     * Does the main work when QSTile is tapped.
     * That is it toggles night mode on or off.
     * @param state - Current state of QSTile
     */
    private fun doService(state: Boolean) {
        Core.applyNightModeAsync(state, applicationContext)
    }
}
