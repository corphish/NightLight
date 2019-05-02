package com.corphish.nightlight.services

import android.app.Service
import android.content.Intent
import android.util.Log
import com.corphish.nightlight.engine.KCALManager
import com.corphish.nightlight.extensions.fromColorTemperatureToRGBIntArray
import kotlin.concurrent.thread

// Top level constants
const val LOG_TAG = "NL_RGBFadeService"
const val SLEEP_MS = 3000L

class RGBFadeService: Service() {
    private val serviceThread = thread(start = false) {
        // All the background stuff happens here
        // This is just a test
        for (i in 4500 downTo 3000 step 100) {
            Log.i(LOG_TAG, "Temperature = $i")
            KCALManager.updateKCALValues(i.fromColorTemperatureToRGBIntArray())
            Thread.sleep(SLEEP_MS)
        }
    }

    override fun onBind(intent: Intent?) = null

    override fun onCreate() {
        super.onCreate()

        Log.i(LOG_TAG, "RGB Fade service started")
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