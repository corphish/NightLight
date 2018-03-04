package com.corphish.nightlight.engine;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import com.corphish.nightlight.helpers.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by avinabadalal on 03/03/18.
 * ProfilesManager to manage profiles
 */

public class ProfilesManager {

    // Profile storage shared preferences key
    private final String PREF_PROFILES_STORE    =   "pref_profiles_store";
    private final String TAG                    =   "NL_ProfilesManager";

    private Context context;

    public ProfilesManager(Context context) {
        this.context = context;
    }

    /**
     * Listens to changes in data
     */
    public interface DataChangeListener {
        /**
         * Callback when data change occurs
         * @param newDataSize Size of new data
         */
        void onDataChanged(int newDataSize);
    }

    private DataChangeListener dataChangeListener;

    public void registerDataChangeListener(DataChangeListener dataChangeListener) {
        this.dataChangeListener = dataChangeListener;
    }

    // Profiles will be stored as string in a set in SP
    private Set<String> profilesSet;

    /**
     * Loads profiles from Shared Preferences
     */
    public void loadProfiles() {
        profilesSet = PreferenceManager.getDefaultSharedPreferences(context)
                .getStringSet(PREF_PROFILES_STORE, null);

        dataChangeListener.onDataChanged(profilesSet == null ? 0: profilesSet.size());
    }

    /**
     * Saves profiles to Shared Preferences
     */
    public void storeProfiles() {
        if (profilesSet == null || profilesSet.size() < 1) return;

        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putStringSet(PREF_PROFILES_STORE, profilesSet)
                .apply();
    }

    /**
     * Creates a new profile, stores it in local set and then writes it to SP
     * @param name Name of profile
     * @param mode Setting mode of profile
     * @param settings Settings of profile
     * @return A boolean indicating whether profile creation was successful or not
     */
    public boolean createProfile(String name, int mode, int[] settings) {
        if (isDuplicate(name)) return false;
        Profile profile = new Profile();

        profile.setName(name);
        profile.setSettingMode(mode);
        profile.setSettings(settings);

        // Add it to set
        if (profilesSet == null) profilesSet = new HashSet<>();
        profilesSet.add(profile.toProfileString());

        storeProfiles();
        dataChangeListener.onDataChanged(profilesSet.size());
        return true;
    }

    /**
     * Checks whether an entry with given name is duplicate or not
     * @param name Name
     * @return A boolean indicating whether entry is duplicate
     */
    private boolean isDuplicate(String name) {
        for (String p: profilesSet)
            if (parseProfile(p).getName().equals(name)) return false;
        return true;
    }

    /**
     * Deletes a profile with given name, and then updates the changes in SP
     * @param name Name of profile to be removed
     */
    public void deleteProfile(String name) {
        String profileToBeRemoved = null;

        for (String profile: profilesSet) {
            Profile p = parseProfile(profile);
            if (name.equals(p.getName())) {
                profileToBeRemoved = profile;
                break;
            }
        }

        if (profileToBeRemoved != null) profilesSet.remove(profileToBeRemoved);
        storeProfiles();
        dataChangeListener.onDataChanged(profilesSet == null ? 0: profilesSet.size());
    }

    /**
     * Updates an existing profile with new params
     * @param oldName Old name of profile to be update
     * @param newName New name
     * @param newMode New mode
     * @param newSettings New settings
     */
    public void updateProfile(String oldName, String newName, int newMode, int newSettings[]) {
        deleteProfile(oldName);
        createProfile(newName, newMode, newSettings);
    }

    /**
     * Applies profile setting
     * @param name Name of profile to be applied
     */
    public void applyProfile(String name) {
        // Find the profile with name at first
        for (String profile: profilesSet) {
            Profile p = parseProfile(profile);
            if (name.equals(p.getName())) {
                p.apply();
                break;
            }
        }
    }

    public ArrayList<Profile> getProfilesList() {
        ArrayList<Profile> list = new ArrayList<>();
        for (String p:profilesSet) list.add(parseProfile(p));
        return list;
    }

    // Profiles

    // Profile model
    public class Profile {
        // Unique name
        private String name;

        // Setting switch
        boolean settingEnabled;

        // Setting mode
        private int settingMode;

        // Settings depending on the mode
        private int[] settings;

        public int getSettingMode() {
            return settingMode;
        }

        public int[] getSettings() {
            return settings;
        }

        public boolean isSettingEnabled() {
            return settingEnabled;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setSettingMode(int settingMode) {
            this.settingMode = settingMode;
        }

        public void setSettings(int[] settings) {
            this.settings = settings;
        }

        public void setSettingEnabled(boolean settingEnabled) {
            this.settingEnabled = settingEnabled;
        }

        // This is how the profile will be stored as string in SP
        // For example -> Name - Profile, SettingMode - 1, settings - {100}
        // Will look like -> "Profile; 1; [100]"
        // DO NOT alter the sequence of data
        // In future if other fields are added, simply append at the end
        public String toProfileString(){
            return name + ";" + settingEnabled + ";" + settingMode + ";" + Arrays.toString(settings);
        }

        public void apply() {
            Core.applyNightModeAsync(settingEnabled, settingMode, settings);
        }
    }

    private Profile parseProfile(String profileString) {
        String parts[] = profileString.split(";");
        Profile profile = new Profile();

        try {
            profile.setName(parts[0]);
            profile.setSettingEnabled(Boolean.parseBoolean(parts[1]));
            profile.setSettingMode(Integer.parseInt(parts[2]));
            profile.setSettings(StringUtils.stringToIntArray(parts[3]));
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.e(TAG, "Profile API mismatch, resultant -> " + profile.toProfileString());
        }

        return profile;
    }
}
