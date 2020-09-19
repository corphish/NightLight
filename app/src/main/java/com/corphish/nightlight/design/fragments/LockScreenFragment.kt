package com.corphish.nightlight.design.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.corphish.nightlight.R
import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.engine.ForegroundServiceManager

/**
 * Shows lock screen options.
 */
class LockScreenFragment: PreferenceFragmentCompat() {
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
        setPreferencesFromResource(R.xml.lock_screen_preference, rootKey)

        val lockPref: SwitchPreferenceCompat? = findPreference(Constants.PREF_DISABLE_IN_LOCK_SCREEN)
        lockPref?.setOnPreferenceChangeListener { p, newValue ->
            ForegroundServiceManager.manageService(requireContext(), newValue as Boolean, p.key)
            true
        }
    }
}