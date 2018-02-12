package com.corphish.nightlight.design.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import com.corphish.nightlight.R;
import com.corphish.nightlight.data.Constants;
import com.corphish.nightlight.design.widgets.KeyValueView;
import com.corphish.nightlight.engine.Core;
import com.corphish.nightlight.helpers.PreferenceHelper;
import com.corphish.nightlight.services.NightLightAppService;

/**
 * Created by avinabadalal on 12/02/18.
 * Color temperature fragment
 */

public class ColorTemperatureFragment extends Fragment {
    private Context context;
    private int colorTemperature;

    private boolean mode;

    // Views
    private SwitchCompat switchCompat;
    private SeekBar seekBar;
    private KeyValueView advancedAutomation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();
        getValues();
        mode = PreferenceHelper.getInt(context, Constants.PREF_SETTING_MODE, Constants.NL_SETTING_MODE_FILTER) == Constants.NL_SETTING_MODE_TEMP;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.card_temperature, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        switchCompat = getView().findViewById(R.id.mode_switch);
        seekBar = getView().findViewById(R.id.temperature_value);
        advancedAutomation = getView().findViewById(R.id.temperature_automation);

        // Disable them by default
        seekBar.setEnabled(false);
        advancedAutomation.setEnabled(false);

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mode = isChecked;

                int settingMode = isChecked ? Constants.NL_SETTING_MODE_TEMP: Constants.NL_SETTING_MODE_FILTER;

                PreferenceHelper.putInt(context, Constants.PREF_SETTING_MODE, settingMode);

                getValues();

                seekBar.setEnabled(isChecked);
                seekBar.setProgress(colorTemperature - 3000);
                advancedAutomation.setEnabled(isChecked);

                NightLightAppService.getInstance().notifyNewSettingMode(settingMode);
            }
        });

        switchCompat.setChecked(mode);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int val = 3000 + seekBar.getProgress();
                val = (val/100) * 100;
                Log.i("NL_ColorTempFragment","val - " + val);
                PreferenceHelper.putInt(context, Constants.PREF_COLOR_TEMP, val);
                Core.applyNightModeAsync(true, context, val);
            }
        });

        seekBar.setProgress(colorTemperature - 3000);
    }

    public void onStateChanged(int newMode) {
        if (switchCompat != null) switchCompat.setChecked(newMode == Constants.NL_SETTING_MODE_TEMP);
    }

    private void getValues() {
        colorTemperature = PreferenceHelper.getInt(context, Constants.PREF_BLUE_INTENSITY, Constants.DEFAULT_COLOR_TEMP);
    }
}
