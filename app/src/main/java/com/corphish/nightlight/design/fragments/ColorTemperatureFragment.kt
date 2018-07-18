package com.corphish.nightlight.design.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar

import kotlinx.android.synthetic.main.card_temperature.*

import com.corphish.nightlight.R
import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.design.utils.FontUtils
import com.corphish.nightlight.engine.Core
import com.corphish.nightlight.helpers.PreferenceHelper
import com.corphish.nightlight.services.NightLightAppService
import com.gregacucnik.EditableSeekBar

/**
 * Created by avinabadalal on 12/02/18.
 * Color temperature fragment
 */

class ColorTemperatureFragment : Fragment() {
    private var colorTemperature: Int = 0

    private var mode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getValues()
        mode = PreferenceHelper.getInt(context, Constants.PREF_SETTING_MODE, Constants.NL_SETTING_MODE_FILTER) == Constants.NL_SETTING_MODE_TEMP
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.card_temperature, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        FontUtils().setCustomFont(context!!, modeSwitch)

        // Disable them by default
        temperatureValue.isEnabled = false

        modeSwitch.setOnCheckedChangeListener { _, isChecked ->
            mode = isChecked

            val settingMode = if (isChecked) Constants.NL_SETTING_MODE_TEMP else Constants.NL_SETTING_MODE_FILTER

            PreferenceHelper.putInt(context, Constants.PREF_SETTING_MODE, settingMode)

            temperatureValue.isEnabled = isChecked

            if (isChecked && NightLightAppService.instance.isInitDone()) {
                Core.applyNightModeAsync(true, context, colorTemperature)
            }

            NightLightAppService.instance.notifyNewSettingMode(settingMode)
        }

        modeSwitch.isChecked = mode

        temperatureValue.setOnEditableSeekBarChangeListener(object : EditableSeekBar.OnEditableSeekBarChangeListener {
            override fun onEditableSeekBarProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                colorTemperature = seekBar.progress
                if (NightLightAppService.instance.isInitDone()) {
                    PreferenceHelper.putInt(context, Constants.PREF_COLOR_TEMP, colorTemperature)
                    Core.applyNightModeAsync(true, context, colorTemperature)
                    PreferenceHelper.putInt(context, Constants.PREF_CUR_APPLY_TYPE, Constants.APPLY_TYPE_NON_PROFILE)
                }
            }

            override fun onEnteredValueTooHigh() {
                temperatureValue.value = 4500
            }

            override fun onEnteredValueTooLow() {
                temperatureValue.value = 3000
            }

            override fun onEditableSeekBarValueChanged(value: Int) {
                colorTemperature = value
                if (NightLightAppService.instance.isInitDone()) {
                    PreferenceHelper.putInt(context, Constants.PREF_COLOR_TEMP, colorTemperature)
                    Core.applyNightModeAsync(true, context, colorTemperature)
                    PreferenceHelper.putInt(context, Constants.PREF_CUR_APPLY_TYPE, Constants.APPLY_TYPE_NON_PROFILE)
                }
            }
        })

        temperatureValue.value = colorTemperature

        NightLightAppService.instance
                .incrementViewInitCount()
    }

    fun onStateChanged(newMode: Int) {
        if (modeSwitch != null) modeSwitch.isChecked = newMode == Constants.NL_SETTING_MODE_TEMP
    }

    private fun getValues() {
        colorTemperature = PreferenceHelper.getInt(context, Constants.PREF_COLOR_TEMP, Constants.DEFAULT_COLOR_TEMP)
    }
}
