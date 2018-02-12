package com.corphish.nightlight.services;

import com.corphish.nightlight.interfaces.NightLightSettingModeListener;
import com.corphish.nightlight.interfaces.NightLightStateListener;

/**
 * Created by avinabadalal on 30/12/17.
 * Night Light App Service
 * This runs as long as the app is running
 * External events or services can request update on in-app elements (like views) through this service
 * For example, when user is using the app, and it toggles night light from quick settings, then the changes must be updated in the app as well.
 * This is done to make the UX for responsive
 */

public class NightLightAppService {
    private static final NightLightAppService ourInstance = new NightLightAppService();

    /**
     * Gets the global instance of this
     * @return Instance
     */
    public static NightLightAppService getInstance() {
        return ourInstance;
    }

    /**
     * Private constructors ensures the usage of this singleton implementation
     */
    private NightLightAppService() {}

    /**
     * Running status of this service
     */
    private boolean serviceStatus = false;

    /**
     * Starts this service
     */
    public void startService() {
        serviceStatus = true;
    }

    /**
     * This night light state listener listens to night light toggle events and performs accordingly
     */
    private NightLightStateListener nightLightStateListener;

    /**
     * This night light setting mode change listener listens to setting mode changes and requests to make proper changes
     */
    private NightLightSettingModeListener nightLightSettingModeListener;

    /**
     * This sets the defined nl state listener in this service
     * @param nightLightStateListener Defined night light state listener
     */
    public void setNightLightStateListener(NightLightStateListener nightLightStateListener) {
        this.nightLightStateListener = nightLightStateListener;
    }

    /**
     * This sets the defined nl setting mode listener in this service
     * @param nightLightSettingModeListener Defined nl setting mode listener
     */
    public void registerNightLightSettingModeChangeListener(NightLightSettingModeListener nightLightSettingModeListener) {
        this.nightLightSettingModeListener = nightLightSettingModeListener;
    }

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
    public boolean isAppServiceRunning () {
        return serviceStatus;
    }

    /**
     * External units can notify updated night light state through this
     * @param newState New state of night light
     */
    public void notifyUpdatedState(boolean newState) {
        if (nightLightStateListener != null) nightLightStateListener.onStateChanged(newState);
    }

    /**
     * Destroys the event listeners
     * This <strong>has to be called in onDestroy() of MainActivity</strong>
     */
    public void destroy() {
        nightLightStateListener = null;
    }
}
