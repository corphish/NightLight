package com.corphish.nightlight.services

import android.app.Service
import android.content.Intent
import android.util.Log
import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.engine.KCALManager
import com.corphish.nightlight.extensions.fromColorTemperatureToRGBIntArray
import com.corphish.nightlight.helpers.PreferenceHelper
import com.corphish.nightlight.helpers.TimeUtils
import kotlin.concurrent.thread

// Top level constants
const val LOG_TAG = "NL_RGBFadeService"
const val SLEEP_MS = 30000L

class RGBFadeService: Service() {
    // Service variables
    private var startTimeMillis = 0L
    private var endTimeMillis = 0L
    private var maxTemp = 0
    private var minTemp = 0
    private var currentTemp = 0

    private val serviceThread = thread(start = false) {
        // Run the stuff only if current time is between specified range
        // Should break out in case of date change and other unexpected stuff
        // Also everything here happens in background thread, so directly use KCALManager abstraction.
        // UI Thread wont be blocked
        while (System.currentTimeMillis() in startTimeMillis..endTimeMillis) {
            // Calculate the temp to be applied, and apply only if calculated temp is different than currentTemp
            // Consider applying KCAL RGB as a costly operation
            // currentTemp needs to be updated every time when calculated temp is different

            // Then sleep for a specific time period and calculate it again
            try {
                Thread.sleep(SLEEP_MS)
            } catch (e: InterruptedException) {}
        }
    }

    override fun onBind(intent: Intent?) = null

    override fun onCreate() {
        super.onCreate()

        Log.i(LOG_TAG, "RGB Fade service started")

        // Initialize service variables
        startTimeMillis = TimeUtils.getTimeAsMSecs(PreferenceHelper.getString(applicationContext, Constants.PREF_START_TIME, Constants.DEFAULT_START_TIME)!!)
        endTimeMillis = TimeUtils.getTimeAsMSecs(PreferenceHelper.getString(applicationContext, Constants.PREF_END_TIME, Constants.DEFAULT_END_TIME)!!)
        maxTemp = PreferenceHelper.getInt(applicationContext, Constants.PREF_MAX_COLOR_TEMP, Constants.DEFAULT_MAX_COLOR_TEMP)
        minTemp = PreferenceHelper.getInt(applicationContext, Constants.PREF_MIN_COLOR_TEMP, Constants.DEFAULT_MIN_COLOR_TEMP)
        currentTemp = maxTemp
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        serviceThread.start()

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.i(LOG_TAG, "RGB Fade service stopped")
        serviceThread.interrupt()
    }
}