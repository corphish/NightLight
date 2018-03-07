package com.corphish.nightlight.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.corphish.nightlight.helpers.BootUtils;
import com.corphish.nightlight.services.BootCompleteJobService;

/**
 * Created by Avinaba on 10/5/2017.
 * Broadcast listener for boot completion
 */

public class BootCompleteReceiver extends BroadcastReceiver {
    private static final String BOOT_COMPLETE_ANDROID_STRING = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (BOOT_COMPLETE_ANDROID_STRING.equals(intent.getAction())) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) BootCompleteJobService.schedule(context);
        else BootUtils.applyOnBoot(context);
    }
}
