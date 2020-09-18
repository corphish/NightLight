package com.corphish.nightlight.design.fragments

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.corphish.nightlight.R
import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.helpers.PreferenceHelper
import com.corphish.nightlight.services.NightLightAppService

/**
 * Shows app customisation options
 */
class OptionsFragment: PreferenceFragmentCompat() {
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
        setPreferencesFromResource(R.xml.settings_preference, rootKey)

        val lightThemePref: SwitchPreferenceCompat? = findPreference(Constants.PREF_LIGHT_THEME)
        lightThemePref?.setOnPreferenceChangeListener { _, newValue ->
            NightLightAppService.instance.notifyThemeChanged(newValue as Boolean)
            requireActivity().recreate()
            true
        }

        findPreference<ListPreference>(Constants.PREF_ICON_SHAPE)?.setOnPreferenceChangeListener { _, _ ->
            NightLightAppService.instance.notifyThemeChanged(PreferenceHelper.getBoolean(context, Constants.PREF_LIGHT_THEME, Constants.DEFAULT_LIGHT_THEME))
            requireActivity().recreate()
            true
        }
    }
}