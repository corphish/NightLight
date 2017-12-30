package com.corphish.nightlight.Interfaces;

/**
 * Created by avinabadalal on 30/12/17.
 * Will listen to night light change events
 */

public interface NightLightStateListener {
    void onStateChanged(boolean newState);
}
