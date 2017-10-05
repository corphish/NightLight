package com.corphish.nightlight.Services;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.preference.PreferenceManager;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import com.corphish.nightlight.Data.Constants;
import com.corphish.nightlight.Engine.Core;
import com.corphish.nightlight.R;

import static com.corphish.nightlight.R.drawable.ic_lightbulb_outline;
import static com.corphish.nightlight.R.drawable.ic_lightbulb_outline_disabled;

/**
 * Created by Avinaba on 10/4/2017.
 * Quick Settings Service
 */

@TargetApi(Build.VERSION_CODES.N)
public class QuickSettingsService extends TileService {
    private static final String SERVICE_STATUS_FLAG = Constants.PREF_MASTER_SWITCH;

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
        updateTile();
    }

    // Changes the appearance of the tile.
    private void updateTile() {
        boolean mode = getServiceStatus();
        updateTileIcon(mode);
        doService(mode);
    }

    private void syncTile() {
        // If auto switch and master switch is enabled, set auto mode
        // If only master is enabled, set on mode
        // Otherwise off mode
        boolean masterSwitch = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(SERVICE_STATUS_FLAG, false);

        updateTileIcon(masterSwitch);
    }

    private void updateTileIcon(boolean mode) {
        Tile tile = this.getQsTile();

        Icon newIcon;
        int newState;

        String title;

        // Change the tile to match the service status.
        if (mode) {
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

    // Access storage to see how many times the tile
    // has been tapped.
    private boolean getServiceStatus() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        boolean mode = prefs.getBoolean(SERVICE_STATUS_FLAG, false);
        mode = !mode;

        prefs.edit().putBoolean(SERVICE_STATUS_FLAG, mode).apply();

        return mode;
    }

    private void doService(boolean mode) {
        int intensity = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt(Constants.PREF_CUSTOM_VAL, Constants.DEFAULT_INTENSITY);

        if (mode) Core.applyNightModeAsync(true, intensity);
        else Core.applyNightModeAsync(false, intensity);
    }
}
