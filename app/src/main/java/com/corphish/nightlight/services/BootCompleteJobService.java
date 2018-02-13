package com.corphish.nightlight.services;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.corphish.nightlight.data.Constants;
import com.corphish.nightlight.helpers.BootUtils;
import com.corphish.nightlight.helpers.PreferenceHelper;

/**
 * Created by avinabadalal on 13/02/18.
 * Boot Complete service for Android O
 */

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
public class BootCompleteJobService extends JobService {

    private static final int JOB_ID = 1;
    private static final int LATENCY = 1000;

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i("NL_Boot","Stop job");
        //stopSelf();
        return false;
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i("NL_Boot","Start job");
        BootUtils.applyOnBoot(this, new BootUtils.OnApplyCompleteListener() {
            @Override
            public void onComplete() {
                stopSelf();
            }
        });
        return false;
    }

    // From https://blog.klinkerapps.com/android-o-background-services/
    public static void schedule(Context context) {
        Log.i("NL_Boot","Scheduled job");

        int delay = PreferenceHelper.getInt(context, Constants.PREF_BOOT_DELAY, Constants.DEFAULT_BOOT_DELAY);

        // Notify that this is set on boot operation
        PreferenceHelper.putBoolean(context, Constants.PREF_BOOT_MODE, true);

        ComponentName component = new ComponentName(context, BootCompleteJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, component)
                // Start with user set (or default) delay
                // Set deadline to 1 min after delay
                .setMinimumLatency(delay * LATENCY)
                .setOverrideDeadline((60 + delay) * LATENCY);

        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());
    }
}
