package com.corphish.nightlight.Helpers;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.content.ContextCompat;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Avinaba on 10/17/2017.
 * Helper class to deal with locations
 */

public class LocationUtils {

    public static Location getLastKnownLocation(Context context) {
        LocationManager mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        try {
            for (String provider : providers) {
                Location l = mLocationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    // Found best last known location: %s", l);
                    bestLocation = l;
                }
            }
        } catch (SecurityException ignored) {}
        return bestLocation;
    }

    public static boolean areLocationPermissionsAvailable(Context context) {
        int permissionStatus = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);

        return permissionStatus == PackageManager.PERMISSION_GRANTED;
    }

    public static String getSunriseTime(Location location) {
        com.luckycatlabs.sunrisesunset.dto.Location mlocation = new com.luckycatlabs.sunrisesunset.dto.Location(location.getLatitude(), location.getLongitude());
        SunriseSunsetCalculator sunriseSunsetCalculator = new SunriseSunsetCalculator(mlocation, TimeZone.getDefault());

        return sunriseSunsetCalculator.getOfficialSunriseForDate(Calendar.getInstance());
    }

    public static String getSunsetTime(Location location) {
        com.luckycatlabs.sunrisesunset.dto.Location mlocation = new com.luckycatlabs.sunrisesunset.dto.Location(location.getLatitude(), location.getLongitude());
        SunriseSunsetCalculator sunriseSunsetCalculator = new SunriseSunsetCalculator(mlocation, TimeZone.getDefault());

        return sunriseSunsetCalculator.getOfficialSunsetForDate(Calendar.getInstance());
    }
}
