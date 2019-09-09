package com.corphish.nightlight.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.corphish.nightlight.data.Constants

import com.corphish.nightlight.helpers.BootUtils
import com.corphish.nightlight.helpers.PreferenceHelper
import com.corphish.nightlight.services.BootCompleteJobService

/**
 * Created by Avinaba on 10/5/2017.
 * Broadcast listener for boot completion
 */

class BootCompleteReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED != intent.action) return
        if (!PreferenceHelper.getBoolean(context, Constants.PREF_SET_ON_BOOT, Constants.DEFAULT_SET_ON_BOOT)) {
            PreferenceHelper.putBoolean(context, Constants.PREF_FORCE_SWITCH, false)

            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            BootCompleteJobService.schedule(context)
        else
            BootUtils.applyOnBoot(context)
    }
}
