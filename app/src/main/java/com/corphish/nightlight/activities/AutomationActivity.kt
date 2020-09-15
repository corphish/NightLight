package com.corphish.nightlight.activities

import android.Manifest
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.corphish.nightlight.R
import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.design.ThemeUtils
import com.corphish.nightlight.engine.Core
import com.corphish.nightlight.engine.TwilightManager
import com.corphish.nightlight.helpers.AlarmUtils
import com.corphish.nightlight.helpers.LocationUtils
import com.corphish.nightlight.helpers.PreferenceHelper
import com.corphish.nightlight.helpers.TimeUtils
import com.corphish.widgets.ktx.KeyValueView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_automation.*
import kotlinx.android.synthetic.main.layout_header.*

/**
 * This activity provides user the interface to customise the
 * automation capabilities provided by the app.
 */
class AutomationActivity : AppCompatActivity(), LocationListener {

    // We have 3 switches:
    // 1. Auto switch - Master switch to enable the automation.
    // 2. Sun switch - Switch to toggle whether sunset/sunrise times are
    //    used or not.
    // 3. Dark switch - Switch to enable or disable dark hours.
    private var autoSwitchStatus = false
    private var sunSwitchStatus = false
    private var darkHoursEnabled = false

    // Location variables
    // Location is used to determine sunset/sunrise times.
    private val locationRequestCode = 69
    private var locationPermissionAvailable = false
    private var location: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Apply theme based on user selection
        setTheme(ThemeUtils.getAppTheme(this))
        setContentView(R.layout.activity_automation)

        // Populate header
        banner_title.text = getString(R.string.section_auto)
        banner_icon.setImageResource(R.drawable.ic_alarm)

        // Initialize switch values from Preference.
        autoSwitchStatus = PreferenceHelper.getBoolean(this, Constants.PREF_AUTO_SWITCH)
        sunSwitchStatus = PreferenceHelper.getBoolean(this, Constants.PREF_SUN_SWITCH)
        darkHoursEnabled = PreferenceHelper.getBoolean(this, Constants.PREF_DARK_HOURS_ENABLE)

        // Finally init views
        initViews()
    }

    /**
     * Associates view click listeners and initializes them.
     */
    private fun initViews() {
        // Handle auto switch changes
        autoEnable.setOnCheckedChangeListener { _, b ->
            autoSwitchStatus = b

            if (b) {
                doCurrentAutoFunctions(true)
            } else {
                Core.applyNightModeAsync(true, this)
            }

            PreferenceHelper.putBoolean(this, Constants.PREF_AUTO_SWITCH, b)
            enableOrDisableAutoSwitchViews(b)
        }

        // Handle sun switch changes
        sunEnable.setOnCheckedChangeListener { _, b ->
            PreferenceHelper.putBoolean(this, Constants.PREF_SUN_SWITCH, b)
            sunSwitchStatus = b

            if (b) {
                doLocationStuff()
            } else {
                val prevStartTime = PreferenceHelper.getString(this, Constants.PREF_LAST_START_TIME, Constants.DEFAULT_START_TIME)
                val prevEndTime = PreferenceHelper.getString(this, Constants.PREF_LAST_END_TIME, Constants.DEFAULT_END_TIME)

                startTime.valueText = prevStartTime!!
                endTime.valueText = prevEndTime!!

                PreferenceHelper.putString(this, Constants.PREF_START_TIME, prevStartTime)
                PreferenceHelper.putString(this, Constants.PREF_END_TIME, prevEndTime)

                addNextDayIfNecessary()
                doCurrentAutoFunctions(true)
            }

            fixDarkHoursStartTime()
            enableOrDisableAutoSwitchViews(autoEnable.isChecked)
        }

        // Set time click listeners
        startTime.setOnClickListener { showTimePickerDialog(startTime, Constants.PREF_START_TIME) }
        endTime.setOnClickListener { showTimePickerDialog(endTime, Constants.PREF_END_TIME) }

        // Handle dark hours switch click
        darkHoursEnable.setOnCheckedChangeListener { _, b ->
            darkHoursEnabled = b
            PreferenceHelper.putBoolean(this, Constants.PREF_DARK_HOURS_ENABLE, b)
            enableOrDisableAutoSwitchViews(autoSwitchStatus)
            fixDarkHoursStartTime()
        }

        // Handle dark start time click
        darkStartTime.setOnClickListener { showTimePickerDialog(darkStartTime, Constants.PREF_DARK_HOURS_START) }

        autoEnable.isChecked = autoSwitchStatus
        sunEnable.isChecked = sunSwitchStatus

        startTime.valueText = PreferenceHelper.getString(this, Constants.PREF_START_TIME, Constants.DEFAULT_START_TIME)!!
        endTime.valueText = PreferenceHelper.getString(this, Constants.PREF_END_TIME, Constants.DEFAULT_END_TIME)!!

        addNextDayIfNecessary()

        darkHoursEnable.isChecked = darkHoursEnabled
        darkStartTime.valueText = PreferenceHelper.getString(this, Constants.PREF_DARK_HOURS_START, Constants.DEFAULT_START_TIME)!!

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
        val timePickerDialog = TimePickerDialog(this, { _, i, i1 ->
            val selectedHour = if (i < 10) "0$i" else "" + i
            val selectedMinute = if (i1 < 10) "0$i1" else "" + i1
            val timeString = "$selectedHour:$selectedMinute"

            PreferenceHelper.putString(this, prefKey, timeString)
            // We also backup the time here
            // To get the prefKey for backup, its "last_" + prefKey
            PreferenceHelper.putString(this, "last_$prefKey", timeString)

            viewWhoIsCallingIt!!.valueText = timeString

            addNextDayIfNecessary()
            fixDarkHoursStartTime()

            doCurrentAutoFunctions(true)
        }, time[0], time[1], false)
        timePickerDialog.show()
    }

    /**
     * This method fixes dark start time by checking if the input
     * time is within the schedule or not.
     *
     * @param darkTime Dark start time as entered by user.
     */
    private fun fixDarkHoursStartTime(darkTime: String? = null) {
        val start = PreferenceHelper.getString(this, Constants.PREF_START_TIME, Constants.DEFAULT_START_TIME)
        val end = PreferenceHelper.getString(this, Constants.PREF_END_TIME, Constants.DEFAULT_END_TIME)
        val test = darkTime
                ?: PreferenceHelper.getString(this, Constants.PREF_DARK_HOURS_START, Constants.DEFAULT_START_TIME)

        // Unnecessary null checks but enables smart casting
        if (start == null || end == null) return

        val b = TimeUtils.determineWhetherNLShouldBeOnOrNot(start, end, test)

        if (!b) {
            darkStartTime.valueText = start
            PreferenceHelper.putString(this, Constants.PREF_DARK_HOURS_START, start)
            Toast.makeText(this, R.string.dark_hours_set_error, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Adds localised (Next Day) string if necessary
     */
    private fun addNextDayIfNecessary() {
        val sStartTime = PreferenceHelper.getString(this, Constants.PREF_START_TIME, Constants.DEFAULT_START_TIME)
        val sEndTime = PreferenceHelper.getString(this, Constants.PREF_END_TIME, Constants.DEFAULT_END_TIME)
        if (TimeUtils.getTimeInMinutes(sEndTime!!) < TimeUtils.getTimeInMinutes(sStartTime!!))
            endTime.valueText = "$sEndTime + ${getString(R.string.next_day)}"
    }

    /**
     * Does automation functions
     * @param setAlarms Boolean indicating whether or not to set alarms
     */
    private fun doCurrentAutoFunctions(setAlarms: Boolean) {
        val prefStartTime = PreferenceHelper.getString(this, Constants.PREF_START_TIME, Constants.DEFAULT_START_TIME)
        val prefEndTime = PreferenceHelper.getString(this, Constants.PREF_END_TIME, Constants.DEFAULT_END_TIME)
        val prefDarkStartTime = PreferenceHelper.getString(this, Constants.PREF_DARK_HOURS_START, Constants.DEFAULT_START_TIME)

        val toEnable = TimeUtils.determineWhetherNLShouldBeOnOrNot(prefStartTime!!, prefEndTime!!)

        val isMinIntensity = TimeUtils.determineWhetherNLShouldBeOnOrNot(prefStartTime, prefDarkStartTime!!)
        val isMaxIntensity = TimeUtils.determineWhetherNLShouldBeOnOrNot(prefDarkStartTime, prefEndTime)

        var intensity: Int? = null

        if (isMinIntensity) {
            intensity = Constants.INTENSITY_TYPE_MINIMUM
            PreferenceHelper.putInt(this, Constants.PREF_INTENSITY_TYPE, Constants.INTENSITY_TYPE_MINIMUM)
        } else if (isMaxIntensity) {
            intensity = Constants.INTENSITY_TYPE_MAXIMUM
            PreferenceHelper.putInt(this, Constants.PREF_INTENSITY_TYPE, Constants.INTENSITY_TYPE_MAXIMUM)
        }

        Core.applyNightModeAsync(toEnable, this, true, if (darkHoursEnabled) intensity else null)

        if (setAlarms) AlarmUtils.setAlarms(this, prefStartTime, prefEndTime, darkHoursEnabled, prefDarkStartTime, true)
    }

    /**
     * Requests location permission
     */
    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationRequestCode)
    }

    /**
     * Wrapper method to do various location functions
     */
    private fun doLocationStuff() {
        if (locationPermissionAvailable || LocationUtils.areLocationPermissionsAvailable(this))
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
        val location = LocationUtils.getLastKnownLocation(this)

        if (!LocationUtils.isLocationStale(location))
            getAndSetSunriseSunsetTimings(location)
        else
            LocationUtils.requestCurrentLocation(this, this)
    }

    /**
     * Gets sunset/sunrise for currentLocation and sets alarms and updates views accordingly
     * @param currentLocation Current location
     */
    private fun getAndSetSunriseSunsetTimings(currentLocation: Location?) {
        if (currentLocation == null) {
            Snackbar.make(findViewById(R.id.layout_container), getString(R.string.location_unavailable), Snackbar.LENGTH_LONG).show()
            sunEnable.isChecked = false
            return
        } else {
            // Save location
            PreferenceHelper.putLocation(this, currentLocation.longitude, currentLocation.latitude)
        }

        TwilightManager.newInstance()
                .atLocation(currentLocation.longitude, currentLocation.latitude)
                .computeAndSaveTime(this) {
                    doCurrentAutoFunctions(false)

                    startTime.valueText = PreferenceHelper.getString(this, Constants.PREF_START_TIME, Constants.DEFAULT_START_TIME)!!
                    endTime.valueText = PreferenceHelper.getString(this, Constants.PREF_END_TIME, Constants.DEFAULT_END_TIME)!!
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
