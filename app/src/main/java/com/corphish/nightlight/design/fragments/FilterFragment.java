package com.corphish.nightlight.design.fragments;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.corphish.nightlight.data.Constants;
import com.corphish.nightlight.engine.Core;
import com.corphish.nightlight.engine.KCALManager;
import com.corphish.nightlight.helpers.ColorTemperatureUtil;
import com.corphish.nightlight.helpers.PreferenceHelper;
import com.corphish.nightlight.R;

/**
 * Created by Avinaba on 10/23/2017.
 * Filter fragment
 */

public class FilterFragment extends Fragment {

    private int blueIntensity, greenIntensity;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();

        blueIntensity = PreferenceHelper.getInt(context, Constants.PREF_BLUE_INTENSITY, Constants.DEFAULT_BLUE_INTENSITY);
        greenIntensity = PreferenceHelper.getInt(context, Constants.PREF_GREEN_INTENSITY, Constants.DEFAULT_GREEN_INTENSITY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.card_slider, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        SeekBar blueSlider = getView().findViewById(R.id.blue_intensity),
                greenSlider = getView().findViewById(R.id.green_intensity),
                temperatureSlider = getView().findViewById(R.id.temperature);

        blueSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                blueIntensity = seekBar.getProgress();
                PreferenceHelper.putInt(context, Constants.PREF_BLUE_INTENSITY ,blueIntensity);
                Core.applyNightModeAsync(true, getContext(), blueIntensity, greenIntensity);
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
                PreferenceHelper.putInt(context, Constants.PREF_GREEN_INTENSITY ,greenIntensity);
                Core.applyNightModeAsync(true, getContext(), blueIntensity, greenIntensity);
            }
        });

        blueSlider.setProgress(blueIntensity);
        greenSlider.setProgress(greenIntensity);

        temperatureSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int val = seekBar.getProgress() + 1000;
                //val = (val/100) * 100;
                KCALManager.updateKCALValues(ColorTemperatureUtil.colorTemperatureToIntRGB(val));
            }
        });
    }
}
