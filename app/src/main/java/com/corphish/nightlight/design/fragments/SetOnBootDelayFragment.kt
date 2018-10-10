package com.corphish.nightlight.design.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar

import com.corphish.nightlight.R
import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.design.fragments.base.BaseBottomSheetDialogFragment
import com.corphish.nightlight.helpers.PreferenceHelper
import com.gregacucnik.EditableSeekBar
import kotlinx.android.synthetic.main.layout_set_on_boot_delay.*

/**
 * Created by avinabadalal on 13/02/18.
 * Set on boot delay fragment
 */

class SetOnBootDelayFragment : BaseBottomSheetDialogFragment() {
    private var bootDelay: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bootDelay = PreferenceHelper.getInt(context, Constants.PREF_BOOT_DELAY, Constants.DEFAULT_BOOT_DELAY)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.layout_set_on_boot_delay, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setOnBootDesc.text = getString(R.string.set_on_boot_delay_desc, bootDelay.toString() + "s")

        setOnBootDelay.setOnEditableSeekBarChangeListener(object : EditableSeekBar.OnEditableSeekBarChangeListener {
            override fun onEditableSeekBarProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                bootDelay = seekBar.progress
                setOnBootDesc.text = getString(R.string.set_on_boot_delay_desc, bootDelay.toString() + "s")
                PreferenceHelper.putInt(context, Constants.PREF_BOOT_DELAY, bootDelay)
            }

            override fun onEnteredValueTooHigh() {
                setOnBootDelay.value = 60
            }

            override fun onEnteredValueTooLow() {
                setOnBootDelay.value = 0
            }

            override fun onEditableSeekBarValueChanged(value: Int) {
                bootDelay = value
                setOnBootDesc.text = getString(R.string.set_on_boot_delay_desc, bootDelay.toString() + "s")
                PreferenceHelper.putInt(context, Constants.PREF_BOOT_DELAY, bootDelay)
            }
        })

        setOnBootDelay.value = bootDelay

        setOnBootWarn.visibility = if (PreferenceHelper.getBoolean(context, Constants.PREF_LAST_BOOT_RES, true)) View.GONE else View.VISIBLE
    }
}
