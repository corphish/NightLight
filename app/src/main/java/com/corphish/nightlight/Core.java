package com.corphish.nightlight;

import com.corphish.nightlight.Helpers.RootUtils;

/**
 * Created by Avinaba on 10/4/2017.
 * Basic functions of the app
 */

public class Core {
    private static void enableNightMode(int intensity) {
        RootUtils.writeToFile("1", Constants.KCAL_SWITCH);
        RootUtils.writeToFile("256 256 "+(192 - intensity),Constants.KCAL_ADJUST);
    }

    private static void disableNightMode() {
        RootUtils.writeToFile("256 256 256",Constants.KCAL_ADJUST);
    }

    static void applyNightMode(boolean e, int intensity) {
        if (e) enableNightMode(intensity);
        else disableNightMode();
    }
}
