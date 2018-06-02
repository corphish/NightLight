package com.corphish.nightlight.services

import com.corphish.nightlight.interfaces.NightLightSettingModeListener
import com.corphish.nightlight.interfaces.NightLightStateListener

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
     * This night light setting mode change listener listens to setting mode changes and requests to make proper changes
     */
    private var nightLightSettingModeListener: NightLightSettingModeListener? = null

    /**
     * This variable indicates whether initial app startup has completed or not.
     * Based on this, other units can perform certain tasks when on init, and other tasks when after init is done.
     */
    private var isInitDone = false

    /**
     * This variable denotes number of fragments which have been completely added in MainActivity
     * This is a part of init process
     */
    private var viewInitCount = 0

    /**
     * Total number of fragments which would be added
     */
    private val TOTAL_VIEWS = 5

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
     * This sets the defined nl setting mode listener in this service
     * @param nightLightSettingModeListener Defined nl setting mode listener
     * @return This instance to allow chaining of calls
     */
    fun registerNightLightSettingModeChangeListener(nightLightSettingModeListener: NightLightSettingModeListener): NightLightAppService {
        this.nightLightSettingModeListener = nightLightSettingModeListener

        return this
    }

    /**
     * External units can notify updated night light state through this
     * @param newState New state of night light
     */
    fun notifyUpdatedState(newState: Boolean) {
        if (nightLightStateListener != null) nightLightStateListener!!.onStateChanged(newState)
    }

    /**
     * Notifies other units that setting mode has been changed
     * @param newMode New setting mode
     */
    fun notifyNewSettingMode(newMode: Int) {
        if (nightLightSettingModeListener != null) nightLightSettingModeListener!!.onModeChanged(newMode)
    }

    /**
     * Returns whether app init has been completed or not
     * @return App init status
     */
    fun isInitDone(): Boolean {
        return isInitDone && viewInitCount == TOTAL_VIEWS
    }

    /**
     * Notifies that init has been done
     */
    fun notifyInitDone() {
        isInitDone = true
    }

    /**
     * Increments view init count
     * This notifies that a fragment has completed adding its view
     * Must be called at the end of every fragment's onActivityCreated
     */
    fun incrementViewInitCount() {
        viewInitCount++
    }

    /**
     * Resets view init count
     */
    fun resetViewCount() {
        viewInitCount = 0
    }

    /**
     * Destroys the event listeners
     * This **has to be called in onDestroy() of MainActivity**
     */
    fun destroy() {
        nightLightStateListener = null
        nightLightSettingModeListener = null
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
