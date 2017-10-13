package com.corphish.nightlight;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.corphish.nightlight.Engine.Core;
import com.corphish.nightlight.Helpers.AlarmUtils;
import com.corphish.nightlight.Helpers.PreferenceHelper;
import com.corphish.nightlight.Helpers.RootUtils;
import com.corphish.nightlight.Helpers.TimeUtils;
import com.corphish.nightlight.Widgets.KeyValueView;
import com.corphish.nightlight.Data.Constants;

import java.io.File;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    Switch masterSwitch, autoSwitch;
    SeekBar slider;
    KeyValueView startTime, endTime;

    Context context = this;

    /**
     * Formula for calculating effective intensity
     * = Constants.MAX_BLUE_LIGHT  - <seekbar/defaultValue>
     */
    int defaultIntensity = Constants.DEFAULT_INTENSITY, currentIntensity = defaultIntensity;

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
        currentIntensity = PreferenceHelper.getIntensity(this);
        viewInit();
        new CompatibilityChecker().execute();
    }

    private void viewInit() {
        masterSwitch = findViewById(R.id.master_switch);
        slider = findViewById(R.id.intensity);
        autoSwitch = findViewById(R.id.auto_enable);
        startTime = findViewById(R.id.start_time);
        endTime = findViewById(R.id.end_time);

        TextView versionTV = findViewById(R.id.app_version);
        versionTV.setText(getString(R.string.app_name) + " v" + BuildConfig.VERSION_NAME);

        boolean enabled = PreferenceHelper.getMasterSwitchStatus(this);
        masterSwitch.setChecked(enabled);
        masterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                toggleSwitch(b);
            }
        });

        slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                currentIntensity = seekBar.getProgress();
                new Switcher(true, false).execute();
                PreferenceHelper.putIntensity(context, currentIntensity);
            }
        });
        slider.setProgress(currentIntensity);

        boolean autoEnabled = PreferenceHelper.getAutoSwitchStatus(this);
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

        startTime.setValue(PreferenceHelper.getStartTime(this));
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog(startTime, Constants.PREF_START_TIME);
            }
        });

        endTime.setValue(PreferenceHelper.getEndTime(this));
        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog(endTime, Constants.PREF_END_TIME);
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
                String timeSting = selectedHour + ":" + selectedMinute;
                PreferenceHelper.putTime(context, prefKey, timeSting);
                viewWhoIsCallingIt.setValue(timeSting);

                doCurrentAutoFunctions();
            }
        }, time[0], time[1], false);
        timePickerDialog.show();
    }

    private void enableOrDisableViews(boolean enabled) {
        slider.setEnabled(enabled);
        autoSwitch.setEnabled(enabled);
        if (!enabled) enableOrDisableAutoSwitchViews(false);
        else enableOrDisableAutoSwitchViews(autoSwitch.isChecked());
    }

    private void enableOrDisableAutoSwitchViews(boolean enabled) {
        startTime.setEnabled(enabled);
        endTime.setEnabled(enabled);
    }

    private void toggleSwitch(boolean enabled) {
        new Switcher(enabled).execute();
        PreferenceHelper.putMasterSwitchStatus(this, enabled);
    }

    private void doCurrentAutoFunctions() {
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMin = calendar.get(Calendar.MINUTE);
        int currentTime = TimeUtils.getTimeInMinutes(currentHour, currentMin);

        String prefStartTime = PreferenceHelper.getStartTime(this);
        String prefEndTime = PreferenceHelper.getEndTime(this);

        int startTime = TimeUtils.getTimeInMinutes(prefStartTime);
        int endTime = TimeUtils.getTimeInMinutes(prefEndTime);

        if (currentTime >= startTime && currentTime <= endTime) new Switcher(true, false).execute();
        else new Switcher(false, false).execute();

        AlarmUtils.setAlarms(this, prefStartTime, prefEndTime);
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
            Core.applyNightMode(enabled, currentIntensity);
            return null;
        }

        @Override
        protected void onPostExecute(String boom) {
            if (toModifyViews) enableOrDisableViews(enabled);
        }
    }
}
