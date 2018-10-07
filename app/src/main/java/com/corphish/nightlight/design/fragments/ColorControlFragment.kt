package com.corphish.nightlight.design.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar

import kotlinx.android.synthetic.main.layout_manual_colors.*

import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.engine.Core
import com.corphish.nightlight.helpers.PreferenceHelper
import com.corphish.nightlight.R
import com.corphish.nightlight.design.utils.FontUtils
import com.corphish.nightlight.services.NightLightAppService
import kotlinx.android.synthetic.main.layout_temperature.*
import com.gregacucnik.EditableSeekBar

/**
 * Created by Avinaba on 10/23/2017.
 * Filter fragment
 */

class ColorControlFragment : BaseBottomSheetDialogFragment() {
    private var redColor: Int = 0
    private var greenColor: Int = 0
    private var blueColor: Int = 0
    private var colorTemperature: Int = 0
    private var mode: Int = Constants.NL_SETTING_MODE_TEMP
    private var state: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getValues()
        mode = PreferenceHelper.getInt(context, Constants.PREF_SETTING_MODE, Constants.NL_SETTING_MODE_TEMP)
        state = PreferenceHelper.getBoolean(context, Constants.PREF_FORCE_SWITCH, false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.layout_color_control, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initManualViews()
        initTemperatureViews()
    }

    private fun initManualViews() {
        FontUtils().setCustomFont(context!!, manualModeSwitch)

        // Disable them by default
        red.isEnabled = false
        green.isEnabled = false
        blue.isEnabled = false

        manualModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            mode = Constants.NL_SETTING_MODE_MANUAL

            val settingMode = if (isChecked) Constants.NL_SETTING_MODE_MANUAL else Constants.NL_SETTING_MODE_TEMP

            PreferenceHelper.putInt(context, Constants.PREF_SETTING_MODE, settingMode)

            red.isEnabled = isChecked
            green.isEnabled = isChecked
            blue.isEnabled = isChecked

            if (isChecked && NightLightAppService.instance.isInitDone()) {
                Core.applyNightModeAsync(isChecked, context, redColor, greenColor, blueColor)
            }

            temperatureModeSwitch.isChecked = !isChecked
        }

        red.setOnEditableSeekBarChangeListener(object : EditableSeekBar.OnEditableSeekBarChangeListener {
            override fun onEditableSeekBarProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

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
                    PreferenceHelper.putInt(context, Constants.PREF_RED_COLOR, redColor)
                    Core.applyNightModeAsync(true, context, redColor, greenColor, blueColor)
                    PreferenceHelper.putInt(context, Constants.PREF_CUR_APPLY_TYPE, Constants.APPLY_TYPE_NON_PROFILE)
                }
            }
        })

        green.setOnEditableSeekBarChangeListener(object : EditableSeekBar.OnEditableSeekBarChangeListener {
            override fun onEditableSeekBarProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

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
                    PreferenceHelper.putInt(context, Constants.PREF_GREEN_COLOR, greenColor)
                    Core.applyNightModeAsync(true, context, redColor, greenColor, blueColor)
                    PreferenceHelper.putInt(context, Constants.PREF_CUR_APPLY_TYPE, Constants.APPLY_TYPE_NON_PROFILE)
                }
            }
        })

        blue.setOnEditableSeekBarChangeListener(object : EditableSeekBar.OnEditableSeekBarChangeListener {
            override fun onEditableSeekBarProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

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
                    PreferenceHelper.putInt(context, Constants.PREF_BLUE_COLOR, blueColor)
                    Core.applyNightModeAsync(true, context, redColor, greenColor, blueColor)
                    PreferenceHelper.putInt(context, Constants.PREF_CUR_APPLY_TYPE, Constants.APPLY_TYPE_NON_PROFILE)
                }
            }
        })

        red.value = redColor
        green.value = greenColor
        blue.value = blueColor

        manualModeSwitch.isChecked = mode == Constants.NL_SETTING_MODE_MANUAL
    }

    private fun initTemperatureViews() {
        FontUtils().setCustomFont(context!!, temperatureModeSwitch)

        // Disable them by default
        temperatureValue.isEnabled = false

        temperatureModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            mode = Constants.NL_SETTING_MODE_TEMP

            val settingMode = if (isChecked) Constants.NL_SETTING_MODE_TEMP else Constants.NL_SETTING_MODE_MANUAL

            PreferenceHelper.putInt(context, Constants.PREF_SETTING_MODE, settingMode)

            temperatureValue.isEnabled = isChecked

            if (isChecked && NightLightAppService.instance.isInitDone()) {
                Core.applyNightModeAsync(true, context, colorTemperature)
            }

            manualModeSwitch.isChecked = !isChecked
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
        if (manualModeSwitch != null) manualModeSwitch.isChecked = newMode == Constants.NL_SETTING_MODE_MANUAL
        if (temperatureModeSwitch != null) temperatureModeSwitch.isChecked = newMode == Constants.NL_SETTING_MODE_TEMP
    }

    private fun getValues() {
        redColor = PreferenceHelper.getInt(context, Constants.PREF_RED_COLOR, Constants.DEFAULT_RED_COLOR)
        greenColor = PreferenceHelper.getInt(context, Constants.PREF_GREEN_COLOR, Constants.DEFAULT_GREEN_COLOR)
        blueColor = PreferenceHelper.getInt(context, Constants.PREF_BLUE_COLOR, Constants.DEFAULT_BLUE_COLOR)
        colorTemperature = PreferenceHelper.getInt(context, Constants.PREF_COLOR_TEMP, Constants.DEFAULT_COLOR_TEMP)
    }

    /**
     * Called when the fragment is no longer in use.  This is called
     * after [.onStop] and before [.onDetach].
     */
    override fun onDestroy() {
        super.onDestroy()

        Core.fixNightMode(context, state)
    }
}
