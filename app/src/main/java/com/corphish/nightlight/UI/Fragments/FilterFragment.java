package com.corphish.nightlight.UI.Fragments;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.corphish.nightlight.Engine.Core;
import com.corphish.nightlight.Helpers.PreferenceHelper;
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

        blueIntensity = PreferenceHelper.getBlueIntensity(context);
        greenIntensity = PreferenceHelper.getGreenIntensity(context);
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
                greenSlider = getView().findViewById(R.id.green_intensity);

        blueSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                blueIntensity = seekBar.getProgress();
                PreferenceHelper.putBlueIntensity(context, blueIntensity);
                Core.applyNightModeAsync(true, blueIntensity, greenIntensity);
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
                PreferenceHelper.putBlueIntensity(context, blueIntensity);
                Core.applyNightModeAsync(true, blueIntensity, greenIntensity);
            }
        });

        blueSlider.setProgress(blueIntensity);
        greenSlider.setProgress(greenIntensity);
    }
}
