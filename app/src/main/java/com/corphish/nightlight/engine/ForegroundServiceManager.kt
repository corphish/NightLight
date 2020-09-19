package com.corphish.nightlight.engine

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
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
     *
     * @param context Context.
     * @param startedService There is a race condition when the new preference value is actually
     *                       committed and this method is called. So we need the key whose change
     *                       caused this method to be called so that we can ignore this key while
     *                       checking.
     */
    fun startForegroundService(context: Context, startedService: String? = null) {
        // Check if it is necessary to start the service.
        // Check if any feature that needs this is enabled
        // by the user.
        var serviceNecessary = false

        for (pref in foregroundFeatureList) {
            if (startedService == pref) {
                continue
            }

            serviceNecessary = serviceNecessary || PreferenceHelper.getBoolean(context, pref, false)
        }

        // Bail out if service not required
        if (!serviceNecessary) {
            Log.e("NL_FSM", "Not starting service as it is not needed")
            return
        }

        // Check if the service is running already or not
        if (PreferenceHelper.getBoolean(context, Constants.PREF_SERVICE_STATE, false)) {
            Log.e("NL_FSM", "Not starting service as it is running")
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

    /**
     * Stops the foreground service.
     * Will stop only if all the features depending on the service are off.
     *
     * @param context Context.
     * @param stoppedService There is a race condition when the new preference value is actually
     *                       committed and this method is called. So we need the key whose change
     *                       caused this method to be called so that we can ignore this key while
     *                       checking.
     */
    private fun stopForegroundService(context: Context, stoppedService: String? = null) {
        // Check if it is necessary to stop the service.
        // Check if any feature that needs this is enabled
        // by the user.
        var serviceNecessary = false

        for (pref in foregroundFeatureList) {
            if (pref == stoppedService) {
                continue
            }
            serviceNecessary = serviceNecessary || PreferenceHelper.getBoolean(context, pref, false)
        }

        // Bail out if service is needed to be run
        if (serviceNecessary) {
            Log.e("NL_FSM", "Not stopping service as it is needed")
            return
        }

        // Check if the service is running already or not
        if (!PreferenceHelper.getBoolean(context, Constants.PREF_SERVICE_STATE, false)) {
            Log.e("NL_FSM", "Not not stopped because it is not running?")
            return
        }

        // Start the service
        Intent(context, ForegroundService::class.java).also {
            it.action = Constants.ACTION_STOP
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(it)
            } else {
                context.startService(it)
            }
        }
    }

    /**
     * Driver method to manage service.
     *
     * @param context Context.
     * @param toStart Boolean indicating whether to start or stop the service.
     */
    fun manageService(context: Context, toStart: Boolean) {
        if (toStart) {
            startForegroundService(context)
        } else {
            stopForegroundService(context)
        }
    }

    /**
     * Force starts the service.
     *
     * @param context Context.
     */
    fun forceStart(context: Context) {
        Intent(context, ForegroundService::class.java).also {
            it.action = Constants.ACTION_START
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(it)
            } else {
                context.startService(it)
            }
        }
    }

    /**
     * Force stops the service.
     *
     * @param context Context.
     */
    fun forceStop(context: Context) {
        Intent(context, ForegroundService::class.java).also {
            it.action = Constants.ACTION_STOP
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(it)
            } else {
                context.startService(it)
            }
        }
    }
}