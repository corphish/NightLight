package com.corphish.nightlight.helpers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.support.v4.content.ContextCompat

/**
 * Created by Avinaba on 10/17/2017.
 * Helper class to deal with locations
 */

object LocationUtils {

    /**
     * Gets last known location
     * Use only when current location is not available
     * @param context Context is required to acquire LocationManager system service
     * @return Returns last known location as android.location.Location
     */
    fun getLastKnownLocation(context: Context): Location? {
        val mLocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providers = mLocationManager.getProviders(true)
        var bestLocation: Location? = null

        if (providers == null || providers.size == 0) return null

        try {
            for (provider in providers) {
                if (provider == LocationManager.PASSIVE_PROVIDER) continue
                val l = mLocationManager.getLastKnownLocation(provider) ?: continue
                if (bestLocation == null || l.accuracy < bestLocation.accuracy) bestLocation = l
            }
        } catch (ignored: SecurityException) {
        }



        return bestLocation
    }

    /**
     * Requests the LocationManager system service for current location
     * It needs a locationListener.
     * Once current location is available, it can be accessed from locationListener
     * @param context Context is required to acquire LocationManager system service
     * @param locationListener LocationListener to handle location updated
     */
    fun requestCurrentLocation(context: Context, locationListener: LocationListener) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, (1000 * 60).toLong(), 1000f, locationListener)
        } catch (ignored: SecurityException) {
        } catch (ignored: IllegalArgumentException) {}

    }

    /**
     * Checks whether location is stale or not
     * @param location Required location
     * @return A boolean indicating whether location is stale or not
     */
    fun isLocationStale(location: Location?): Boolean {
        return location == null || location.latitude == 0.0 && location.longitude == 0.0
    }

    /**
     * Checks whether location permissions are available or not
     * @param context Context is required for checking permissions
     * @return A boolean indicating whether location permissions are available or not
     */
    fun areLocationPermissionsAvailable(context: Context): Boolean {
        val permissionStatus = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)

        return permissionStatus == PackageManager.PERMISSION_GRANTED
    }
}
