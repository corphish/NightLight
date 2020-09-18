package com.corphish.nightlight.design.fragments

import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.corphish.nightlight.R
import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.design.ThemeUtils
import com.corphish.nightlight.helpers.PreferenceHelper
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.gregacucnik.EditableSeekBar

class KCALBackupSettingsFragment: PreferenceFragmentCompat() {

    private lateinit var kcalBackupSettingsView: View
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private var r: Int = 0
    private var g: Int = 0
    private var b: Int = 0

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
        setPreferencesFromResource(R.xml.kcal_backup_preferences, rootKey)

        // Initialise old KCAL values
        val backedUpValues = PreferenceHelper.getString(context, Constants.KCAL_PRESERVE_VAL)

        if (backedUpValues == null) {
            b = 256
            g = b
            r = g
        } else {
            val parts = backedUpValues.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            r = Integer.parseInt(parts[0])
            g = Integer.parseInt(parts[1])
            b = Integer.parseInt(parts[2])
        }

        updateConfigSummary()

        findPreference<Preference>(Constants.KCAL_PRESERVE_VAL)?.setOnPreferenceClickListener { _ ->
            bottomSheetDialog = BottomSheetDialog(requireContext(), ThemeUtils.getBottomSheetTheme(requireContext()))
            initKCALBackupView()
            bottomSheetDialog.setContentView(kcalBackupSettingsView)
            bottomSheetDialog.show()

            true
        }
    }

    /**
     * Builds and shows the dialog for setting KCAL
     * backup values.
     *
     */
    private fun initKCALBackupView() {
        kcalBackupSettingsView = View.inflate(context, R.layout.bottom_sheet_kcal_backup_set, null)

        val red = kcalBackupSettingsView.findViewById<EditableSeekBar>(R.id.red)
        val green = kcalBackupSettingsView.findViewById<EditableSeekBar>(R.id.green)
        val blue = kcalBackupSettingsView.findViewById<EditableSeekBar>(R.id.blue)

        red.value = r
        green.value = g
        blue.value = b

        red.setOnEditableSeekBarChangeListener(object : EditableSeekBar.OnEditableSeekBarChangeListener {
            override fun onEditableSeekBarProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                r = seekBar.progress
            }

            override fun onEnteredValueTooHigh() {
                red.value = 255
            }

            override fun onEnteredValueTooLow() {
                red.value = 0
            }

            override fun onEditableSeekBarValueChanged(value: Int) {
                r = value
            }
        })

        green.setOnEditableSeekBarChangeListener(object : EditableSeekBar.OnEditableSeekBarChangeListener {
            override fun onEditableSeekBarProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                g = seekBar.progress
            }

            override fun onEnteredValueTooHigh() {
                green.value = 256
            }

            override fun onEnteredValueTooLow() {
                green.value = 0
            }

            override fun onEditableSeekBarValueChanged(value: Int) {
                g = value
            }
        })

        blue.setOnEditableSeekBarChangeListener(object : EditableSeekBar.OnEditableSeekBarChangeListener {
            override fun onEditableSeekBarProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                b = seekBar.progress
            }

            override fun onEnteredValueTooHigh() {
                blue.value = 256
            }

            override fun onEnteredValueTooLow() {
                blue.value = 0
            }

            override fun onEditableSeekBarValueChanged(value: Int) {
                b = value
            }
        })


        kcalBackupSettingsView.findViewById<View>(R.id.button_cancel).setOnClickListener { bottomSheetDialog.dismiss() }

        kcalBackupSettingsView.findViewById<View>(R.id.button_ok).setOnClickListener {
            updateConfigSummary()
            PreferenceHelper.putString(context, Constants.KCAL_PRESERVE_VAL, "$r $g $b")
            bottomSheetDialog.dismiss()
        }
    }

    /**
     * Updates the configure preference summary.
     */
    private fun updateConfigSummary() {
        findPreference<Preference>(Constants.KCAL_PRESERVE_VAL)?.summary = "RGB($r, $g, $b)"
    }
}