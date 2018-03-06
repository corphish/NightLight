package com.corphish.nightlight.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.corphish.nightlight.data.Constants;
import com.corphish.nightlight.engine.ProfilesManager;
import com.corphish.nightlight.helpers.PreferenceHelper;

import java.util.Arrays;

/**
 * Created by avinabadalal on 03/03/18.
 * Tasker service
 */

public class TaskerIntentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("NL_Tasker","Plugin supported");
        boolean masterSwitchEnabled = PreferenceHelper.getBoolean(context, Constants.PREF_MASTER_SWITCH);
        if (!masterSwitchEnabled) return;
        ProfilesManager profilesManager = new ProfilesManager(context);
        profilesManager.loadProfiles();
        Bundle bundle = intent.getBundleExtra("com.twofortyfouram.locale.intent.extra.BUNDLE");
        if (bundle == null) return;
        
        String name = bundle.getString(Constants.TASKER_SETTING);
        if (name == null) return;
        
        ProfilesManager.Profile profile = profilesManager.getProfileByName(name);
        if (profile != null) {
            profile.apply(context);

            PreferenceHelper.putInt(context, Constants.PREF_CUR_APPLY_TYPE, Constants.APPLY_TYPE_PROFILE);
            PreferenceHelper.putBoolean(context, Constants.PREF_CUR_APPLY_EN, profile.isSettingEnabled());
            PreferenceHelper.putInt(context, Constants.PREF_CUR_PROF_MODE, profile.getSettingMode());
            PreferenceHelper.putString(context, Constants.PREF_CUR_PROF_VAL, Arrays.toString(profile.getSettings()));
        }
    }
}
