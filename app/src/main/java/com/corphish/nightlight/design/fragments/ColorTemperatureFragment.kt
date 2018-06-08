package com.corphish.nightlight.design.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SwitchCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar

import com.corphish.nightlight.R
import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.design.utils.FontUtils
import com.corphish.nightlight.engine.Core
import com.corphish.nightlight.helpers.PreferenceHelper
import com.corphish.nightlight.services.NightLightAppService

/**
 * Created by avinabadalal on 12/02/18.
 * Color temperature fragment
 */

class ColorTemperatureFragment : Fragment() {
    private var colorTemperature: Int = 0

    private var mode: Boolean = false

    // Views
    private var switchCompat: SwitchCompat? = null
    private var seekBar: SeekBar? = null

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

        switchCompat = view!!.findViewById(R.id.mode_switch)
        seekBar = view!!.findViewById(R.id.temperature_value)

        FontUtils().setCustomFont(context!!, switchCompat)

        // Disable them by default
        seekBar!!.isEnabled = false

        switchCompat!!.setOnCheckedChangeListener { _, isChecked ->
            mode = isChecked

            val settingMode = if (isChecked) Constants.NL_SETTING_MODE_TEMP else Constants.NL_SETTING_MODE_FILTER

            PreferenceHelper.putInt(context, Constants.PREF_SETTING_MODE, settingMode)

            seekBar!!.isEnabled = isChecked

            if (isChecked && NightLightAppService.instance.isInitDone()) {
                Core.applyNightModeAsync(true, context, colorTemperature + 3000)
            }

            NightLightAppService.instance.notifyNewSettingMode(settingMode)
        }

        switchCompat!!.isChecked = mode

        seekBar!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                colorTemperature = seekBar.progress
                colorTemperature = colorTemperature / 100 * 100
                if (NightLightAppService.instance.isInitDone()) {
                    PreferenceHelper.putInt(context, Constants.PREF_COLOR_TEMP, colorTemperature + 3000)
                    Core.applyNightModeAsync(true, context, colorTemperature + 3000)
                    PreferenceHelper.putInt(context, Constants.PREF_CUR_APPLY_TYPE, Constants.APPLY_TYPE_NON_PROFILE)
                }
            }
        })

        seekBar!!.progress = colorTemperature

        NightLightAppService.instance
                .incrementViewInitCount()
    }

    fun onStateChanged(newMode: Int) {
        if (switchCompat != null) switchCompat!!.isChecked = newMode == Constants.NL_SETTING_MODE_TEMP
    }

    private fun getValues() {
        colorTemperature = PreferenceHelper.getInt(context, Constants.PREF_COLOR_TEMP, Constants.DEFAULT_COLOR_TEMP) - 3000
    }
}
