package com.corphish.nightlight.design.fragments

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference

import com.corphish.nightlight.R
import com.corphish.nightlight.data.Constants

/**
 * Created by avinabadalal on 13/02/18.
 * Set on boot delay fragment
 */
class SetOnBootDelayFragment : PreferenceFragmentCompat() {
    /**
     * Called during [.onCreate] to supply the preferences for this fragment.
     * Subclasses are expected to call [.setPreferenceScreen] either
     * directly or via helper methods such as [.addPreferencesFromResource].
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state,
     * this is the state.
     * @param rootKey            If non-null, this preference fragment should be rooted at the
     * [PreferenceScreen] with this key.
     */
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.set_on_boot_delay_preference, rootKey)

        val delayPref = findPreference<SeekBarPreference>(Constants.PREF_BOOT_DELAY)
        delayPref?.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, newValue ->
            /**
             * Called when a preference has been changed by the user. This is called before the state
             * of the preference is about to be updated and before the state is persisted.
             *
             * @param preference The changed preference
             * @param newValue   The new value of the preference
             * @return `true` to update the state of the preference with the new value
             */
            preference.summary = getString(R.string.set_on_boot_delay_desc, newValue.toString() + "s")
            true
        }

        delayPref?.summary = getString(R.string.set_on_boot_delay_desc, delayPref?.value.toString() + "s")
    }
}
