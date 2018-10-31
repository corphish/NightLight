package com.corphish.nightlight.design.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.corphish.nightlight.R
import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.design.ThemeUtils
import com.corphish.nightlight.design.fragments.base.BaseBottomSheetDialogFragment
import com.corphish.nightlight.design.utils.FontUtils
import com.corphish.nightlight.helpers.PreferenceHelper
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.layout_kcal_backup.*
import com.gregacucnik.EditableSeekBar

class KCALBackupSettingsFragment: BaseBottomSheetDialogFragment() {

    private lateinit var kcalBackupSettingsView: View
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private var r: Int = 0
    private var g: Int = 0
    private var b: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.layout_kcal_backup, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        preserveSwitch.isChecked = PreferenceHelper.getBoolean(context, Constants.KCAL_PRESERVE_SWITCH, true)
        preserveSwitch.setOnCheckedChangeListener { _, b ->
            PreferenceHelper.putBoolean(context, Constants.KCAL_PRESERVE_SWITCH, b)
            backupEveryTimeSwitch.isEnabled = b
        }

        backupEveryTimeSwitch.isChecked = PreferenceHelper.getBoolean(context, Constants.PREF_KCAL_BACKUP_EVERY_TIME, true)
        backupEveryTimeSwitch.setOnCheckedChangeListener {_, b ->
            PreferenceHelper.putBoolean(context, Constants.PREF_KCAL_BACKUP_EVERY_TIME, b)
        }


        configureKcalBackup.setOnClickListener(View.OnClickListener {
            val context = context ?: return@OnClickListener
            bottomSheetDialog = BottomSheetDialog(context, ThemeUtils.getBottomSheetTheme(context))
            initKCALBackupView()
            bottomSheetDialog.setContentView(kcalBackupSettingsView)
            bottomSheetDialog.show()
        })

        FontUtils().setCustomFont(context!!, preserveSwitch, backupEveryTimeSwitch)
    }

    private fun initKCALBackupView() {
        kcalBackupSettingsView = View.inflate(context, R.layout.bottom_sheet_kcal_backup_set, null)

        val backedUpValues = PreferenceHelper.getString(context, Constants.KCAL_PRESERVE_VAL)

        val red = kcalBackupSettingsView.findViewById<EditableSeekBar>(R.id.red)
        val green = kcalBackupSettingsView.findViewById<EditableSeekBar>(R.id.green)
        val blue = kcalBackupSettingsView.findViewById<EditableSeekBar>(R.id.blue)

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

        red.value = r
        green.value = g
        blue.value = b

        red.setOnEditableSeekBarChangeListener(object : EditableSeekBar.OnEditableSeekBarChangeListener {
            override fun onEditableSeekBarProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

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
            override fun onEditableSeekBarProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

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
            override fun onEditableSeekBarProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

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
            PreferenceHelper.putString(context, Constants.KCAL_PRESERVE_VAL, r.toString() + " " + g + " " + b)
            bottomSheetDialog.dismiss()
        }
    }
}