package com.corphish.nightlight.design.fragments

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.corphish.nightlight.R
import com.corphish.nightlight.engine.KCALManager

/**
 * Preference fragment screen to show KCAL driver info
 */
class KCALDriverInfoFragment : PreferenceFragmentCompat() {
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
        setPreferencesFromResource(R.xml.kcal_driver_screen, rootKey)

        // Populate KCAL driver values
        val kcalImplementation = KCALManager.implementation

        findPreference<Preference>("kcal_impl_name")?.summary = kcalImplementation.getImplementationName()
        findPreference<Preference>("kcal_impl_switch_path")?.summary = kcalImplementation.getImplementationSwitchPath()
        findPreference<Preference>("kcal_impl_rgb_path")?.summary = kcalImplementation.getImplementationFilePaths()
        findPreference<Preference>("kcal_format_spec")?.summary = kcalImplementation.getImplementationFormat()
        findPreference<Preference>("kcal_sat_path")?.summary = kcalImplementation.getSaturationPath()
    }
}