package com.corphish.nightlight.engine

import android.content.Context
import androidx.preference.PreferenceManager
import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.extensions.toArrayOfInts

class ProfilesManager(private val context: Context) {

    private val _prefKey = "pref_profiles_store"

    // Migration Data
    // This can only migrate apply type, any mismatch in param format will be discarded
    // Also, migration can only take place between consecutive API version (example, from 0 - 1. Direct 0 - 2 should not be possible, this could be done by 0 - 1 and then 1 - 2)
    // A null pair indicates migration is not possible
    // The 'from API version' value is the index of the lis, and 'to API version' value is value (nullable) at that index
    private val migrationData = listOf(
            // Migration data of API version 0 to 1
            // Migration of apply type 0 (formerly Filter Intensity) is not possible
            // Migration of apply type 1 (formerly Color Temperature) is possible to value 0
            listOf(null, 0)
    )

    /**
     * Listens to changes in data
     */
    interface DataChangeListener {
        /**
         * Callback when data change occurs
         * @param newDataSize Size of new data
         */
        fun onDataChanged(newDataSize: Int)
    }

    private var dataChangeListener: DataChangeListener? = null

    fun registerDataChangeListener(dataChangeListener: DataChangeListener) {
        this.dataChangeListener = dataChangeListener
        dataChangeListener.onDataChanged(profilesList.size)
    }

    // Profiles set maybe null (when set is empty) but will not contain null values
    val profilesList: MutableList<Profile> = mutableListOf()

    fun loadProfiles() {
        profilesList.clear()

        val savedSet = PreferenceManager.getDefaultSharedPreferences(context)
                .getStringSet(_prefKey, null)

        if (savedSet == null || savedSet.isEmpty()) return

        for (entry in savedSet) {
            val profile = parseProfile(entry)
            if (profile != null) {
                if (profile.apiVersion == Constants.PROFILE_API_VERSION) {
                    profilesList.add(profile)
                } else {
                    val migratedProfile = migrateProfile(profile, profile.apiVersion)

                    if (migratedProfile != null)
                        profilesList.add(migratedProfile)
                }
            }
        }

        dataChangeListener?.onDataChanged(profilesList.size)
    }

    private fun migrateProfile(profile: Profile, fromVersion: Int): Profile? {
        // Avoid erroneous API version
        if (fromVersion >= migrationData.size)
            return null

        for (i in fromVersion until Constants.PROFILE_API_VERSION) {
            val data = migrationData[i]

            val prevType = profile.settingMode
            if (prevType >= data.size) return null

            val newType = data[prevType] ?: return null

            profile.settingMode = newType
            profile.apiVersion = i + 1
        }

        return profile
    }

    private fun storeProfiles() {
        // Need string set for shared pref
        val stringSet: MutableSet<String> = hashSetOf()

        profilesList.forEach { profile -> stringSet += profile.toProfileString() }
        dataChangeListener?.onDataChanged(profilesList.size)

        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putStringSet(_prefKey, stringSet)
                .apply()
    }

    /**
     * Creates a new profile, stores it in local set and then writes it to SP
     * @param enabled Whether the setting should be enabled or not
     * @param name Name of profile
     * @param mode Setting mode of profile
     * @param settings Settings of profile
     * @return A boolean indicating whether profile creation was successful or not
     */
    fun createProfile(enabled: Boolean, name: String, mode: Int, settings: IntArray): Boolean {
        val ret = profilesList.add(Profile(name, enabled, mode, settings))

        if (ret) storeProfiles()

        return ret
    }

    /**
     * Deletes a profile with given name, and then updates the changes in SP
     * @param profile Profile to be deleted
     */
    fun deleteProfile(profile: Profile) : Boolean {
        val ret = profilesList.remove(profile)
        storeProfiles()

        return ret
    }

    /**
     * Updates an existing profile with new params
     * @param oldProfile Old  profile to be updated
     * @param enabled Whether setting should be enabled or not
     * @param newName New name
     * @param newMode New mode
     * @param newSettings New settings
     * @return Whether profile update was successful or not
     */
    fun updateProfile(oldProfile: Profile, enabled: Boolean, newName: String, newMode: Int, newSettings: IntArray): Boolean {
        val ret = deleteProfile(oldProfile)
        if (!ret) return false

        createProfile(enabled, newName, newMode, newSettings)

        return ret
    }

    fun getProfileByName(name: String) : Profile? {
        var profile: Profile? = null
        for (p in profilesList)
            if (p.name == name) profile = p

        return profile
    }

    // Profile Model
    data class Profile(var name: String,
                       var isSettingEnabled: Boolean = false,
                       var settingMode: Int = 0,
                       var settings: IntArray,
                       var apiVersion: Int = Constants.PROFILE_API_VERSION) : Comparable<Profile> {
        // This is how the profile will be stored as string in SP
        // For example -> Name - Profile, SettingMode - 1, settings - {100}
        // Will look like -> "Profile; 1; [100]"
        // DO NOT alter the sequence of data
        // In future if other fields are added, simply append at the end
        fun toProfileString() = "$name;$isSettingEnabled;$settingMode;${settings.contentToString()};$apiVersion"

        fun apply(context: Context) {
            Core.applyNightModeAsync(isSettingEnabled, context, settingMode, settings)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other?.javaClass != javaClass) return false

            other as Profile

            return other.name == this.name &&
                    other.isSettingEnabled == this.isSettingEnabled &&
                    other.settingMode == settingMode &&
                    other.settings.contentEquals(this.settings) &&
                    other.apiVersion == this.apiVersion
        }

        override fun hashCode(): Int {
            return this.name.hashCode()
        }

        override fun compareTo(other: Profile): Int {
            return this.name.compareTo(other.name)
        }
    }

    private fun parseProfile(profileString: String): Profile? {
        val parts = profileString.split(";".toRegex())

        return when (parts.size) {
            4 -> Profile(parts[0], parts[1].toBoolean(), parts[2].toInt(), parts[3].toArrayOfInts(","), 0)
            5 -> Profile(parts[0], parts[1].toBoolean(), parts[2].toInt(), parts[3].toArrayOfInts(","), parts[4].toInt())
            else -> null
        }
    }
}