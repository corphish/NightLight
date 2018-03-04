package com.corphish.nightlight;

import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.corphish.nightlight.data.Constants;
import com.corphish.nightlight.engine.ProfilesManager;
import com.corphish.nightlight.helpers.PreferenceHelper;

import java.util.ArrayList;
import java.util.List;

public class ProfilesActivity extends AppCompatActivity implements ProfilesManager.DataChangeListener{

    View creatorView;
    AppCompatEditText editText;
    TextView editTextError, settingTitle1, settingTitle2, cancel;
    AppCompatSpinner modes;
    AppCompatSeekBar settingParam1, settingParam2;
    AppCompatButton ok;
    SwitchCompat nlSwitch;
    BottomSheetDialog bottomSheetDialog;

    private int currentModeSelection = Constants.NL_SETTING_MODE_FILTER;
    private ProfilesManager.Profile curProfile = null;

    private final int MODE_CREATE   =   0;
    private final int MODE_EDIT     =   1;
    private int curMode             =   MODE_CREATE;

    ProfilesManager profilesManager;

    RecyclerView recyclerView;
    ProfilesAdapter profilesAdapter;

    ArrayList<ProfilesManager.Profile> profiles;

    View emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiles);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                curMode = MODE_CREATE;
                bottomSheetDialog = new BottomSheetDialog(ProfilesActivity.this, R.style.BottomSheetDialogDark);
                initProfileCreatorViews();
                bottomSheetDialog.setContentView(creatorView);
                bottomSheetDialog.setCancelable(false);
                bottomSheetDialog.show();
            }
        });
        emptyView = findViewById(R.id.emptyView);

        profilesManager = new ProfilesManager(this);
        profilesManager.registerDataChangeListener(this);
        profilesManager.loadProfiles();
        profiles = profilesManager.getProfilesList();

        recyclerView = findViewById(R.id.profiles_holder);
        profilesAdapter = new ProfilesAdapter();
        profilesAdapter.setProfiles(profiles);

        recyclerView.invalidateItemDecorations();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(profilesAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);

        profilesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDataChanged(int newDataSize) {
        if (newDataSize < 1) emptyView.setVisibility(View.VISIBLE);
        else emptyView.setVisibility(View.GONE);
    }

    private class ProfilesAdapter extends RecyclerView.Adapter<ProfilesActivity.ProfilesAdapter.CustomViewHolder> {
        private List<ProfilesManager.Profile> profiles;

        public void setProfiles(List<ProfilesManager.Profile> profiles) {
            this.profiles = profiles;
        }

        public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            final TextView name, desc;
            CustomViewHolder(View v) {
                super(v);

                name = v.findViewById(R.id.profile_name);
                desc = v.findViewById(R.id.profile_desc);
            }

            @Override
            public void onClick(View v) {}
        }

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_profile_item, parent, false);
            return new CustomViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final CustomViewHolder holder, int position) {
            Log.i("NL_Prof", profiles.size() + "");
            holder.name.setText(profiles.get(position).getName());
            holder.desc.setText(getDescription(profiles.get(position)));
        }

        @Override
        public int getItemCount() {
            return profiles.size();
        }

        private String getDescription(ProfilesManager.Profile profile) {
            String desc = "";
            desc += getString(R.string.app_name) + " : " +
                    (profile.isSettingEnabled() ? getString(R.string.on).toLowerCase() : getString(R.string.off).toLowerCase()) + ", ";
            if (profile.getSettingMode() == Constants.NL_SETTING_MODE_TEMP) {
                desc += getString(R.string.color_temperature_title) + " : " + (3000 + profile.getSettings()[0]) + "K";
            } else {
                desc += getString(R.string.blue_light) + " : " + profile.getSettings()[0] + ", " +
                        getString(R.string.green_light) + " : " + profile.getSettings()[1];
            }
            return desc;
        }
    }

    private void initProfileCreatorViews() {
        creatorView = View.inflate(this, R.layout.bottom_sheet_create_profile, null);

        editText = creatorView.findViewById(R.id.profile_name_set);
        editTextError = creatorView.findViewById(R.id.profile_name_error);
        nlSwitch = creatorView.findViewById(R.id.profile_night_light_switch);
        settingTitle1 = creatorView.findViewById(R.id.profile_night_light_setting_title1);
        settingTitle2 = creatorView.findViewById(R.id.profile_night_light_setting_title2);
        cancel = creatorView.findViewById(R.id.button_cancel);
        modes = creatorView.findViewById(R.id.profile_night_light_setting_mode);
        settingParam1 = creatorView.findViewById(R.id.profile_night_light_setting_param1);
        settingParam2 = creatorView.findViewById(R.id.profile_night_light_setting_param2);
        ok = creatorView.findViewById(R.id.button_ok);

        modes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentModeSelection = position;
                updateProfileCreatorParams(currentModeSelection, curProfile);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (curMode == MODE_CREATE) {
                    profilesManager.createProfile(nlSwitch.isChecked(),
                            editText.getEditableText().toString(),
                            modes.getSelectedItemPosition(),
                            // TODO: Fix values
                            modes.getSelectedItemId() == Constants.NL_SETTING_MODE_TEMP ? new int[]{settingParam1.getProgress()} : new int[]{settingParam1.getProgress(), settingParam2.getProgress()});
                } else {
                    profilesManager.updateProfile(curProfile.getName(),
                            nlSwitch.isChecked(),
                            editText.getEditableText().toString(),
                            modes.getSelectedItemPosition(),
                            // TODO: Fix values
                            modes.getSelectedItemId() == Constants.NL_SETTING_MODE_TEMP ? new int[]{settingParam1.getProgress()} : new int[]{settingParam1.getProgress(), settingParam2.getProgress()});
                }
                profiles = profilesManager.getProfilesList();
                profilesAdapter.notifyDataSetChanged();
                bottomSheetDialog.dismiss();
            }
        });

        if (curProfile != null) {
            nlSwitch.setChecked(curProfile.isSettingEnabled());
            editText.setText(curProfile.getName());
        }
    }

    private void updateProfileCreatorParams(int mode, ProfilesManager.Profile profile) {
        if (mode == Constants.NL_SETTING_MODE_FILTER) {
            settingParam1.setEnabled(true);
            settingParam2.setEnabled(true);
            settingTitle1.setEnabled(true);
            settingTitle2.setEnabled(true);
            settingTitle1.setText(R.string.blue_light);
            settingTitle2.setText(R.string.green_light);
            if (profile != null) {
                settingParam1.setProgress(profile.getSettings()[0]);
                settingParam2.setProgress(profile.getSettings()[1]);
            } else {
                settingParam1.setProgress(PreferenceHelper.getInt(this, Constants.PREF_BLUE_INTENSITY, Constants.DEFAULT_BLUE_INTENSITY));
                settingParam2.setProgress(PreferenceHelper.getInt(this, Constants.PREF_GREEN_INTENSITY, Constants.DEFAULT_GREEN_INTENSITY));
            }
        } else {
            settingParam1.setEnabled(true);
            settingParam2.setEnabled(false);
            settingTitle1.setEnabled(true);
            settingTitle2.setEnabled(false);
            settingTitle1.setText(R.string.color_temperature_title);
            settingTitle2.setText(R.string.profile_nl_setting_unavailable);
            if (profile != null) {
                settingParam1.setProgress(profile.getSettings()[0]);
            } else {
                settingParam1.setProgress(PreferenceHelper.getInt(this, Constants.PREF_COLOR_TEMP, Constants.DEFAULT_COLOR_TEMP));
            }
        }
    }
}
