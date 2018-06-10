package com.corphish.nightlight.helpers;

import android.content.Context;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class CrashlyticsHelper {
    public static void start(Context context) {
        Fabric.with(context, new Crashlytics());
    }
}