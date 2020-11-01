package com.corphish.nightlight.services

import com.corphish.nightlight.interfaces.NightLightStateListener
import com.corphish.nightlight.interfaces.ThemeChangeListener

/**
 * Created by avinabadalal on 30/12/17.
 * Night Light App Service
 * This runs as long as the app is running
 * External events or services can request update on in-app elements (like views) through this service
 * For example, when user is using the app, and it toggles night light from quick settings, then the changes must be updated in the app as well.
 * This is done to make the UX for responsive
 */

class NightLightAppService
/**
 * Private constructors ensures the usage of this singleton implementation
 */
private constructor() {

    /**
     * Running status of this service
     */
    /**
     * To check whether this service is running or not, service status is checked
     * Accessing this through @link getInstance() will always create the new instance if not already and return it
     * So to check whether this is actually running or not, service status is checked
     * As a result, @link startService() must be called to set this service running
     * Other units can then access it through here, including QSService and other receivers
     * In this way, QSService and other external units can make changes in app (UI especially) when the app is running
     * When the app is not running, the event listener is null and thus external units cant make changes as they do not need to
     * @return Whether event listeners are null or not
     */
    var isAppServiceRunning = false
        private set

    /**
     * This night light state listener listens to night light toggle events and performs accordingly
     */
    private var nightLightStateListener: NightLightStateListener? = null

    /**
     * A listener for theme change event
     */
    private var themeChangeListener: ThemeChangeListener? = null

    /**
     * Starts this service
     */
    fun startService() {
        isAppServiceRunning = true
    }

    /**
     * This sets the defined nl state listener in this service
     * @param nightLightStateListener Defined night light state listener
     * @return This instance to allow chaining of calls
     */
    fun registerNightLightStateListener(nightLightStateListener: NightLightStateListener): NightLightAppService {
        this.nightLightStateListener = nightLightStateListener

        return this
    }

    /**
     * This sets the defined theme change listener in this service
     * @param themeChangeListener Defined theme change listener
     * @return This instance to allow chaining of calls
     */
    fun registerThemeChangeListener(themeChangeListener: ThemeChangeListener): NightLightAppService {
        this.themeChangeListener = themeChangeListener

        return this
    }

    /**
     * External units can notify updated night light state through this
     * @param newState New state of night light
     */
    fun notifyUpdatedState(newState: Boolean) {
        if (nightLightStateListener != null) {
            nightLightStateListener!!.onStateChanged(newState)
        }
    }

    /**
     * Global notification for new theme
     * @param isLightTheme A boolean indicating whether the new theme is light or not
     */
    fun notifyThemeChanged(isLightTheme: Boolean) {
        if (themeChangeListener != null) {
            themeChangeListener!!.onThemeChanged(isLightTheme)
        }
    }

    /**
     * Destroys the event listeners
     * This **has to be called in onDestroy() of MainActivity**
     */
    fun destroy() {
        nightLightStateListener = null
        isAppServiceRunning = false
    }

    companion object {
        /**
         * Gets the global instance of this
         * @return Instance
         */
        val instance = NightLightAppService()
    }
}
