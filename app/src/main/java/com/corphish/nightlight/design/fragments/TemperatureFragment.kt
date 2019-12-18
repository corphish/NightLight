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

class TemperatureFragment: Fragment() {

    private val _type = Constants.NL_SETTING_MODE_TEMP

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_temperature, container, false)
        val temperatureValue = root.findViewById<EditableSeekBar>(R.id.temperatureValue)
        val colorTemperature = PreferenceHelper.getInt(context, Constants.PREF_COLOR_TEMP[_type], Constants.DEFAULT_COLOR_TEMP[_type])

        temperatureValue.setOnEditableSeekBarChangeListener(object : EditableSeekBar.OnEditableSeekBarChangeListener {
            override fun onEditableSeekBarProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}

            override fun onEnteredValueTooHigh() {
                temperatureValue.value = resources.getInteger(R.integer.maxTemp)
            }

            override fun onEnteredValueTooLow() {
                temperatureValue.value = resources.getInteger(R.integer.minTemp)
            }

            override fun onEditableSeekBarValueChanged(value: Int) {
                if (NightLightAppService.instance.isInitDone()) {
                    PreferenceHelper.putInt(context, Constants.PREF_COLOR_TEMP[_type], value)
                    Core.applyNightModeAsync(true, context, value)
                    PreferenceHelper.putInt(context, Constants.PREF_CUR_APPLY_TYPE, Constants.APPLY_TYPE_NON_PROFILE)
                }
            }
        })

        temperatureValue.value = colorTemperature
        PreferenceHelper.putInt(context, Constants.PREF_SETTING_MODE, _type)

        return root
    }
}