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

    /**
     * Gets last known location
     * Use only when current location is not available
     * @param context Context is required to acquire LocationManager system service
     * @return Returns last known location as android.location.Location
     */
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

    /**
     * Requests the LocationManager system service for current location
     * It needs a locationListener.
     * Once current location is available, it can be accessed from locationListener
     * @param context Context is required to acquire LocationManager system service
     * @param locationListener LocationListener to handle location updated
     */
    public static void requestCurrentLocation(Context context, LocationListener locationListener) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 60, 1000, locationListener);
        } catch (SecurityException | IllegalArgumentException ignored) {}
    }

    /**
     * Checks whether location is stale or not
     * @param location Required location
     * @return A boolean indicating whether location is stale or not
     */
    public static boolean isLocationStale(Location location) {
        return location == null || location.getLatitude() == 0.0 && location.getLongitude() == 0.0;
    }

    /**
     * Checks whether location permissions are available or not
     * @param context Context is required for checking permissions
     * @return A boolean indicating whether location permissions are available or not
     */
    public static boolean areLocationPermissionsAvailable(Context context) {
        int permissionStatus = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);

        return permissionStatus == PackageManager.PERMISSION_GRANTED;
    }
}
