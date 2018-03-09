package com.corphish.nightlight.design.fragments;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
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
import com.gregacucnik.EditableSeekBar;

/**
 * Created by Avinaba on 10/23/2017.
 * Master switch fragment
 */

public class MasterSwitchFragment extends Fragment {

    public interface MasterSwitchClickListener {
        void onSwitchClicked (boolean checkStatus);
    }

    MasterSwitchClickListener mCallback;
    boolean enabled;

    private View kcalBackupSettingsView;
    private BottomSheetDialog bottomSheetDialog;
    private int r, g, b;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        enabled = PreferenceHelper.getBoolean(getContext(), Constants.PREF_MASTER_SWITCH);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (MasterSwitchClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement MasterSwitchClickListener");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.card_master_switch, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        SwitchCompat masterSwitch = getView().findViewById(R.id.master_switch);
        masterSwitch.setChecked(enabled);
        masterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Core.applyNightModeAsync(b, getContext());
                PreferenceHelper.putBoolean(getContext(), Constants.PREF_MASTER_SWITCH ,b);
                if (mCallback != null) mCallback.onSwitchClicked(b);
            }
        });

        AppCompatCheckBox preserveSwitch = getView().findViewById(R.id.kcal_preserve_switch);
        preserveSwitch.setChecked(PreferenceHelper.getBoolean(getContext(), Constants.KCAL_PRESERVE_SWITCH, true));
        preserveSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                PreferenceHelper.putBoolean(getContext(), Constants.KCAL_PRESERVE_SWITCH, b);
            }
        });


        getView().findViewById(R.id.configure_kcal_backup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getContext();
                if (context == null) return;
                bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogDark);
                initKCALBackupView();
                bottomSheetDialog.setContentView(kcalBackupSettingsView);
                bottomSheetDialog.show();
            }
        });
    }

    private void initKCALBackupView() {
        kcalBackupSettingsView = View.inflate(getContext(), R.layout.bottom_sheet_kcal_backup_set, null);

        String backedUpValues = PreferenceHelper.getString(getContext(), Constants.KCAL_PRESERVE_VAL, null);

        if (backedUpValues == null) {
            r = g = b = 256;
        } else {
            String parts[] = backedUpValues.split(" ");
            r = Integer.parseInt(parts[0]);
            g = Integer.parseInt(parts[1]);
            b = Integer.parseInt(parts[2]);
        }

        final EditableSeekBar red, green, blue;
        red = kcalBackupSettingsView.findViewById(R.id.kcal_red);
        green = kcalBackupSettingsView.findViewById(R.id.kcal_green);
        blue = kcalBackupSettingsView.findViewById(R.id.kcal_blue);

        red.setValue(r);
        green.setValue(g);
        blue.setValue(b);

        red.setOnEditableSeekBarChangeListener(new EditableSeekBar.OnEditableSeekBarChangeListener() {
            @Override
            public void onEditableSeekBarProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                r = seekBar.getProgress();
            }

            @Override
            public void onEnteredValueTooHigh() {
                red.setValue(255);
            }

            @Override
            public void onEnteredValueTooLow() {
                red.setValue(0);
            }

            @Override
            public void onEditableSeekBarValueChanged(int value) {
                r = value;
            }
        });

        green.setOnEditableSeekBarChangeListener(new EditableSeekBar.OnEditableSeekBarChangeListener() {
            @Override
            public void onEditableSeekBarProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                g = seekBar.getProgress();
            }

            @Override
            public void onEnteredValueTooHigh() {
                green.setValue(256);
            }

            @Override
            public void onEnteredValueTooLow() {
                green.setValue(0);
            }

            @Override
            public void onEditableSeekBarValueChanged(int value) {
                g = value;
            }
        });

        blue.setOnEditableSeekBarChangeListener(new EditableSeekBar.OnEditableSeekBarChangeListener() {
            @Override
            public void onEditableSeekBarProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                b = seekBar.getProgress();
            }

            @Override
            public void onEnteredValueTooHigh() {
                blue.setValue(256);
            }

            @Override
            public void onEnteredValueTooLow() {
                blue.setValue(0);
            }

            @Override
            public void onEditableSeekBarValueChanged(int value) {
                b = value;
            }
        });


        kcalBackupSettingsView.findViewById(R.id.button_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        kcalBackupSettingsView.findViewById(R.id.button_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceHelper.putString(getContext(), Constants.KCAL_PRESERVE_VAL, r + " " + g + " " + b);
                bottomSheetDialog.dismiss();
            }
        });
    }
}
