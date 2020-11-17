package com.corphish.nightlight.design.fragments

import android.Manifest
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
import com.corphish.nightlight.engine.AutomationRoutineManager
import com.corphish.nightlight.engine.Core
import com.corphish.nightlight.helpers.LocationUtils
import com.corphish.nightlight.helpers.PreferenceHelper
import com.corphish.widgets.ktx.dialogs.OnBoardingDialog

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
                AutomationRoutineManager.apply {
                    loadRoutines(requireContext())
                    scheduleAlarms(requireContext())
                }

                doLocationStuff()
            } else {
                Core.applyNightModeAsync(true, requireContext())
            }

            true
        }

        findPreference<Preference>("reset_location")?.setOnPreferenceClickListener {
            doLocationStuff()
            true
        }

        // Tutorial
        findPreference<Preference>("auto_tutorial")?.setOnPreferenceClickListener {
            OnBoardingDialog(requireActivity()).apply {
                slides = listOf(
                        OnBoardingDialog.Slide(
                                titleResId = R.string.section_auto,
                                messageResId = R.string.automation_intro_1,
                                animation = R.raw.alarm
                        ),
                        OnBoardingDialog.Slide(
                                titleResId = R.string.sunset_sunrise,
                                messageResId = R.string.automation_intro_2,
                                animation = R.raw.day_night
                        ),
                        OnBoardingDialog.Slide(
                                titleResId = R.string.fading_in,
                                messageResId = R.string.automation_intro_3,
                                animation = R.raw.day_night
                        ),
                        OnBoardingDialog.Slide(
                                titleResId = R.string.fade_poll_rate,
                                messageResId = R.string.automation_intro_4,
                                animation = R.raw.time
                        ),
                )
            }.show()

            true
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
        if (locationPermissionAvailable || LocationUtils.areLocationPermissionsAvailable(requireContext())) {
            getBestLocation()
        } else {
            requestLocationPermission()
        }
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

        if (!LocationUtils.isLocationStale(location)) {
            if (location != null) {
                PreferenceHelper.putLocation(requireContext(), location.longitude, location.latitude)
                Toast.makeText(requireContext(), getString(R.string.location_available), Toast.LENGTH_LONG)
                        .show()
            } else {
                Toast.makeText(requireContext(), getString(R.string.location_unavailable), Toast.LENGTH_LONG)
                        .show()
            }
        } else {
            LocationUtils.requestCurrentLocation(requireContext(), this)
        }
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
            PreferenceHelper.putLocation(requireContext(), location.longitude, location.latitude)
        }
    }

    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}
    override fun onStatusChanged(provider: String, status: Int, bundle: Bundle) {}
}
