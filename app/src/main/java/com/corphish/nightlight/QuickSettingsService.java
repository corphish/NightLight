package com.corphish.nightlight;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.preference.PreferenceManager;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;

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
     * @return TileService constant indicating tile state
     */

    @Override
    public void onTileAdded() {
        Log.d("QS", "Tile added");

        syncTile();
    }

    /**
     * Called when this tile begins listening for events.
     */
    @Override
    public void onStartListening() {
        Log.d("QS", "Start listening");

        syncTile();
    }

    /**
     * Called when the user taps the tile.
     */
    @Override
    public void onClick() {
        Log.d("QS", "Tile tapped");

        updateTile();
    }

    /**
     * Called when this tile moves out of the listening state.
     */
    @Override
    public void onStopListening() {
        Log.d("QS", "Stop Listening");
    }

    /**
     * Called when the user removes this tile from Quick Settings.
     */
    @Override
    public void onTileRemoved() {
        Log.d("QS", "Tile removed");
    }

    // Changes the appearance of the tile.
    private void updateTile() {
        boolean isActive = getServiceStatus();

        updateTileIcon(isActive);


        doService(isActive);
    }

    private void syncTile() {
        updateTileIcon(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(SERVICE_STATUS_FLAG, false));
    }

    private void updateTileIcon(boolean isActive) {
        Tile tile = this.getQsTile();

        Icon newIcon;
        int newState;

        // Change the tile to match the service status.
        if (isActive) {
            newIcon = Icon.createWithResource(getApplicationContext(), ic_lightbulb_outline);
            newState = Tile.STATE_ACTIVE;
        } else {
            newIcon = Icon.createWithResource(getApplicationContext(), ic_lightbulb_outline_disabled);
            newState = Tile.STATE_INACTIVE;
        }

        // Change the UI of the tile.
        tile.setIcon(newIcon);
        tile.setState(newState);

        // Need to call updateTile for the tile to pick up changes.
        tile.updateTile();
    }

    // Access storage to see how many times the tile
    // has been tapped.
    private boolean getServiceStatus() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        boolean isActive = prefs.getBoolean(SERVICE_STATUS_FLAG, false);
        isActive = !isActive;

        prefs.edit().putBoolean(SERVICE_STATUS_FLAG, isActive).apply();

        return isActive;
    }

    private void doService(boolean status) {
        int intensity = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt(Constants.PREF_CUSTOM_VAL, 64);
        Core.applyNightMode(status, intensity);
    }
}
