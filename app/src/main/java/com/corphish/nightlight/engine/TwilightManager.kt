package com.corphish.nightlight.engine

import android.content.Context

import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.helpers.PreferenceHelper
import com.corphish.nightlight.helpers.TimeUtils
import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator
import com.luckycatlabs.sunrisesunset.dto.Location

import java.util.Calendar
import java.util.TimeZone

/**
 * Created by Avinaba on 10/26/2017.
 * Sunrise and sunset time manager
 */

class TwilightManager {

    /**
     * Longitude of current location
     */
    private var longitude: Double = 0.toDouble()

    /**
     * Latitude of current location
     */
    private var latitude: Double = 0.toDouble()

    /**
     * Sets current location.
     * Only support setting of co-ordinates directly
     * Any error checks need to be done before calling this
     * @param longitude Longitude of current location
     * @param latitude Latitude of current location
     * @return Current instance
     */
    fun atLocation(longitude: Double, latitude: Double): TwilightManager {
        this.longitude = longitude
        this.latitude = latitude

        return this
    }

    /**
     * Sets current location.
     * Only support setting of co-ordinates directly
     * Any error checks need to be done before calling this
     * @param location double array as {Longitude, Latitude}
     * @return Current instance
     */
    fun atLocation(location: DoubleArray): TwilightManager {
        this.longitude = location[0]
        this.latitude = location[1]

        return this
    }

    /**
     * Computes sunset and sunrise time and saves in the preference
     * @param context Ok where was the shrug emoji again?
     * @param eventFinishCallback Callback for event finish
     * @return Current instance
     */
    fun computeAndSaveTime(context: Context, eventFinishCallback: ((String, String) -> Unit)? = null): TwilightManager {
        val mLocation = Location(latitude, longitude)

        val sunriseSunsetCalculator = SunriseSunsetCalculator(mLocation, TimeZone.getDefault())
        val calendar = Calendar.getInstance()

        val sunsetTime = sunriseSunsetCalculator.getOfficialSunsetForDate(calendar)
        val sunriseTime = sunriseSunsetCalculator.getOfficialSunriseForDate(calendar)

        var darkStartTime = PreferenceHelper.getString(context, Constants.PREF_DARK_HOURS_START, Constants.DEFAULT_START_TIME)

        PreferenceHelper.putString(context, Constants.PREF_START_TIME, sunsetTime)
        PreferenceHelper.putString(context, Constants.PREF_END_TIME, sunriseTime)

        if (!TimeUtils.determineWhetherNLShouldBeOnOrNot(sunsetTime, sunriseTime, darkStartTime)) {
            darkStartTime = sunsetTime
            PreferenceHelper.putString(context, Constants.PREF_DARK_HOURS_START, darkStartTime)
        }

        eventFinishCallback?.invoke(sunsetTime, sunriseTime)

        return this
    }

    /**
     * Computes the times and returns it.
     */
    fun computeAndGet(): Pair<String, String> {
        val mLocation = Location(latitude, longitude)

        val sunriseSunsetCalculator = SunriseSunsetCalculator(mLocation, TimeZone.getDefault())
        val calendar = Calendar.getInstance()

        return sunriseSunsetCalculator.getOfficialSunsetForDate(calendar) to
                sunriseSunsetCalculator.getOfficialSunriseForDate(calendar)
    }

    companion object {

        fun newInstance(): TwilightManager {
            return TwilightManager()
        }
    }
}
