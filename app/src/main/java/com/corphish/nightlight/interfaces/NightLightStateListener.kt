package com.corphish.nightlight.interfaces

/**
 * Created by avinabadalal on 30/12/17.
 * Will listen to night light change events
 */

interface NightLightStateListener {
    fun onStateChanged(newState: Boolean)
}
