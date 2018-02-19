package com.corphish.nightlight.design.fragments;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TimePicker;

import com.corphish.nightlight.R;
import com.corphish.nightlight.data.Constants;
import com.corphish.nightlight.design.widgets.KeyValueView;
import com.corphish.nightlight.helpers.PreferenceHelper;
import com.corphish.nightlight.helpers.TimeUtils;

import java.util.Locale;

/**
 * Created by avinabadalal on 19/02/18.
 * Advanced automation fragment
 */

public class AdvancedAutomationFragment extends Fragment {
    // Cards
    View tempCard, timeCard, intervalCard;

    // Switches
    SwitchCompat mainSwitch;

    // Seekbars
    SeekBar maxTemp, minTemp;

    // Times (KVView)
    KeyValueView scaleDownStart, scaleDownEnd, peakStart, peakEnd, scaleUpStart, scaleUpEnd;

    // RadioButton
    RadioButton halfHour, oneHour;

    // Switch
    boolean enabled;

    // Interval
    int interval;

    // Automatic times
    String autoStartTime, autoEndTime, scaleDownEndTime, peakEndTime;

    // Temps
    int maxTempVal, minTempVal;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        enabled = PreferenceHelper.getBoolean(getContext(), Constants.PREF_ADV_AUTO_SWITCH, false);
        interval = PreferenceHelper.getInt(getContext(), Constants.PREF_ADV_AUTO_TIME_INTERVAL, 60);

        autoStartTime = PreferenceHelper.getString(getContext(), Constants.PREF_START_TIME, Constants.DEFAULT_START_TIME);
        autoEndTime = PreferenceHelper.getString(getContext(), Constants.PREF_END_TIME, Constants.DEFAULT_END_TIME);

        scaleDownEndTime = PreferenceHelper.getString(getContext(), Constants.PREF_ADV_AUTO_SCALE_DOWN_END, Constants.DEFAULT_PEAK_START_TIME);
        peakEndTime = PreferenceHelper.getString(getContext(), Constants.PREF_ADV_AUTO_SCALE_DOWN_END, Constants.DEFAULT_PEAK_END_TIME);

        maxTempVal = PreferenceHelper.getInt(getContext(), Constants.PREF_ADV_AUTO_MAX_TEMP, Constants.DEFAULT_MAX_TEMP);
        minTempVal = PreferenceHelper.getInt(getContext(), Constants.PREF_ADV_AUTO_MIN_TEMP, Constants.DEFAULT_MIN_TEMP);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_advanced_automation, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        assignViews(getView());
        setClickListeners();
        initViews();
    }

    private void assignViews(View view) {
        tempCard = view.findViewById(R.id.card_advanced_automation_temps);
        timeCard = view.findViewById(R.id.card_times);
        intervalCard = view.findViewById(R.id.card_advanced_automation_interval);

        mainSwitch = view.findViewById(R.id.advanced_automation_switch);

        maxTemp = view.findViewById(R.id.maximum_temperature);
        minTemp = view.findViewById(R.id.minimum_temperature);

        scaleDownStart = view.findViewById(R.id.scale_down_start_time);
        scaleDownEnd = view.findViewById(R.id.scale_down_end_time);
        peakStart = view.findViewById(R.id.peak_start_time);
        peakEnd = view.findViewById(R.id.peak_end_time);
        scaleUpStart = view.findViewById(R.id.scale_up_start_time);
        scaleUpEnd = view.findViewById(R.id.scale_up_end_time);

        halfHour = view.findViewById(R.id.interval_half_hour);
        oneHour = view.findViewById(R.id.interval_one_hour);
    }

    private void setClickListeners() {
        setSwitchClickListeners();
        setSeekBarClickListeners();
        setKVViewClickListeners();
        setRadioButtonClickListeners();
    }

    private void setSwitchClickListeners() {
        mainSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferenceHelper.putBoolean(getContext(), Constants.PREF_ADV_AUTO_SWITCH, isChecked);
                showSettingViews(isChecked);
            }
        });
    }

    private void setSeekBarClickListeners() {
        maxTemp.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();

                // Make sure maxTemp always greater than minTemp
                if (progress < minTemp.getProgress()) {
                    progress = minTemp.getProgress();
                    maxTemp.setProgress(progress);
                }

                PreferenceHelper.putInt(getContext(), Constants.PREF_ADV_AUTO_MAX_TEMP, 3000 + (progress / 100)*100);
            }
        });

        minTemp.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();

                // Make sure maxTemp always greater than minTemp
                if (progress > maxTemp.getProgress()) {
                    progress = maxTemp.getProgress();
                    minTemp.setProgress(progress);
                }

                PreferenceHelper.putInt(getContext(), Constants.PREF_ADV_AUTO_MIN_TEMP, 3000 + (progress / 100)*100);
            }
        });
    }

    private void setKVViewClickListeners() {
        // User can only set scale down end time and peak time end time
        // scale down start time = start time of automatic schedule
        // scale down end time = user set
        // peak time start time = scale down end time
        // peak time end time = user set
        // For internal purposes
        // scale up start time = peak time end time
        // scale up end time = end time of automatic schedule

        scaleDownEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String setTime = String.format(Locale.getDefault() ,"%02d:%02d", hourOfDay, minute);

                        // Prevent setting time below scaleDownStartTime for now
                        // Actual behavior should support setting this time in next day but before the time when automatic schedule ends
                        String scaleDownStartTime = scaleDownStart.getValue();
                        if (setTime.compareTo(scaleDownStartTime) < 0) setTime = scaleDownStartTime;

                        scaleDownEnd.setValue(setTime);
                        peakStart.setValue(setTime);

                        PreferenceHelper.putString(getContext(), Constants.PREF_ADV_AUTO_SCALE_DOWN_END, setTime);
                        PreferenceHelper.putString(getContext(), Constants.PREF_ADV_AUTO_PEAK_START, setTime);
                    }
                });
            }
        });

        peakEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String setTime = String.format(Locale.getDefault() ,"%02d:%02d", hourOfDay, minute);

                        // Prevent setting time below peakStartTime for now
                        // Actual behavior should support setting this time in next day but before the time when automatic schedule ends
                        String peakStartValue = peakStart.getValue();
                        if (setTime.compareTo(peakStartValue) < 0) setTime = peakStartValue;

                        peakEnd.setValue(setTime);

                        PreferenceHelper.putString(getContext(), Constants.PREF_ADV_AUTO_PEAK_END, setTime);
                    }
                });
            }
        });
    }

    private void setRadioButtonClickListeners() {
        oneHour.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) PreferenceHelper.putInt(getContext(), Constants.PREF_ADV_AUTO_TIME_INTERVAL, 60);
            }
        });

        halfHour.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) PreferenceHelper.putInt(getContext(), Constants.PREF_ADV_AUTO_TIME_INTERVAL, 30);
            }
        });
    }

    private void initViews() {
        mainSwitch.setChecked(enabled);

        scaleDownStart.setValue(autoStartTime);
        scaleDownStart.setEnabled(false);

        scaleDownEnd.setValue(scaleDownEndTime);

        peakStart.setValue(scaleDownEndTime);
        peakStart.setEnabled(false);

        peakEnd.setValue(peakEndTime);

        scaleUpStart.setValue(peakEndTime);
        scaleUpStart.setEnabled(false);

        scaleUpEnd.setValue(autoEndTime);
        scaleUpEnd.setEnabled(false);

        maxTemp.setProgress(maxTempVal - 3000);
        minTemp.setProgress(minTempVal - 3000);

        if (interval == 60) oneHour.setChecked(true);
        else halfHour.setChecked(true);

        showSettingViews(enabled);
    }

    private void showSettingViews(boolean toShow) {
        int visibility = toShow ? View.VISIBLE : View.GONE;

        tempCard.setVisibility(visibility);
        timeCard.setVisibility(visibility);
        intervalCard.setVisibility(visibility);
    }

    private void showTimePicker(TimePickerDialog.OnTimeSetListener onTimeSetListener) {
        int currentTime[] = TimeUtils.getCurrentTimeAsHourAndMinutes();

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), onTimeSetListener, currentTime[0], currentTime[1], false);
        timePickerDialog.show();
    }
}
