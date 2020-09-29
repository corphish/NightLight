package com.corphish.nightlight.design.fragments

import android.Manifest
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.corphish.nightlight.R
import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.engine.Core
import com.corphish.nightlight.engine.TwilightManager
import com.corphish.nightlight.helpers.AlarmUtils
import com.corphish.nightlight.helpers.LocationUtils
import com.corphish.nightlight.helpers.PreferenceHelper
import com.corphish.nightlight.helpers.TimeUtils

/**
 * This activity provides user the interface to customise the
 * automation capabilities provided by the app.
 */
class AutomationFragment : PreferenceFragmentCompat(), LocationListener {
    // Location variables
    // Location is used to determine sunset/sunrise times.
    private val locationRequestCode = 69
    private var locationPermissionAvailable = false
    private var location: Location? = null

    /**
     * Called during [.onCreate] to supply the preferences for this fragment.
     * Subclasses are expected to call [.setPreferenceScreen] either
     * directly or via helper methods such as [.addPreferencesFromResource].
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state,
     * this is the state.
     * @param rootKey            If non-null, this preference fragment should be rooted at the
     * [PreferenceScreen] with this key.
     */
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.automation_preferences, rootKey)

        initViews()
    }

    /**
     * Associates view click listeners and initializes them.
     */
    private fun initViews() {
        // Handle auto switch changes
        findPreference<SwitchPreferenceCompat>(Constants.PREF_AUTO_SWITCH)?.setOnPreferenceChangeListener { _, newValue ->
            val b = newValue as Boolean

            if (b) {
                doCurrentAutoFunctions(true)
            } else {
                Core.applyNightModeAsync(true, requireContext())
            }

            enableOrDisableAutoSwitchViews(b)

            true
        }

        // Handle sun switch changes
        findPreference<SwitchPreferenceCompat>(Constants.PREF_SUN_SWITCH)?.setOnPreferenceChangeListener { _, newValue ->
            val b = newValue as Boolean

            if (b) {
                doLocationStuff()
            } else {
                val prevStartTime = PreferenceHelper.getString(requireContext(), Constants.PREF_LAST_START_TIME, Constants.DEFAULT_START_TIME)
                val prevEndTime = PreferenceHelper.getString(requireContext(), Constants.PREF_LAST_END_TIME, Constants.DEFAULT_END_TIME)

                findPreference<Preference>(Constants.PREF_START_TIME)?.summary = prevStartTime!!
                findPreference<Preference>(Constants.PREF_END_TIME)?.summary = prevEndTime!!

                PreferenceHelper.putString(requireContext(), Constants.PREF_START_TIME, prevStartTime)
                PreferenceHelper.putString(requireContext(), Constants.PREF_END_TIME, prevEndTime)

                addNextDayIfNecessary(Constants.PREF_END_TIME)
                doCurrentAutoFunctions(true)
            }

            fixDarkHoursStartTime()
            true
        }

        // Set time click listeners
        findPreference<Preference>(Constants.PREF_START_TIME)?.setOnPreferenceClickListener { p ->
            showTimePickerDialog(p, Constants.PREF_START_TIME)

            true
        }
        findPreference<Preference>(Constants.PREF_END_TIME)?.setOnPreferenceClickListener { p ->
            showTimePickerDialog(p, Constants.PREF_END_TIME)

            true
        }

        // Handle dark hours switch click
        findPreference<SwitchPreferenceCompat>(Constants.PREF_DARK_HOURS_ENABLE)?.setOnPreferenceChangeListener { _, _ ->
            fixDarkHoursStartTime()
            doCurrentAutoFunctions(true)
            PreferenceHelper.putBoolean(requireContext(), Constants.PREF_FADE_ENABLED, true)

            true
        }

        // Handle dark start time click
        findPreference<Preference>(Constants.PREF_DARK_HOURS_START)?.setOnPreferenceClickListener { p ->
            showTimePickerDialog(p, Constants.PREF_DARK_HOURS_START)

            true
        }

        findPreference<Preference>(Constants.PREF_START_TIME)?.summary = PreferenceHelper.getString(requireContext(), Constants.PREF_START_TIME, Constants.DEFAULT_START_TIME)
        findPreference<Preference>(Constants.PREF_END_TIME)?.summary = PreferenceHelper.getString(requireContext(), Constants.PREF_END_TIME, Constants.DEFAULT_END_TIME)
        findPreference<Preference>(Constants.PREF_DARK_HOURS_START)?.summary = PreferenceHelper.getString(requireContext(), Constants.PREF_DARK_HOURS_START, Constants.DEFAULT_START_TIME)

        addNextDayIfNecessary(Constants.PREF_END_TIME)
        addNextDayIfNecessary(Constants.PREF_DARK_HOURS_START)
    }

    /**
     * Enable/disable views in this fragment depending on a boolean value
     * @param enabled A boolean indicating whether or not views should be enabled or disabled
     */
    private fun enableOrDisableAutoSwitchViews(enabled: Boolean) {

    }

    /**
     * Shows time picker dialog and handles the time selected by user
     * @param viewWhoIsCallingIt View whose time time needs to be updated after user selects time
     * @param prefKey Preference for the key needed to be updated after user selects time
     */
    private fun showTimePickerDialog(viewWhoIsCallingIt: Preference?, prefKey: String) {
        val time = TimeUtils.currentTimeAsHourAndMinutes
        val timePickerDialog = TimePickerDialog(requireContext(), { _, i, i1 ->
            val selectedHour = if (i < 10) "0$i" else "" + i
            val selectedMinute = if (i1 < 10) "0$i1" else "" + i1
            val timeString = "$selectedHour:$selectedMinute"

            PreferenceHelper.putString(requireContext(), prefKey, timeString)
            // We also backup the time here
            // To get the prefKey for backup, its "last_" + prefKey
            PreferenceHelper.putString(requireContext(), "last_$prefKey", timeString)

            viewWhoIsCallingIt?.summary = timeString

            addNextDayIfNecessary(Constants.PREF_END_TIME)
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
        val start = PreferenceHelper.getString(requireContext(), Constants.PREF_START_TIME, Constants.DEFAULT_START_TIME)
        val end = PreferenceHelper.getString(requireContext(), Constants.PREF_END_TIME, Constants.DEFAULT_END_TIME)
        val test = darkTime
                ?: PreferenceHelper.getString(requireContext(), Constants.PREF_DARK_HOURS_START, Constants.DEFAULT_START_TIME)

        // Unnecessary null checks but enables smart casting
        if (start == null || end == null) return

        val b = TimeUtils.determineWhetherNLShouldBeOnOrNot(start, end, test)

        if (!b) {
            findPreference<Preference>(Constants.PREF_DARK_HOURS_START)?.summary = start
            PreferenceHelper.putString(requireContext(), Constants.PREF_DARK_HOURS_START, start)
            Toast.makeText(requireContext(), R.string.dark_hours_set_error, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Adds localised (Next Day) string if necessary.
     *
     * @param prefKey Preference to change.
     * @param defaultValue Optional parameter to supply default value.
     */
    private fun addNextDayIfNecessary(prefKey: String, defaultValue: String = Constants.DEFAULT_END_TIME) {
        val sStartTime = PreferenceHelper.getString(requireContext(), Constants.PREF_START_TIME, Constants.DEFAULT_START_TIME)

        val sEndTime = PreferenceHelper.getString(requireContext(), prefKey, defaultValue)
        if (TimeUtils.getTimeInMinutes(sEndTime!!) < TimeUtils.getTimeInMinutes(sStartTime!!)) {
            findPreference<Preference>(prefKey)?.summary = "$sEndTime ${getString(R.string.next_day)}"
        }
    }

    /**
     * Does automation functions
     * @param setAlarms Boolean indicating whether or not to set alarms
     */
    private fun doCurrentAutoFunctions(setAlarms: Boolean) {
        val prefStartTime = PreferenceHelper.getString(requireContext(), Constants.PREF_START_TIME, Constants.DEFAULT_START_TIME)
        val prefEndTime = PreferenceHelper.getString(requireContext(), Constants.PREF_END_TIME, Constants.DEFAULT_END_TIME)
        val prefDarkStartTime = PreferenceHelper.getString(requireContext(), Constants.PREF_DARK_HOURS_START, Constants.DEFAULT_START_TIME)
        val darkHoursEnabled = PreferenceHelper.getBoolean(requireContext(), Constants.PREF_DARK_HOURS_ENABLE, false)

        val toEnable = TimeUtils.determineWhetherNLShouldBeOnOrNot(prefStartTime!!, prefEndTime!!)

        val isMinIntensity = TimeUtils.determineWhetherNLShouldBeOnOrNot(prefStartTime, prefDarkStartTime!!)
        val isMaxIntensity = TimeUtils.determineWhetherNLShouldBeOnOrNot(prefDarkStartTime, prefEndTime)

        var intensity: Int? = null

        if (isMinIntensity) {
            intensity = Constants.INTENSITY_TYPE_MINIMUM
            PreferenceHelper.putInt(requireContext(), Constants.PREF_INTENSITY_TYPE, Constants.INTENSITY_TYPE_MINIMUM)
        } else if (isMaxIntensity) {
            intensity = Constants.INTENSITY_TYPE_MAXIMUM
            PreferenceHelper.putInt(requireContext(), Constants.PREF_INTENSITY_TYPE, Constants.INTENSITY_TYPE_MAXIMUM)
        }

        Core.applyNightModeAsync(toEnable, requireContext(), true, if (darkHoursEnabled) intensity else null)

        if (setAlarms) {
            AlarmUtils.setAlarmRelative(requireContext(), 0)
        }
    }

    /**
     * Requests location permission
     */
    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationRequestCode)
    }

    /**
     * Wrapper method to do various location functions
     */
    private fun doLocationStuff() {
        if (locationPermissionAvailable || LocationUtils.areLocationPermissionsAvailable(requireContext()))
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
        val location = LocationUtils.getLastKnownLocation(requireContext())

        if (!LocationUtils.isLocationStale(location))
            getAndSetSunriseSunsetTimings(location)
        else
            LocationUtils.requestCurrentLocation(requireContext(), this)
    }

    /**
     * Gets sunset/sunrise for currentLocation and sets alarms and updates views accordingly
     * @param currentLocation Current location
     */
    private fun getAndSetSunriseSunsetTimings(currentLocation: Location?) {
        if (currentLocation == null) {
            Toast.makeText(requireContext(), getString(R.string.location_unavailable), Toast.LENGTH_LONG).show()
            findPreference<SwitchPreferenceCompat>(Constants.PREF_SUN_SWITCH)?.isChecked = false
            return
        } else {
            // Save location
            PreferenceHelper.putLocation(requireContext(), currentLocation.longitude, currentLocation.latitude)
        }

        TwilightManager.newInstance()
                .atLocation(currentLocation.longitude, currentLocation.latitude)
                .computeAndSaveTime(requireContext()) { a, b ->
                    doCurrentAutoFunctions(false)
                    fixDarkHoursStartTime()

                    findPreference<Preference>(Constants.PREF_START_TIME)?.summary = a
                    findPreference<Preference>(Constants.PREF_END_TIME)?.summary = b
                }

        addNextDayIfNecessary(Constants.PREF_END_TIME)
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
