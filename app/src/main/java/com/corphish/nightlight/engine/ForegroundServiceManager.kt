package com.corphish.nightlight.engine

import android.content.Context
import android.content.Intent
import android.os.Build
import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.helpers.PreferenceHelper
import com.corphish.nightlight.services.ForegroundService

/**
 * Object to manage the foreground service.
 */
object ForegroundServiceManager {
    /*
     * List of features that need the foreground service.
     * By design, all the features that needs this service
     * MUST be disabled by default.
     */
    private val foregroundFeatureList = listOf(
            Constants.PREF_DISABLE_IN_LOCK_SCREEN
    )

    /**
     * Starts the foreground service of the app.
     * It will only be started if any features that
     * require the foreground service is enabled bu the user.
     */
    fun startForegroundService(context: Context) {
        // Check if it is necessary to start the service.
        // Check if any feature that needs this is enabled
        // by the user.
        var serviceNecessary = false

        for (pref in foregroundFeatureList) {
            serviceNecessary = serviceNecessary || PreferenceHelper.getBoolean(context, pref, false)
        }

        // Bail out if service not required
        if (!serviceNecessary) {
            return
        }

        // Check if the service is running already or not
        if (PreferenceHelper.getBoolean(context, Constants.PREF_SERVICE_STATE, false)) {
            return
        }

        // Start the service
        Intent(context, ForegroundService::class.java).also {
            it.action = Constants.ACTION_START
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(it)
            } else {
                context.startService(it)
            }
        }
    }
}