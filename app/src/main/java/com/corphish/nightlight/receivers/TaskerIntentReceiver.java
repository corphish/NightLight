package com.corphish.nightlight.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by avinabadalal on 03/03/18.
 * Tasker service
 */

public class TaskerIntentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("NL_Tasker","Plugin supported");
    }
}
