package com.corphish.nightlight.design.fragments

import android.app.Activity
import android.content.Context
import com.google.android.material.bottomsheet.BottomSheetDialog
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar

import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.engine.Core
import com.corphish.nightlight.helpers.PreferenceHelper
import com.corphish.nightlight.R
import com.corphish.nightlight.design.utils.FontUtils
import com.gregacucnik.EditableSeekBar
import kotlinx.android.synthetic.main.card_master_switch.*

/**
 * Created by Avinaba on 10/23/2017.
 * Master switch fragment
 */

class MasterSwitchFragment : Fragment() {

    private var mCallback: MasterSwitchClickListener? = null
    internal var enabled: Boolean = false

    private lateinit var kcalBackupSettingsView: View
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private var r: Int = 0
    private var g: Int = 0
    private var b: Int = 0

    interface MasterSwitchClickListener {
        fun onSwitchClicked(checkStatus: Boolean)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enabled = PreferenceHelper.getBoolean(context, Constants.PREF_MASTER_SWITCH)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (context as Activity) as MasterSwitchClickListener?
        } catch (e: ClassCastException) {
            throw ClassCastException(activity!!.toString() + " must implement MasterSwitchClickListener")
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.card_master_switch, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        masterSwitch.isChecked = enabled
        masterSwitch.setOnCheckedChangeListener { _, b ->
            Core.applyNightModeAsync(b, context)
            PreferenceHelper.putBoolean(context, Constants.PREF_MASTER_SWITCH, b)
            if (mCallback != null) mCallback!!.onSwitchClicked(b)
        }

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
            bottomSheetDialog = BottomSheetDialog(context, R.style.BottomSheetDialogDark)
            initKCALBackupView()
            bottomSheetDialog.setContentView(kcalBackupSettingsView)
            bottomSheetDialog.show()
        })

        FontUtils().setCustomFont(context!!, masterSwitch, preserveSwitch)
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
