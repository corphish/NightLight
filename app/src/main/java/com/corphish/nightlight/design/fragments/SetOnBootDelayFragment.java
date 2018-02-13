package com.corphish.nightlight.design.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.corphish.nightlight.R;
import com.corphish.nightlight.data.Constants;
import com.corphish.nightlight.helpers.PreferenceHelper;

/**
 * Created by avinabadalal on 13/02/18.
 * Set on boot delay fragment
 */

public class SetOnBootDelayFragment extends Fragment {
    private int bootDelay;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();
        bootDelay = PreferenceHelper.getInt(context, Constants.PREF_BOOT_DELAY, Constants.DEFAULT_BOOT_DELAY);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.card_set_on_boot_delay, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final TextView textView = getView().findViewById(R.id.set_on_boot_desc_tv);
        textView.setText(getString(R.string.set_on_boot_delay_desc, bootDelay+"s"));

        SeekBar seekBar = getView().findViewById(R.id.set_on_boot_delay);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                bootDelay = seekBar.getProgress();
                textView.setText(getString(R.string.set_on_boot_delay_desc, bootDelay+"s"));
                PreferenceHelper.putInt(context, Constants.PREF_BOOT_DELAY, bootDelay);
            }
        });

        seekBar.setProgress(bootDelay);

        TextView warn = getView().findViewById(R.id.set_on_boot_warn);
        warn.setVisibility(PreferenceHelper.getBoolean(context, Constants.PREF_LAST_BOOT_RES, true) ? View.GONE : View.VISIBLE);
    }
}
