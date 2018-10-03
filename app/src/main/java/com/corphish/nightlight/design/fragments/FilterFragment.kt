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
import com.corphish.nightlight.services.NightLightAppService

/**
 * Created by Avinaba on 10/23/2017.
 * Filter fragment
 */

class FilterFragment : BaseBottomSheetDialogFragment() {

    private var blueIntensity: Int = 0
    private var greenIntensity: Int = 0
    private var mode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getValues()
        mode = PreferenceHelper.getInt(context, Constants.PREF_SETTING_MODE, Constants.NL_SETTING_MODE_FILTER) == Constants.NL_SETTING_MODE_FILTER
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.layout_filter_intensity, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        FontUtils().setCustomFont(context!!, modeSwitch)

        // Disable them by default
        blueSlider.isEnabled = false
        greenSlider.isEnabled = false

        modeSwitch.setOnCheckedChangeListener { _, isChecked ->
            mode = isChecked

            val settingMode = if (isChecked) Constants.NL_SETTING_MODE_FILTER else Constants.NL_SETTING_MODE_TEMP

            PreferenceHelper.putInt(context, Constants.PREF_SETTING_MODE, settingMode)

            blueSlider.isEnabled = isChecked
            greenSlider.isEnabled = isChecked

            if (isChecked && NightLightAppService.instance.isInitDone()) {
                Core.applyNightModeAsync(isChecked, context, blueIntensity, greenIntensity)
            }

            NightLightAppService.instance.notifyNewSettingMode(settingMode)
        }

        modeSwitch.isChecked = mode

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

        NightLightAppService.instance
                .incrementViewInitCount()
    }

    fun onStateChanged(newMode: Int) {
        if (modeSwitch != null) modeSwitch.isChecked = newMode == Constants.NL_SETTING_MODE_FILTER
    }

    private fun getValues() {
        blueIntensity = PreferenceHelper.getInt(context, Constants.PREF_BLUE_INTENSITY, Constants.DEFAULT_BLUE_INTENSITY)
        greenIntensity = PreferenceHelper.getInt(context, Constants.PREF_GREEN_INTENSITY, Constants.DEFAULT_GREEN_INTENSITY)
    }
}
