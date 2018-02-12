package com.corphish.nightlight.services;

import android.annotation.TargetApi;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import com.corphish.nightlight.data.Constants;
import com.corphish.nightlight.engine.Core;
import com.corphish.nightlight.helpers.PreferenceHelper;
import com.corphish.nightlight.R;

import static com.corphish.nightlight.R.drawable.ic_lightbulb_outline;
import static com.corphish.nightlight.R.drawable.ic_lightbulb_outline_disabled;

/**
 * Created by Avinaba on 10/4/2017.
 * Quick Settings Service
 */

@TargetApi(Build.VERSION_CODES.N)
public class QuickSettingsService extends TileService {

    /**
     * Called when the tile is added to the Quick Settings.
     */
    @Override
    public void onTileAdded() {
        syncTile();
    }

    /**
     * Called when this tile begins listening for events.
     */
    @Override
    public void onStartListening() {
        syncTile();
    }

    /**
     * Called when the user taps the tile.
     */
    @Override
    public void onClick() {
        boolean mode = getServiceStatus();
        updateTileUI(mode);
        doService(mode);
    }

    /**
     * Syncs QSTile with current state of night light
     */
    private void syncTile() {
        boolean state = PreferenceHelper.getBoolean(getApplicationContext(), Constants.PREF_FORCE_SWITCH);

        updateTileUI(state);
    }

    /**
     * Updates the QSTile icon and label
     * @param state - State of night light. True value means night light is on, and vice versa
     */
    private void updateTileUI(boolean state) {
        Tile tile = this.getQsTile();

        if (tile == null) return;

        Icon newIcon;
        int newState;

        String title;

        // Change the tile to match the service status.
        if (state) {
            newIcon = Icon.createWithResource(getApplicationContext(), ic_lightbulb_outline);
            newState = Tile.STATE_ACTIVE;
            title = getString(R.string.on);
        } else {
            newIcon = Icon.createWithResource(getApplicationContext(), ic_lightbulb_outline_disabled);
            newState = Tile.STATE_INACTIVE;

            title = getString(R.string.off);
        }

        // Change the UI of the tile.
        tile.setLabel(title);
        tile.setIcon(newIcon);
        tile.setState(newState);

        // Need to call updateTile for the tile to pick up changes.
        tile.updateTile();
    }

    /**
     * Gets the current night light forceSwitch state, and returns its toggled value
     * @return - Toggled value of forceSwitch
     */
    private boolean getServiceStatus() {
        boolean forceSwitch = PreferenceHelper.getBoolean(getApplicationContext(), Constants.PREF_FORCE_SWITCH);
        boolean masterSwitch = PreferenceHelper.getBoolean(getApplicationContext(), Constants.PREF_MASTER_SWITCH);

        /*
         * If forceSwitch is on, while masterSwitch is off, turn on master switch as well
         */
        forceSwitch = !forceSwitch;
        if (forceSwitch && !masterSwitch)
            PreferenceHelper.putBoolean(getApplicationContext(), Constants.PREF_MASTER_SWITCH ,true);

        return forceSwitch;
    }

    /**
     * Does the main work when QSTile is tapped.
     * That is it toggles night mode on or off.
     * @param state - Current state of QSTile
     */
    private void doService(boolean state) {
        Core.applyNightModeAsync(state, getApplicationContext());
    }
}
