package com.corphish.nightlight.design.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.corphish.nightlight.R
import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.engine.Core
import com.corphish.nightlight.helpers.PreferenceHelper
import com.corphish.nightlight.services.NightLightAppService
import com.gregacucnik.EditableSeekBar

class ManualFragment : Fragment() {

    private val _type = Constants.NL_SETTING_MODE_MANUAL

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        var redColor = PreferenceHelper.getInt(context, Constants.PREF_RED_COLOR[_type], Constants.DEFAULT_RED_COLOR[_type])
        var greenColor = PreferenceHelper.getInt(context, Constants.PREF_GREEN_COLOR[_type], Constants.DEFAULT_GREEN_COLOR[_type])
        var blueColor = PreferenceHelper.getInt(context, Constants.PREF_BLUE_COLOR[_type], Constants.DEFAULT_BLUE_COLOR[_type])

        val root = inflater.inflate(R.layout.fragment_manual, container, false)

        val red = root.findViewById<EditableSeekBar>(R.id.red)
        val green = root.findViewById<EditableSeekBar>(R.id.green)
        val blue = root.findViewById<EditableSeekBar>(R.id.blue)



        red.setOnEditableSeekBarChangeListener(object : EditableSeekBar.OnEditableSeekBarChangeListener {
            override fun onEditableSeekBarProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}

            override fun onEnteredValueTooHigh() {
                red.value = 256
            }

            override fun onEnteredValueTooLow() {
                red.value = 0
            }

            override fun onEditableSeekBarValueChanged(value: Int) {
                redColor = value

                if (NightLightAppService.instance.isInitDone()) {
                    PreferenceHelper.putInt(context, Constants.PREF_RED_COLOR[Constants.NL_SETTING_MODE_MANUAL], redColor)
                    Core.applyNightModeAsync(true, context, redColor, greenColor, blueColor)
                    PreferenceHelper.putInt(context, Constants.PREF_CUR_APPLY_TYPE, Constants.APPLY_TYPE_NON_PROFILE)
                }
            }
        })

        green.setOnEditableSeekBarChangeListener(object : EditableSeekBar.OnEditableSeekBarChangeListener {
            override fun onEditableSeekBarProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}

            override fun onEnteredValueTooHigh() {
                green.value = 256
            }

            override fun onEnteredValueTooLow() {
                green.value = 0
            }

            override fun onEditableSeekBarValueChanged(value: Int) {
                greenColor = value

                if (NightLightAppService.instance.isInitDone()) {
                    PreferenceHelper.putInt(context, Constants.PREF_GREEN_COLOR[Constants.NL_SETTING_MODE_MANUAL], greenColor)
                    Core.applyNightModeAsync(true, context, redColor, greenColor, blueColor)
                    PreferenceHelper.putInt(context, Constants.PREF_CUR_APPLY_TYPE, Constants.APPLY_TYPE_NON_PROFILE)
                }
            }
        })

        blue.setOnEditableSeekBarChangeListener(object : EditableSeekBar.OnEditableSeekBarChangeListener {
            override fun onEditableSeekBarProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}

            override fun onEnteredValueTooHigh() {
                blue.value = 256
            }

            override fun onEnteredValueTooLow() {
                blue.value = 0
            }

            override fun onEditableSeekBarValueChanged(value: Int) {
                blueColor = value


                if (NightLightAppService.instance.isInitDone()) {
                    PreferenceHelper.putInt(context, Constants.PREF_BLUE_COLOR[Constants.NL_SETTING_MODE_MANUAL], blueColor)
                    Core.applyNightModeAsync(true, context, redColor, greenColor, blueColor)
                    PreferenceHelper.putInt(context, Constants.PREF_CUR_APPLY_TYPE, Constants.APPLY_TYPE_NON_PROFILE)
                }
            }
        })

        red.value = redColor
        green.value = greenColor
        blue.value = blueColor

        PreferenceHelper.putInt(context, Constants.PREF_SETTING_MODE, _type)

        return root
    }
}