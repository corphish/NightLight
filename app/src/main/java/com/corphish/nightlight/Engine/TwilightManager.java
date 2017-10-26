package com.corphish.nightlight.Engine;

import android.content.Context;

import com.corphish.nightlight.Data.Constants;
import com.corphish.nightlight.Helpers.AlarmUtils;
import com.corphish.nightlight.Helpers.PreferenceHelper;
import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by Avinaba on 10/26/2017.
 * Sunrise and sunset time manager
 */

public class TwilightManager {

    /*
     * Longitude of current location
     */
    private double longitude;

    /*
     * Latitude of current location
     */
    private double latitude;

    public static TwilightManager newInstance() {
        return new TwilightManager();
    }

    /**
     * Sets current location.
     * Only support setting of co-ordinates directly
     * Any error checks need to be done before calling this
     * @param longitude - Longitude of current location
     * @param latitude - Latitude of current location
     * @return - Current instance
     */
    public TwilightManager atLocation(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;

        return this;
    }

    /**
     * Computes sunset and sunrise time and saves in the preference
     * @param context - Ok where was the shrug emoji again?
     * @return - Current instance
     */
    public TwilightManager computeAndSaveTime(Context context) {
        Location mLocation = new Location(latitude, longitude);

        SunriseSunsetCalculator sunriseSunsetCalculator = new SunriseSunsetCalculator(mLocation, TimeZone.getDefault());
        Calendar calendar = Calendar.getInstance();

        String sunsetTime = sunriseSunsetCalculator.getOfficialSunsetForDate(calendar);
        String sunriseTime = sunriseSunsetCalculator.getOfficialSunriseForDate(calendar);

        PreferenceHelper.putTime(context, Constants.PREF_START_TIME, sunsetTime);
        PreferenceHelper.putTime(context, Constants.PREF_END_TIME, sunriseTime);

        AlarmUtils.setAlarms(context, sunsetTime, sunriseTime);

        return this;
    }
}
