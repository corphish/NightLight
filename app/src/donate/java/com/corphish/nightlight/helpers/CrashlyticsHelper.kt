package com.corphish.nightlight.helpers

import android.content.Context

import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric

object CrashlyticsHelper {
    fun start(context: Context) {
        Fabric.with(context, Crashlytics())
    }
}