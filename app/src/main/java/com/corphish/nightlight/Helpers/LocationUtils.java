package com.corphish.nightlight.Helpers;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.content.ContextCompat;

import java.util.List;

/**
 * Created by Avinaba on 10/17/2017.
 * Helper class to deal with locations
 */

public class LocationUtils {

    public static Location getLastKnownLocation(Context context) {
        LocationManager mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;

        if (providers == null || providers.size() == 0) return null;

        try {
            for (String provider : providers) {
                if (provider.equals(LocationManager.PASSIVE_PROVIDER)) continue;
                Location l = mLocationManager.getLastKnownLocation(provider);
                if (l == null) continue;
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) bestLocation = l;
            }
        } catch (SecurityException ignored) {}



        return bestLocation;
    }

    public static void requestCurrentLocation(Context context, LocationListener locationListener) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 60, 1000, locationListener);
        } catch (SecurityException | IllegalArgumentException ignored) {}
    }

    public static boolean isLocationStale(Location location) {
        return location == null || location.getLatitude() == 0.0 && location.getLongitude() == 0.0;
    }

    public static boolean areLocationPermissionsAvailable(Context context) {
        int permissionStatus = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);

        return permissionStatus == PackageManager.PERMISSION_GRANTED;
    }
}
