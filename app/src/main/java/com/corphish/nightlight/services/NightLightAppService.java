package com.corphish.nightlight.services;

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
     * This night light state listener listens to night light toggle events and performs accordingly
     */
    private NightLightStateListener nightLightStateListener;

    /**
     * This sets the defined nl state listener in this service
     * @param nightLightStateListener Defined night light state listener
     */
    public void setNightLightStateListener(NightLightStateListener nightLightStateListener) {
        this.nightLightStateListener = nightLightStateListener;
    }

    /**
     * To check whether this service is running or not, we must check if the event listeners are null or not
     * Accessing this through @link getInstance will always create the new instance if not already and return it
     * So to check whether this is actually running or not, we check whether event listeners are null or not
     * When the app starts, MainActivity must define the event listener and set it here
     * Other units can then access it through here, including QSService and other receivers
     * In this way, QSService and other external units can make changes in app (UI especially) when the app is running
     * When the app is not running, the event listener is null and thus external units cant make changes as they do not need to
     * @return Whether event listeners are null or not
     */
    public boolean isAppServiceRunning () {
        return nightLightStateListener != null;
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
