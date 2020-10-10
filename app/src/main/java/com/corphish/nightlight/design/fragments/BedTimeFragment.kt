package com.corphish.nightlight.design.fragments

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.corphish.nightlight.R
import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.engine.Core
import com.corphish.widgets.ktx.dialogs.MessageAlertDialog

/**
 * Fragment to show bed time switch and tutorial.
 */
class BedTimeFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.bed_time_preferences, rootKey)

        // Switch
        val windDown = findPreference<SwitchPreferenceCompat>(Constants.PREF_WIND_DOWN)
        windDown?.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, value ->
            val bedTimeSwitchStatus = value as Boolean
            Core.applyGrayScaleAsync(bedTimeSwitchStatus, requireContext())
            true
        }

        // Tutorial
        findPreference<Preference>("wind_down_tutorial")?.setOnPreferenceClickListener {
            MessageAlertDialog(requireContext()).apply {
                titleResId = R.string.bed_time_title
                messageResId = R.string.bed_time_desc
                positiveButtonProperties = MessageAlertDialog.ButtonProperties(
                        buttonTitleResId = android.R.string.ok,
                        dismissDialogOnButtonClick = true
                )
            }.show()

            true
        }
    }
}
