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

import com.corphish.nightlight.helpers.BootUtils;

/**
 * Created by avinabadalal on 13/02/18.
 * Boot Complete service for Android O
 */

@RequiresApi(Build.VERSION_CODES.O)
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
        ComponentName component = new ComponentName(context, BootCompleteJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, component)
                // schedule it to run any time between 1 - 5 minutes
                .setMinimumLatency(60*LATENCY)
                .setOverrideDeadline(300 * LATENCY);

        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());
    }
}
