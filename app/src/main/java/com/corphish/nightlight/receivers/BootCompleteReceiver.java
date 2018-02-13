package com.corphish.nightlight.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.corphish.nightlight.data.Constants;
import com.corphish.nightlight.engine.Core;
import com.corphish.nightlight.engine.TwilightManager;
import com.corphish.nightlight.helpers.AlarmUtils;
import com.corphish.nightlight.helpers.BootUtils;
import com.corphish.nightlight.helpers.PreferenceHelper;
import com.corphish.nightlight.helpers.TimeUtils;
import com.corphish.nightlight.services.BootCompleteJobService;

/**
 * Created by Avinaba on 10/5/2017.
 * Broadcast listener for boot completion
 */

public class BootCompleteReceiver extends BroadcastReceiver {
    private static final String BOOT_COMPLETE_ANDROID_STRING = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("NL_Boot", "BootComplete Signal received");
        if (!intent.getAction().equals(BOOT_COMPLETE_ANDROID_STRING)) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) BootCompleteJobService.schedule(context);
        else BootUtils.applyOnBoot(context);
    }
}
