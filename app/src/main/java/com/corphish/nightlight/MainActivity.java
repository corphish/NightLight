package com.corphish.nightlight;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import com.corphish.nightlight.Engine.Core;
import com.corphish.nightlight.Helpers.AlarmUtils;
import com.corphish.nightlight.Helpers.ExternalLink;
import com.corphish.nightlight.Helpers.LocationUtils;
import com.corphish.nightlight.Helpers.PreferenceHelper;
import com.corphish.nightlight.Helpers.RootUtils;
import com.corphish.nightlight.Helpers.TimeUtils;
import com.corphish.nightlight.Widgets.KeyValueView;
import com.corphish.nightlight.Data.Constants;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    SwitchCompat masterSwitch, autoSwitch, forceSwitch, sunSwitch;
    SeekBar blueSlider, greenSlider;
    KeyValueView startTime, endTime;

    Context context = this;

    /**
     * Formula for calculating effective intensity
     * = Constants.MAX_BLUE_LIGHT  - <seekbar/defaultValue>
     */
    int defaultBlueIntensity = Constants.DEFAULT_BLUE_INTENSITY, currentBlueIntensity = defaultBlueIntensity;
    int defaultGreenIntensity = Constants.DEFAULT_GREEN_INTENSITY, currentGreenIntensity = defaultGreenIntensity;

    // Location stuff
    boolean locationPermissionAvailable = false;
    int locationRequestCode = 69;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init() {
        currentBlueIntensity = PreferenceHelper.getBlueIntensity(this);
        currentGreenIntensity = PreferenceHelper.getGreenIntensity(this);
        viewInit();
        if (!BuildConfig.DEBUG) new CompatibilityChecker().execute();
    }

    private void viewInit() {
        masterSwitch = findViewById(R.id.master_switch);
        forceSwitch = findViewById(R.id.force_switch);
        blueSlider = findViewById(R.id.blue_intensity);
        greenSlider = findViewById(R.id.green_intensity);
        autoSwitch = findViewById(R.id.auto_enable);
        sunSwitch = findViewById(R.id.sun_enable);
        startTime = findViewById(R.id.start_time);
        endTime = findViewById(R.id.end_time);

        TextView versionTV = findViewById(R.id.app_version);
        versionTV.setText(getString(R.string.app_name) + " v" + BuildConfig.VERSION_NAME);

        final boolean enabled = PreferenceHelper.getMasterSwitchStatus(this);
        masterSwitch.setChecked(enabled);
        masterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                toggleSwitch(b);
            }
        });

        forceSwitch.setChecked(PreferenceHelper.getForceSwitchStatus(this));
        forceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Core.applyNightModeAsync(b, currentBlueIntensity, currentGreenIntensity);
                PreferenceHelper.putForceSwitchStatus(context, b);
            }
        });

        blueSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                currentBlueIntensity = seekBar.getProgress();
                new Switcher(true, false).execute();
                PreferenceHelper.putBlueIntensity(context, currentBlueIntensity);
            }
        });
        blueSlider.setProgress(currentBlueIntensity);

        greenSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                currentGreenIntensity = seekBar.getProgress();
                new Switcher(true, false).execute();
                PreferenceHelper.putGreenIntensity(context, currentGreenIntensity);
            }
        });
        greenSlider.setProgress(currentGreenIntensity);

        boolean autoEnabled = PreferenceHelper.getAutoSwitchStatus(this);
        boolean sunEnabled = PreferenceHelper.getSunSwitchStatus(this);

        autoSwitch.setChecked(autoEnabled);
        enableOrDisableAutoSwitchViews(autoEnabled);
        autoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                PreferenceHelper.putAutoSwitchStatus(context, b);

                if (b) doCurrentAutoFunctions();
                else new Switcher(true, false).execute();
                enableOrDisableAutoSwitchViews(b);
            }
        });

        sunSwitch.setChecked(sunEnabled);
        sunSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                PreferenceHelper.putSunSwitchStatus(context, b);
                if (b) {
                    // Backup current timings
                    PreferenceHelper.putTime(context, Constants.PREF_LAST_START_TIME, startTime.getValue());
                    PreferenceHelper.putTime(context, Constants.PREF_LAST_END_TIME, endTime.getValue());

                    doLocationStuff();
                } else {
                    String prevStartTime = PreferenceHelper.getStartTime(context, Constants.PREF_LAST_START_TIME);
                    String prevEndTime = PreferenceHelper.getEndTime(context, Constants.PREF_LAST_END_TIME);

                    startTime.setValue(prevStartTime);
                    endTime.setValue(prevEndTime);

                    PreferenceHelper.putTime(context, Constants.PREF_START_TIME, prevStartTime);
                    PreferenceHelper.putTime(context, Constants.PREF_END_TIME, prevEndTime);
                }

                doCurrentAutoFunctions();
            }
        });

        String startTimeVal = PreferenceHelper.getStartTime(this, Constants.PREF_START_TIME);
        String endTimeVal = PreferenceHelper.getEndTime(this, Constants.PREF_END_TIME);

        startTime.setValue(startTimeVal);
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog(startTime, Constants.PREF_START_TIME);
            }
        });

        endTime.setValue(endTimeVal);
        addNextDayIfNecessary();
        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog(endTime, Constants.PREF_END_TIME);
            }
        });

        findViewById(R.id.card_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExternalLink.open(context, "market://details?id="+getPackageName());
            }
        });

        findViewById(R.id.card_donate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExternalLink.open(context, "market://details?id=com.corphish.nightlight.donate");
            }
        });

        enableOrDisableViews(enabled);
    }

    private void showTimePickerDialog(final KeyValueView viewWhoIsCallingIt, final String prefKey) {
        int time[] = TimeUtils.getCurrentTimeAsHourAndMinutes();
        TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
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

    private void enableOrDisableViews(boolean enabled) {
        blueSlider.setEnabled(enabled);
        greenSlider.setEnabled(enabled);
        autoSwitch.setEnabled(enabled);
        sunSwitch.setEnabled(enabled);
        forceSwitch.setEnabled(enabled);
        if (!enabled) enableOrDisableAutoSwitchViews(false);
        else enableOrDisableAutoSwitchViews(autoSwitch.isChecked());
    }

    private void enableOrDisableAutoSwitchViews(boolean enabled) {
        startTime.setEnabled(enabled);
        endTime.setEnabled(enabled);
        sunSwitch.setEnabled(enabled);
    }

    private void toggleSwitch(boolean enabled) {
        new Switcher(enabled).execute();
        PreferenceHelper.putMasterSwitchStatus(this, enabled);
    }

    private void doCurrentAutoFunctions() {
        String prefStartTime = PreferenceHelper.getStartTime(this, Constants.PREF_START_TIME);
        String prefEndTime = PreferenceHelper.getEndTime(this, Constants.PREF_END_TIME);

        new Switcher(TimeUtils.determineWhetherNLShouldBeOnOrNot(prefStartTime, prefEndTime), false).execute();

        AlarmUtils.setAlarms(this, prefStartTime, prefEndTime);
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, locationRequestCode);
    }

    private void doLocationStuff() {
        if (locationPermissionAvailable || LocationUtils.areLocationPermissionsAvailable(context))
            getAndSetSunriseSunsetTimings();
        else requestLocationPermission();
    }

    private void getAndSetSunriseSunsetTimings() {
        Location currentLocation = LocationUtils.getLastKnownLocation(this);

        String sunriseTime = LocationUtils.getSunriseTime(currentLocation), sunsetTime = LocationUtils.getSunsetTime(currentLocation);

        startTime.setValue(sunsetTime);
        endTime.setValue(sunriseTime);
        addNextDayIfNecessary();

        PreferenceHelper.putTime(this, Constants.PREF_START_TIME, sunsetTime);
        PreferenceHelper.putTime(this, Constants.PREF_END_TIME, sunriseTime);
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

    private void addNextDayIfNecessary() {
        /*String sStartTime = PreferenceHelper.getStartTime(this, Constants.PREF_START_TIME), sEndTime = PreferenceHelper.getEndTime(this, Constants.PREF_END_TIME);
        if (TimeUtils.getTimeInMinutes(sEndTime) < TimeUtils.getTimeInMinutes(sStartTime))
            endTime.setValue(sEndTime + getString(R.string.next_day));*/
    }

    private void showAlertDialog(int caption, int msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(caption);
        builder.setMessage(msg);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.show();
    }

    private class CompatibilityChecker extends AsyncTask<String, String, String> {
        boolean rootAccessAvailable = false, kcalSupported = false;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage(getString(R.string.compat_check));
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... booms) {
            rootAccessAvailable = RootUtils.getRootAccess();
            kcalSupported = new File(Constants.KCAL_ADJUST).exists();
            return null;
        }

        @Override
        protected void onPostExecute(String boom) {
            progressDialog.hide();
            if (!rootAccessAvailable) showAlertDialog(R.string.no_root_access, R.string.no_root_desc);
            else if (!kcalSupported) showAlertDialog(R.string.no_kcal, R.string.no_kcal_desc);
        }
    }

    private class Switcher extends AsyncTask<String, String, String> {
        boolean enabled, toModifyViews;
        Switcher(boolean b) {
            enabled = b;
            toModifyViews = true;
        }

        Switcher(boolean e, boolean m) {
            enabled = e;
            toModifyViews = m;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... booms) {
            Core.applyNightMode(enabled, currentBlueIntensity, currentGreenIntensity);
            return null;
        }

        @Override
        protected void onPostExecute(String boom) {
            if (toModifyViews) enableOrDisableViews(enabled);
        }
    }
}
