package com.corphish.nightlight.UI.Fragments;

import android.Manifest;
import android.location.LocationListener;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TimePicker;

import com.corphish.nightlight.Data.Constants;
import com.corphish.nightlight.Engine.Core;
import com.corphish.nightlight.Engine.TwilightManager;
import com.corphish.nightlight.Helpers.AlarmUtils;
import com.corphish.nightlight.Helpers.LocationUtils;
import com.corphish.nightlight.Helpers.PreferenceHelper;
import com.corphish.nightlight.Helpers.TimeUtils;
import com.corphish.nightlight.R;
import com.corphish.nightlight.UI.Widgets.KeyValueView;

/**
 * Created by Avinaba on 10/24/2017.
 * Auto related fragment
 */

public class AutoFragment extends Fragment implements LocationListener {

    private SwitchCompat autoSwitch, sunSwitch;
    private KeyValueView startTimeKV, endTimeKV;
    private boolean sunSwitchStatus, autoSwitchStatus;
    private Context context;

    private final int locationRequestCode = 69;
    private boolean locationPermissionAvailable = false;

    Location location = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();

        autoSwitchStatus = PreferenceHelper.getBoolean(context, Constants.PREF_AUTO_SWITCH);
        sunSwitchStatus = PreferenceHelper.getBoolean(context, Constants.PREF_SUN_SWITCH);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.card_auto_enable, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        autoSwitch = getView().findViewById(R.id.auto_enable);
        sunSwitch = getView().findViewById(R.id.sun_enable);

        startTimeKV = getView().findViewById(R.id.start_time);
        endTimeKV = getView().findViewById(R.id.end_time);

        autoSwitch.setChecked(autoSwitchStatus);
        sunSwitch.setChecked(sunSwitchStatus);

        autoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) doCurrentAutoFunctions(true);
                else Core.applyNightModeAsync(true);

                PreferenceHelper.putBoolean(context, Constants.PREF_AUTO_SWITCH, b);

                enableOrDisableAutoSwitchViews(b);
            }
        });

        sunSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                PreferenceHelper.putBoolean(context, Constants.PREF_SUN_SWITCH, b);
                if (b) {
                    // Backup current timings
                    PreferenceHelper.putString(context, Constants.PREF_LAST_START_TIME, PreferenceHelper.getString(context, Constants.PREF_START_TIME, Constants.DEFAULT_START_TIME));
                    PreferenceHelper.putString(context, Constants.PREF_LAST_END_TIME, PreferenceHelper.getString(context, Constants.PREF_END_TIME, Constants.DEFAULT_END_TIME));

                    doLocationStuff();
                } else {
                    String prevStartTime = PreferenceHelper.getString(context, Constants.PREF_LAST_START_TIME, Constants.DEFAULT_START_TIME);
                    String prevEndTime = PreferenceHelper.getString(context, Constants.PREF_LAST_END_TIME, Constants.DEFAULT_END_TIME);

                    startTimeKV.setValue(prevStartTime);
                    endTimeKV.setValue(prevEndTime);

                    PreferenceHelper.putString(context, Constants.PREF_START_TIME, prevStartTime);
                    PreferenceHelper.putString(context, Constants.PREF_END_TIME, prevEndTime);

                    addNextDayIfNecessary();
                    doCurrentAutoFunctions(true);
                }
                enableOrDisableAutoSwitchViews(autoSwitch.isChecked());
            }
        });

        startTimeKV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog(startTimeKV, Constants.PREF_START_TIME);
            }
        });
        endTimeKV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog(endTimeKV, Constants.PREF_END_TIME);
            }
        });

        startTimeKV.setValue(PreferenceHelper.getString(context, Constants.PREF_START_TIME, Constants.DEFAULT_START_TIME));
        endTimeKV.setValue(PreferenceHelper.getString(context, Constants.PREF_END_TIME, Constants.DEFAULT_END_TIME));

        enableOrDisableAutoSwitchViews(autoSwitchStatus);
    }

    /**
     * Enable/disable views in this fragment depending on a boolean value
     * @param enabled A boolean indicating whether or not views should be enabled or disabled
     */
    private void enableOrDisableAutoSwitchViews(boolean enabled) {
        // Enabled = Status of autoSwitch if masterSwitch is on, otherwise status of masterSwitch
        boolean sunSwitchEnabled = sunSwitch.isChecked();

        // If auto switch is off, or master switch off, turn them off all
        if (!enabled) {
            startTimeKV.setEnabled(false);
            endTimeKV.setEnabled(false);
            sunSwitch.setEnabled(false);
        } else {
            // autoSwitch is enabled, enable sunSwitch
            sunSwitch.setEnabled(true);

            // if sunSwitch is enabled, disable kvviews
            startTimeKV.setEnabled(!sunSwitchEnabled);
            endTimeKV.setEnabled(!sunSwitchEnabled);
        }
    }

    /**
     * Shows time picker dialog and handles the time selected by user
     * @param viewWhoIsCallingIt View whose time time needs to be updated after user selects time
     * @param prefKey Preference for the key needed to be updated after user selects time
     */
    private void showTimePickerDialog(final KeyValueView viewWhoIsCallingIt, final String prefKey) {
        int time[] = TimeUtils.getCurrentTimeAsHourAndMinutes();
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                String selectedHour = i < 10 ? "0" + i: "" + i;
                String selectedMinute = i1 < 10 ? "0" +i1: "" + i1;
                String timeString = selectedHour + ":" + selectedMinute;
                PreferenceHelper.putString(context, prefKey, timeString);
                viewWhoIsCallingIt.setValue(timeString);

                addNextDayIfNecessary();

                doCurrentAutoFunctions(true);
            }
        }, time[0], time[1], false);
        timePickerDialog.show();
    }

    /**
     * Adds localised (Next Day) string if necessary
     */
    private void addNextDayIfNecessary() {
        String sStartTime = PreferenceHelper.getString(context, Constants.PREF_START_TIME, Constants.DEFAULT_START_TIME), sEndTime = PreferenceHelper.getString(context, Constants.PREF_END_TIME, Constants.DEFAULT_END_TIME);
        if (TimeUtils.getTimeInMinutes(sEndTime) < TimeUtils.getTimeInMinutes(sStartTime))
            endTimeKV.setValue(sEndTime + getString(R.string.next_day));
    }

    /**
     * Does automation functions
     * @param setAlarms Boolean indicating whether or not to set alarms
     */
    private void doCurrentAutoFunctions(boolean setAlarms) {
        String prefStartTime = PreferenceHelper.getString(context, Constants.PREF_START_TIME, Constants.DEFAULT_START_TIME);
        String prefEndTime = PreferenceHelper.getString(context, Constants.PREF_END_TIME, Constants.DEFAULT_END_TIME);

        Core.applyNightModeAsync(TimeUtils.determineWhetherNLShouldBeOnOrNot(prefStartTime, prefEndTime), context);

        if(setAlarms) AlarmUtils.setAlarms(context, prefStartTime, prefEndTime, true);
    }

    /**
     * Requests location permission
     */
    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, locationRequestCode);
    }

    /**
     * Wrapper method to do various location functions
     */
    private void doLocationStuff() {
        if (locationPermissionAvailable || LocationUtils.areLocationPermissionsAvailable(context))
            getBestLocation();
        else requestLocationPermission();
    }

    /**
     * Gets best location which is needed to determine sunset and sunrise timings
     * It first gets last known location. It does so because getting last known location is faster than getting current location
     * If the last known location is inappropriate, it uses current location
     * Then it gets and sets sunset/sunrise timings.
     */
    private void getBestLocation() {
        // Try to get best last known location
        Location location = LocationUtils.getLastKnownLocation(context);

        if (!LocationUtils.isLocationStale(location)) getAndSetSunriseSunsetTimings(location);
        else LocationUtils.requestCurrentLocation(context, this);
    }

    /**
     * Gets sunset/sunrise for currentLocation and sets alarms and updates views accordingly
     * @param currentLocation Current location
     */
    private void getAndSetSunriseSunsetTimings(Location currentLocation) {
        if (currentLocation == null) {
            Snackbar.make(getActivity().findViewById(R.id.layout_container), getString(R.string.location_unavailable), Snackbar.LENGTH_LONG).show();
            sunSwitch.setChecked(false);
            return;
        } else {
            // Save location
            PreferenceHelper.putLocation(context, currentLocation.getLongitude(), currentLocation.getLatitude());
        }

        TwilightManager.newInstance()
                .atLocation(currentLocation.getLongitude(), currentLocation.getLatitude())
                .computeAndSaveTime(context, new TwilightManager.OnComputeCompleteListener() {
                    @Override
                    public void onComputeComplete() {
                        doCurrentAutoFunctions(false);

                        startTimeKV.setValue(PreferenceHelper.getString(context, Constants.PREF_START_TIME, Constants.DEFAULT_START_TIME));
                        endTimeKV.setValue(PreferenceHelper.getString(context, Constants.PREF_END_TIME, Constants.DEFAULT_END_TIME));
                    }
                });

        addNextDayIfNecessary();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == locationRequestCode) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION) && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionAvailable = true;
                    getBestLocation();
                    break;
                }
            }
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        if (this.location == null) {
            this.location = location;
            getAndSetSunriseSunsetTimings(location);
        }
    }

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle bundle) {}
}
