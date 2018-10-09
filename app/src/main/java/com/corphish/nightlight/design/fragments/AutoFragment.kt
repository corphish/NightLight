package com.corphish.nightlight.design.fragments

import android.Manifest
import android.location.LocationListener
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.core.app.ActivityCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import kotlinx.android.synthetic.main.layout_automation.*

import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.engine.Core
import com.corphish.nightlight.engine.TwilightManager
import com.corphish.nightlight.helpers.AlarmUtils
import com.corphish.nightlight.helpers.LocationUtils
import com.corphish.nightlight.helpers.PreferenceHelper
import com.corphish.nightlight.helpers.TimeUtils
import com.corphish.nightlight.R
import com.corphish.nightlight.design.utils.FontUtils
import com.corphish.widgets.KeyValueView
import com.google.android.material.snackbar.Snackbar

/**
 * Created by Avinaba on 10/24/2017.
 * Auto related fragment
 */

class AutoFragment : BaseBottomSheetDialogFragment(), LocationListener {
    private var sunSwitchStatus: Boolean = false
    private var autoSwitchStatus: Boolean = false
    private var darkHoursEnabled = false

    private val locationRequestCode = 69
    private var locationPermissionAvailable = false

    private var location: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        autoSwitchStatus = PreferenceHelper.getBoolean(context, Constants.PREF_AUTO_SWITCH)
        sunSwitchStatus = PreferenceHelper.getBoolean(context, Constants.PREF_SUN_SWITCH)
        darkHoursEnabled = PreferenceHelper.getBoolean(context, Constants.PREF_DARK_HOURS_ENABLE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.layout_automation, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        FontUtils().setCustomFont(context!!, autoEnable, sunEnable)

        autoEnable.setOnCheckedChangeListener { _, b ->
            if (b)
                doCurrentAutoFunctions(true)
            else {
                Core.applyNightModeAsync(true, context)
            }

            PreferenceHelper.putBoolean(context, Constants.PREF_AUTO_SWITCH, b)

            enableOrDisableAutoSwitchViews(b)
        }

        sunEnable.setOnCheckedChangeListener { _, b ->
            PreferenceHelper.putBoolean(context, Constants.PREF_SUN_SWITCH, b)
            if (b) {
                doLocationStuff()
            } else {
                val prevStartTime = PreferenceHelper.getString(context, Constants.PREF_LAST_START_TIME, Constants.DEFAULT_START_TIME)
                val prevEndTime = PreferenceHelper.getString(context, Constants.PREF_LAST_END_TIME, Constants.DEFAULT_END_TIME)

                startTime.setValueText(prevStartTime!!)
                endTime.setValueText(prevEndTime!!)

                PreferenceHelper.putString(context, Constants.PREF_START_TIME, prevStartTime)
                PreferenceHelper.putString(context, Constants.PREF_END_TIME, prevEndTime)

                addNextDayIfNecessary()
                doCurrentAutoFunctions(true)
            }
            enableOrDisableAutoSwitchViews(autoEnable.isChecked)
        }
        autoEnable.isChecked = autoSwitchStatus
        sunEnable.isChecked = sunSwitchStatus

        startTime.setOnClickListener { showTimePickerDialog(startTime, Constants.PREF_START_TIME) }
        endTime.setOnClickListener { showTimePickerDialog(endTime, Constants.PREF_END_TIME) }

        startTime.setValueText(PreferenceHelper.getString(context, Constants.PREF_START_TIME, Constants.DEFAULT_START_TIME)!!)
        endTime.setValueText(PreferenceHelper.getString(context, Constants.PREF_END_TIME, Constants.DEFAULT_END_TIME)!!)

        addNextDayIfNecessary()

        darkHoursEnable.setOnCheckedChangeListener { _, b ->
            darkHoursEnabled = b
            PreferenceHelper.putBoolean(context, Constants.PREF_DARK_HOURS_ENABLE, b)
            enableOrDisableAutoSwitchViews(autoSwitchStatus)
        }

        darkHoursEnable.isChecked = darkHoursEnabled

        darkStartTime.setOnClickListener { showTimePickerDialog(darkStartTime, Constants.PREF_DARK_HOURS_START) }
        darkStartTime.setValueText(PreferenceHelper.getString(context, Constants.PREF_DARK_HOURS_START, Constants.DEFAULT_START_TIME)!!)

        enableOrDisableAutoSwitchViews(autoSwitchStatus)
    }

    /**
     * Enable/disable views in this fragment depending on a boolean value
     * @param enabled A boolean indicating whether or not views should be enabled or disabled
     */
    private fun enableOrDisableAutoSwitchViews(enabled: Boolean) {
        // Enabled = Status of autoSwitch if masterSwitch is on, otherwise status of masterSwitch
        val sunSwitchEnabled = sunEnable.isChecked

        // If auto switch is off, or master switch off, turn them off all
        if (!enabled) {
            startTime.isEnabled = false
            endTime.isEnabled = false
            sunEnable.isEnabled = false
            darkHoursEnable.isEnabled = false
            darkStartTime.isEnabled = false
        } else {
            // autoSwitch is enabled, enable sunSwitch
            sunEnable.isEnabled = true
            darkHoursEnable.isEnabled = true

            // if sunSwitch is enabled, disable kvviews
            startTime.isEnabled = !sunSwitchEnabled
            endTime.isEnabled = !sunSwitchEnabled
            darkStartTime.isEnabled = darkHoursEnabled
        }
    }

    /**
     * Shows time picker dialog and handles the time selected by user
     * @param viewWhoIsCallingIt View whose time time needs to be updated after user selects time
     * @param prefKey Preference for the key needed to be updated after user selects time
     */
    private fun showTimePickerDialog(viewWhoIsCallingIt: KeyValueView?, prefKey: String) {
        val time = TimeUtils.currentTimeAsHourAndMinutes
        val timePickerDialog = TimePickerDialog(context, TimePickerDialog.OnTimeSetListener { _, i, i1 ->
            val selectedHour = if (i < 10) "0$i" else "" + i
            val selectedMinute = if (i1 < 10) "0$i1" else "" + i1
            val timeString = "$selectedHour:$selectedMinute"

            PreferenceHelper.putString(context, prefKey, timeString)
            // We also backup the time here
            // To get the prefKey for backup, its "last_" + prefKey
            PreferenceHelper.putString(context, "last_$prefKey", timeString)

            viewWhoIsCallingIt!!.setValueText(timeString)

            addNextDayIfNecessary()
            fixDarkHoursStartTime()

            doCurrentAutoFunctions(true)
        }, time[0], time[1], false)
        timePickerDialog.show()
    }

    private fun fixDarkHoursStartTime() {
        val nextDayString = getString(R.string.next_day)
        val start = startTime.valueTextView.text.toString().replace(nextDayString, "")
        val end = endTime.valueTextView.text.toString().replace(nextDayString, "")
        val test = darkStartTime.valueTextView.text.toString().replace(nextDayString, "")
        val b = TimeUtils.determineWhetherNLShouldBeOnOrNot(start, end, test)
        if (!b) {
            darkStartTime.setValueText(start)
            PreferenceHelper.putString(context, Constants.PREF_DARK_HOURS_START, start)
            Toast.makeText(context, R.string.dark_hours_set_error, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Adds localised (Next Day) string if necessary
     */
    private fun addNextDayIfNecessary() {
        if (activity == null || !isAdded || isDetached) return
        val sStartTime = PreferenceHelper.getString(context, Constants.PREF_START_TIME, Constants.DEFAULT_START_TIME)
        val sEndTime = PreferenceHelper.getString(context, Constants.PREF_END_TIME, Constants.DEFAULT_END_TIME)
        if (TimeUtils.getTimeInMinutes(sEndTime!!) < TimeUtils.getTimeInMinutes(sStartTime!!))
            endTime.setValueText(sEndTime + getString(R.string.next_day))
    }

    /**
     * Does automation functions
     * @param setAlarms Boolean indicating whether or not to set alarms
     */
    private fun doCurrentAutoFunctions(setAlarms: Boolean) {
        val prefStartTime = PreferenceHelper.getString(context, Constants.PREF_START_TIME, Constants.DEFAULT_START_TIME)
        val prefEndTime = PreferenceHelper.getString(context, Constants.PREF_END_TIME, Constants.DEFAULT_END_TIME)
        val prefDarkStartTime = PreferenceHelper.getString(context, Constants.PREF_DARK_HOURS_START, Constants.DEFAULT_START_TIME)

        val toEnable = TimeUtils.determineWhetherNLShouldBeOnOrNot(prefStartTime!!, prefEndTime!!)

        val isMinIntensity = TimeUtils.determineWhetherNLShouldBeOnOrNot(prefStartTime, prefDarkStartTime!!)
        val isMaxIntensity = TimeUtils.determineWhetherNLShouldBeOnOrNot(prefDarkStartTime, prefEndTime)

        var intensity: Int? = null

        if (isMinIntensity) {
            intensity = Constants.INTENSITY_TYPE_MINIMUM
            PreferenceHelper.putInt(context, Constants.PREF_INTENSITY_TYPE, Constants.INTENSITY_TYPE_MINIMUM)
        } else if (isMaxIntensity) {
            intensity = Constants.INTENSITY_TYPE_MAXIMUM
            PreferenceHelper.putInt(context, Constants.PREF_INTENSITY_TYPE, Constants.INTENSITY_TYPE_MAXIMUM)
        }

        Core.applyNightModeAsync(toEnable, context, true, if (darkHoursEnabled) intensity else null)

        if (setAlarms) AlarmUtils.setAlarms(context!!, prefStartTime, prefEndTime, true)
    }

    /**
     * Requests location permission
     */
    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationRequestCode)
    }

    /**
     * Wrapper method to do various location functions
     */
    private fun doLocationStuff() {
        if (locationPermissionAvailable || LocationUtils.areLocationPermissionsAvailable(context!!))
            getBestLocation()
        else
            requestLocationPermission()
    }

    /**
     * Gets best location which is needed to determine sunset and sunrise timings
     * It first gets last known location. It does so because getting last known location is faster than getting current location
     * If the last known location is inappropriate, it uses current location
     * Then it gets and sets sunset/sunrise timings.
     */
    private fun getBestLocation() {
        // Try to get best last known location
        val location = LocationUtils.getLastKnownLocation(context!!)

        if (!LocationUtils.isLocationStale(location))
            getAndSetSunriseSunsetTimings(location)
        else
            LocationUtils.requestCurrentLocation(context!!, this)
    }

    /**
     * Gets sunset/sunrise for currentLocation and sets alarms and updates views accordingly
     * @param currentLocation Current location
     */
    private fun getAndSetSunriseSunsetTimings(currentLocation: Location?) {
        if (currentLocation == null) {
            Snackbar.make(activity!!.findViewById(R.id.layout_container), getString(R.string.location_unavailable), Snackbar.LENGTH_LONG).show()
            sunEnable.isChecked = false
            return
        } else {
            // Save location
            PreferenceHelper.putLocation(context, currentLocation.longitude, currentLocation.latitude)
        }

        TwilightManager.newInstance()
                .atLocation(currentLocation.longitude, currentLocation.latitude)
                .computeAndSaveTime(context!!) {
                        doCurrentAutoFunctions(false)

                        startTime.setValueText(PreferenceHelper.getString(context, Constants.PREF_START_TIME, Constants.DEFAULT_START_TIME)!!)
                        endTime.setValueText(PreferenceHelper.getString(context, Constants.PREF_END_TIME, Constants.DEFAULT_END_TIME)!!)
                }

        addNextDayIfNecessary()
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == locationRequestCode) {
            for (i in permissions.indices) {
                if (permissions[i] == Manifest.permission.ACCESS_FINE_LOCATION && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionAvailable = true
                    getBestLocation()
                    break
                }
            }
        }

    }

    override fun onLocationChanged(location: Location) {
        if (this.location == null) {
            this.location = location
            getAndSetSunriseSunsetTimings(location)
        }
    }

    override fun onProviderEnabled(provider: String) {}

    override fun onProviderDisabled(provider: String) {}

    override fun onStatusChanged(provider: String, status: Int, bundle: Bundle) {}
}
