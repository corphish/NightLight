package com.corphish.nightlight.design.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar

import kotlinx.android.synthetic.main.layout_filter_intensity.*

import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.engine.Core
import com.corphish.nightlight.helpers.PreferenceHelper
import com.corphish.nightlight.R
import com.corphish.nightlight.design.utils.FontUtils
import com.corphish.nightlight.helpers.TimeUtils
import com.corphish.nightlight.services.NightLightAppService
import kotlinx.android.synthetic.main.layout_temperature.*
import com.gregacucnik.EditableSeekBar

/**
 * Created by Avinaba on 10/23/2017.
 * Filter fragment
 */

class ColorControlFragment : BaseBottomSheetDialogFragment() {
    /**
     * Called when the fragment is no longer in use.  This is called
     * after [.onStop] and before [.onDetach].
     */
    override fun onDestroy() {
        super.onDestroy()

        val autoEnabled = PreferenceHelper.getBoolean(context, Constants.PREF_AUTO_SWITCH, false)
        val startTime = PreferenceHelper.getString(context, Constants.PREF_START_TIME, Constants.DEFAULT_START_TIME)
        val endTime = PreferenceHelper.getString(context, Constants.PREF_END_TIME, Constants.DEFAULT_END_TIME)

        Core.applyNightModeAsync(
                !autoEnabled || (autoEnabled && startTime != null && endTime != null && TimeUtils.determineWhetherNLShouldBeOnOrNot(startTime, endTime)),
                context
        )
    }

    private var blueIntensity: Int = 0
    private var greenIntensity: Int = 0
    private var colorTemperature: Int = 0
    private var mode: Int = Constants.NL_SETTING_MODE_FILTER

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getValues()
        mode = PreferenceHelper.getInt(context, Constants.PREF_SETTING_MODE, Constants.NL_SETTING_MODE_FILTER)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.layout_color_control, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initIntensityViews()
        initTemperatureViews()
    }

    private fun initIntensityViews() {
        FontUtils().setCustomFont(context!!, intensityModeSwitch)

        // Disable them by default
        blueSlider.isEnabled = false
        greenSlider.isEnabled = false

        intensityModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            mode = Constants.NL_SETTING_MODE_FILTER

            val settingMode = if (isChecked) Constants.NL_SETTING_MODE_FILTER else Constants.NL_SETTING_MODE_TEMP

            PreferenceHelper.putInt(context, Constants.PREF_SETTING_MODE, settingMode)

            blueSlider.isEnabled = isChecked
            greenSlider.isEnabled = isChecked

            if (isChecked && NightLightAppService.instance.isInitDone()) {
                Core.applyNightModeAsync(isChecked, context, blueIntensity, greenIntensity)
            }

            temperatureModeSwitch.isChecked = !isChecked
        }

        intensityModeSwitch.isChecked = mode == Constants.NL_SETTING_MODE_FILTER

        blueSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {}

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                blueIntensity = seekBar.progress
                if (NightLightAppService.instance.isInitDone()) {
                    PreferenceHelper.putInt(context, Constants.PREF_BLUE_INTENSITY, blueIntensity)
                    Core.applyNightModeAsync(true, context, blueIntensity, greenIntensity)
                    PreferenceHelper.putInt(context, Constants.PREF_CUR_APPLY_TYPE, Constants.APPLY_TYPE_NON_PROFILE)
                }
            }
        })

        greenSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                greenIntensity = seekBar.progress
                if (NightLightAppService.instance.isInitDone()) {
                    PreferenceHelper.putInt(context, Constants.PREF_GREEN_INTENSITY, greenIntensity)
                    Core.applyNightModeAsync(true, context, blueIntensity, greenIntensity)
                    PreferenceHelper.putInt(context, Constants.PREF_CUR_APPLY_TYPE, Constants.APPLY_TYPE_NON_PROFILE)
                }
            }
        })

        blueSlider.progress = blueIntensity
        greenSlider.progress = greenIntensity
    }

    private fun initTemperatureViews() {
        FontUtils().setCustomFont(context!!, temperatureModeSwitch)

        // Disable them by default
        temperatureValue.isEnabled = false

        temperatureModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            mode = Constants.NL_SETTING_MODE_TEMP

            val settingMode = if (isChecked) Constants.NL_SETTING_MODE_TEMP else Constants.NL_SETTING_MODE_FILTER

            PreferenceHelper.putInt(context, Constants.PREF_SETTING_MODE, settingMode)

            temperatureValue.isEnabled = isChecked

            if (isChecked && NightLightAppService.instance.isInitDone()) {
                Core.applyNightModeAsync(true, context, colorTemperature)
            }

            intensityModeSwitch.isChecked = !isChecked
        }

        temperatureModeSwitch.isChecked = mode == Constants.NL_SETTING_MODE_TEMP

        temperatureValue.setOnEditableSeekBarChangeListener(object : EditableSeekBar.OnEditableSeekBarChangeListener {
            override fun onEditableSeekBarProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {}

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
    }

    fun onStateChanged(newMode: Int) {
        if (intensityModeSwitch != null) intensityModeSwitch.isChecked = newMode == Constants.NL_SETTING_MODE_FILTER
        if (temperatureModeSwitch != null) temperatureModeSwitch.isChecked = newMode == Constants.NL_SETTING_MODE_TEMP
    }

    private fun getValues() {
        blueIntensity = PreferenceHelper.getInt(context, Constants.PREF_BLUE_INTENSITY, Constants.DEFAULT_BLUE_INTENSITY)
        greenIntensity = PreferenceHelper.getInt(context, Constants.PREF_GREEN_INTENSITY, Constants.DEFAULT_GREEN_INTENSITY)
        colorTemperature = PreferenceHelper.getInt(context, Constants.PREF_COLOR_TEMP, Constants.DEFAULT_COLOR_TEMP)
    }
}
