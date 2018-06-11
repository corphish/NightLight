package com.corphish.nightlight.services

import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi

import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.helpers.BootUtils
import com.corphish.nightlight.helpers.PreferenceHelper

/**
 * Created by avinabadalal on 13/02/18.
 * Boot Complete service for Android O
 */

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class BootCompleteJobService : JobService() {

    override fun onStopJob(params: JobParameters): Boolean {
        return false
    }

    override fun onStartJob(params: JobParameters): Boolean {
        BootUtils.applyOnBoot(this, { stopSelf() })

        return false
    }

    companion object {

        private val JOB_ID = 1
        private val LATENCY = 1000

        // From https://blog.klinkerapps.com/android-o-background-services/
        fun schedule(context: Context) {
            val delay = PreferenceHelper.getInt(context, Constants.PREF_BOOT_DELAY, Constants.DEFAULT_BOOT_DELAY)

            // Notify that this is set on boot operation
            PreferenceHelper.putBoolean(context, Constants.PREF_BOOT_MODE, true)

            val component = ComponentName(context, BootCompleteJobService::class.java)
            val builder = JobInfo.Builder(JOB_ID, component)
                    // Start with user set (or default) delay
                    // Set deadline to 1 min after delay
                    .setMinimumLatency((delay * LATENCY).toLong())
                    .setOverrideDeadline(((60 + delay) * LATENCY).toLong())

            val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            jobScheduler.schedule(builder.build())
        }
    }
}
