package com.corphish.nightlight.engine;

import android.content.Context;
import android.preference.PreferenceManager;

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
    private ArrayList<Profile> list;

    /**
     * Loads profiles from Shared Preferences
     */
    public void loadProfiles() {
        if (profilesSet != null) profilesSet.clear();
        profilesSet = PreferenceManager.getDefaultSharedPreferences(context)
                .getStringSet(PREF_PROFILES_STORE, null);

        if (dataChangeListener != null) dataChangeListener.onDataChanged(profilesSet == null ? 0: profilesSet.size());
    }

    /**
     * Saves profiles to Shared Preferences
     */
    public void storeProfiles() {
        if (profilesSet == null) {
            return;
        }

        // First remove existing entries
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .remove(PREF_PROFILES_STORE)
                .apply();

        if (profilesSet.size() < 1) {
            return;
        }

        // Then save it
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putStringSet(PREF_PROFILES_STORE, profilesSet)
                .apply();
    }

    /**
     * Creates a new profile, stores it in local set and then writes it to SP
     * @param enabled Whether the setting should be enabled or not
     * @param name Name of profile
     * @param mode Setting mode of profile
     * @param settings Settings of profile
     * @return A boolean indicating whether profile creation was successful or not
     */
    public boolean createProfile(boolean enabled, String name, int mode, int[] settings) {
        if (isDuplicate(name)) return false;
        Profile profile = new Profile();

        profile.setSettingEnabled(enabled);
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
        if (profilesSet == null) return false;
        for (String p: profilesSet)
            if (parseProfile(p).getName().equals(name)) return true;
        return false;
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
     * @param enabled Whether setting should be enabled or not
     * @param newName New name
     * @param newMode New mode
     * @param newSettings New settings
     * @return Whether profile update was successful or not
     */
    public boolean updateProfile(String oldName, boolean enabled, String newName, int newMode, int newSettings[]) {
        // Backup the original profile JIC
        Profile profile = getProfileByName(oldName);

        deleteProfile(oldName);

        boolean ret = createProfile(enabled, newName, newMode, newSettings);
        if (!ret) {
            // Add back the old profile
            createProfile(profile.settingEnabled, profile.getName(), profile.getSettingMode(), profile.getSettings());
            return false;
        }

        return true;
    }

    public ArrayList<Profile> getProfilesList() {
        if (list == null) list = new ArrayList<> ();
        list.clear();

        if (profilesSet == null) return list;

        for (String p:profilesSet) list.add(parseProfile(p));

        return list;
    }

    /**
     * Searches and returns the profile by given name
     * @param name Name
     * @return Required profile as raw string
     */
    public String getRawProfileByName(String name) {
        for (String p: profilesSet) if (p.contains(name)) return p;
        return null;
    }

    /**
     * Searches and returns the profile by given name
     * @param name Name
     * @return Required profile as Profile object
     */
    public Profile getProfileByName(String name) {
        return parseProfile(getRawProfileByName(name));
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

        public void apply(Context context) {
            Core.applyNightModeAsync(settingEnabled, context, settingMode, settings);
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
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }

        return profile;
    }
}
