package com.corphish.nightlight.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.corphish.nightlight.R
import com.corphish.nightlight.activities.MainActivity
import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.helpers.PreferenceHelper
import com.corphish.nightlight.receivers.ScreenOnOffReceiver
/**
 * Defines the foreground service required by various
 * features of this app to work.
 */
class ForegroundService : Service() {
    // Internal boolean indicating service state
    private var isServiceStarted = false

    // Receivers associated with the service
    private var screenOnOffReceiver: ScreenOnOffReceiver? = null

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("NL_Foreground","onStartCommand executed with startId: $startId")

        if (intent != null) {
            val action = intent.action
            // Log.d("using an intent with action $action")
            when (action) {
                Constants.ACTION_START -> startService()
                Constants.ACTION_STOP -> stopService()
                else -> Log.d("NL_ForegroundService", "This should never happen. No action in the received intent")
            }
        }

        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        val notification = createNotification()
        startForeground(1, notification)
    }

    private fun startService() {
        // No need to start service if it is running
        if (isServiceStarted) {
            return
        }

        isServiceStarted = true
        setServiceState(this, true)

        // we need this lock so our service gets not affected by Doze Mode
        /* wakeLock =
                (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                    newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "EndlessService::lock").apply {
                        acquire()
                    }
                } */

        startScreenOffService()
    }

    private fun stopService() {
        try {
            stopScreenOnOffReceiver()
            stopForeground(true)
            stopSelf()
        } catch (e: Exception) {
            Log.d("NL_Foreground","Service stopped without being started: ${e.message}")
        }

        isServiceStarted = false
        setServiceState(this, false)
    }

    /**
     * Starts the screen on off receiver.
     */
    private fun startScreenOffService() {
        screenOnOffReceiver = ScreenOnOffReceiver()

        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF)
        intentFilter.addAction(Intent.ACTION_USER_PRESENT)

        registerReceiver(screenOnOffReceiver, intentFilter)
    }

    /**
     * Stops the screen on off receiver.
     */
    private fun stopScreenOnOffReceiver() {
        if (screenOnOffReceiver != null) {
            unregisterReceiver(screenOnOffReceiver)
        }
    }

    private fun createNotification(): Notification {
        val notificationChannelId = "ENDLESS SERVICE CHANNEL"

        // depending on the Android API that we're dealing with we will have
        // to use a specific method to create the notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                    notificationChannelId,
                    "Endless Service notifications channel",
                    NotificationManager.IMPORTANCE_HIGH
            ).let {
                it.description = "Endless Service channel"
                it.enableLights(true)
                it.lightColor = Color.RED
                it.enableVibration(true)
                it.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
                it
            }
            notificationManager.createNotificationChannel(channel)
        }

        val pendingIntent: PendingIntent = Intent(this, MainActivity::class.java).let { notificationIntent ->
            PendingIntent.getActivity(this, 0, notificationIntent, 0)
        }

        val builder: Notification.Builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Notification.Builder(
                this,
                notificationChannelId
        ) else Notification.Builder(this)

        return builder
                .setContentTitle("Endless Service")
                .setContentText("This is your favorite endless service working")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("Ticker text")
                .setPriority(Notification.PRIORITY_HIGH) // for under android 26 compatibility
                .build()
    }

    override fun onDestroy() {
        super.onDestroy()

        setServiceState(this, false)
    }

    /**
     * Sets global service status indicator.
     *
     * @param context Context is needed for preference.
     * @param started Running state.
     */
    private fun setServiceState(context: Context, started: Boolean) {
        PreferenceHelper.putBoolean(context, Constants.PREF_SERVICE_STATE, started)
    }
}