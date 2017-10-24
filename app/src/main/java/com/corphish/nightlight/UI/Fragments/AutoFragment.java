package com.corphish.nightlight.UI.Fragments;

import android.Manifest;
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
import com.corphish.nightlight.Helpers.AlarmUtils;
import com.corphish.nightlight.Helpers.LocationUtils;
import com.corphish.nightlight.Helpers.PreferenceHelper;
import com.corphish.nightlight.Helpers.TimeUtils;
import com.corphish.nightlight.R;
import com.corphish.nightlight.Widgets.KeyValueView;

/**
 * Created by Avinaba on 10/24/2017.
 * Auto related fragment
 */

public class AutoFragment extends Fragment {

    private SwitchCompat autoSwitch, sunSwitch;
    private KeyValueView startTimeKV, endTimeKV;
    private boolean sunSwitchStatus, autoSwitchStatus;
    private Context context;

    private final int locationRequestCode = 69;
    private boolean locationPermissionAvailable = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();

        autoSwitchStatus = PreferenceHelper.getAutoSwitchStatus(context);
        sunSwitchStatus = PreferenceHelper.getSunSwitchStatus(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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

        autoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) doCurrentAutoFunctions();
                else Core.applyNightModeAsync(false);

                enableOrDisableAutoSwitchViews(b);
            }
        });

        sunSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                PreferenceHelper.putSunSwitchStatus(context, b);
                if (b) {
                    // Backup current timings
                    PreferenceHelper.putTime(context, Constants.PREF_LAST_START_TIME, PreferenceHelper.getStartTime(context, Constants.PREF_START_TIME));
                    PreferenceHelper.putTime(context, Constants.PREF_LAST_END_TIME, PreferenceHelper.getEndTime(context, Constants.PREF_END_TIME));

                    // TODO: Do location stuff
                    doLocationStuff();
                } else {
                    String prevStartTime = PreferenceHelper.getStartTime(context, Constants.PREF_LAST_START_TIME);
                    String prevEndTime = PreferenceHelper.getEndTime(context, Constants.PREF_LAST_END_TIME);

                    startTimeKV.setValue(prevStartTime);
                    endTimeKV.setValue(prevEndTime);

                    PreferenceHelper.putTime(context, Constants.PREF_START_TIME, prevStartTime);
                    PreferenceHelper.putTime(context, Constants.PREF_END_TIME, prevEndTime);

                    addNextDayIfNecessary();
                }

                doCurrentAutoFunctions();
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

        autoSwitch.setChecked(autoSwitchStatus);
        sunSwitch.setChecked(sunSwitchStatus);

        startTimeKV.setValue(PreferenceHelper.getStartTime(context, Constants.PREF_START_TIME));
        endTimeKV.setValue(PreferenceHelper.getEndTime(context, Constants.PREF_END_TIME));
    }

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

    private void showTimePickerDialog(final KeyValueView viewWhoIsCallingIt, final String prefKey) {
        int time[] = TimeUtils.getCurrentTimeAsHourAndMinutes();
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                String selectedHour = i < 10 ? "0" + i: "" + i;
                String selectedMinute = i1 < 10 ? "0" +i1: "" + i1;
                String timeString = selectedHour + ":" + selectedMinute;
                PreferenceHelper.putTime(context, prefKey, timeString);
                viewWhoIsCallingIt.setValue(timeString);

                addNextDayIfNecessary();

                doCurrentAutoFunctions();
            }
        }, time[0], time[1], false);
        timePickerDialog.show();
    }

    private void addNextDayIfNecessary() {
        String sStartTime = PreferenceHelper.getStartTime(context, Constants.PREF_START_TIME), sEndTime = PreferenceHelper.getEndTime(context, Constants.PREF_END_TIME);
        if (TimeUtils.getTimeInMinutes(sEndTime) < TimeUtils.getTimeInMinutes(sStartTime))
            endTimeKV.setValue(sEndTime + getString(R.string.next_day));
    }

    private void doCurrentAutoFunctions() {
        String prefStartTime = PreferenceHelper.getStartTime(context, Constants.PREF_START_TIME);
        String prefEndTime = PreferenceHelper.getEndTime(context, Constants.PREF_END_TIME);

        Core.applyNightModeAsync(TimeUtils.determineWhetherNLShouldBeOnOrNot(prefStartTime, prefEndTime), context);

        AlarmUtils.setAlarms(context, prefStartTime, prefEndTime);
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, locationRequestCode);
    }

    private void doLocationStuff() {
        if (locationPermissionAvailable || LocationUtils.areLocationPermissionsAvailable(context))
            getAndSetSunriseSunsetTimings();
        else requestLocationPermission();
    }

    private void getAndSetSunriseSunsetTimings() {
        Location currentLocation = LocationUtils.getLastKnownLocation(context);

        String sunriseTime = LocationUtils.getSunriseTime(currentLocation), sunsetTime = LocationUtils.getSunsetTime(currentLocation);

        startTimeKV.setValue(sunsetTime);
        endTimeKV.setValue(sunriseTime);

        PreferenceHelper.putTime(context, Constants.PREF_START_TIME, sunsetTime);
        PreferenceHelper.putTime(context, Constants.PREF_END_TIME, sunriseTime);

        addNextDayIfNecessary();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == locationRequestCode) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION) && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionAvailable = true;
                    getAndSetSunriseSunsetTimings();
                    break;
                }
            }
        }

    }
}
