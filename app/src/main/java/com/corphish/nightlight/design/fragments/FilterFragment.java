package com.corphish.nightlight.design.fragments;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import com.corphish.nightlight.data.Constants;
import com.corphish.nightlight.engine.Core;
import com.corphish.nightlight.helpers.PreferenceHelper;
import com.corphish.nightlight.R;
import com.corphish.nightlight.services.NightLightAppService;

/**
 * Created by Avinaba on 10/23/2017.
 * Filter fragment
 */

public class FilterFragment extends Fragment {

    private int blueIntensity, greenIntensity;
    boolean mode;
    private Context context;

    // Views
    SeekBar blueSlider, greenSlider;
    SwitchCompat switchCompat;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();
        getValues();
        mode = PreferenceHelper.getInt(context, Constants.PREF_SETTING_MODE, Constants.NL_SETTING_MODE_FILTER) == Constants.NL_SETTING_MODE_FILTER;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.card_slider, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        blueSlider = getView().findViewById(R.id.blue_intensity);
        greenSlider = getView().findViewById(R.id.green_intensity);
        switchCompat = getView().findViewById(R.id.mode_switch);

        // Disable them by default
        blueSlider.setEnabled(false);
        greenSlider.setEnabled(false);

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mode = isChecked;

                int settingMode = isChecked ? Constants.NL_SETTING_MODE_FILTER : Constants.NL_SETTING_MODE_TEMP;

                PreferenceHelper.putInt(context, Constants.PREF_SETTING_MODE, settingMode);

                blueSlider.setEnabled(isChecked);
                greenSlider.setEnabled(isChecked);

                if (isChecked && NightLightAppService.getInstance().isInitDone()) {
                    Core.applyNightModeAsync(isChecked, context, blueIntensity, greenIntensity);
                }

                NightLightAppService.getInstance().notifyNewSettingMode(settingMode);
            }
        });

        switchCompat.setChecked(mode);

        blueSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                blueIntensity = seekBar.getProgress();
                if (NightLightAppService.getInstance().isInitDone()) {
                    PreferenceHelper.putInt(context, Constants.PREF_BLUE_INTENSITY ,blueIntensity);
                    Core.applyNightModeAsync(true, getContext(), blueIntensity, greenIntensity);
                    PreferenceHelper.putInt(context, Constants.PREF_CUR_APPLY_TYPE, Constants.APPLY_TYPE_NON_PROFILE);
                }
            }
        });

        greenSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                greenIntensity = seekBar.getProgress();
                if (NightLightAppService.getInstance().isInitDone()) {
                    PreferenceHelper.putInt(context, Constants.PREF_GREEN_INTENSITY ,greenIntensity);
                    Core.applyNightModeAsync(true, getContext(), blueIntensity, greenIntensity);
                    PreferenceHelper.putInt(context, Constants.PREF_CUR_APPLY_TYPE, Constants.APPLY_TYPE_NON_PROFILE);
                }
            }
        });

        blueSlider.setProgress(blueIntensity);
        greenSlider.setProgress(greenIntensity);

        NightLightAppService.getInstance()
                .incrementViewInitCount();
    }

    public void onStateChanged(int newMode) {
        if (switchCompat != null) switchCompat.setChecked(newMode == Constants.NL_SETTING_MODE_FILTER);
    }

    private void getValues() {
        blueIntensity = PreferenceHelper.getInt(context, Constants.PREF_BLUE_INTENSITY, Constants.DEFAULT_BLUE_INTENSITY);
        greenIntensity = PreferenceHelper.getInt(context, Constants.PREF_GREEN_INTENSITY, Constants.DEFAULT_GREEN_INTENSITY);
    }
}
