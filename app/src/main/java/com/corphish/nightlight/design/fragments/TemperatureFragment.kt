package com.corphish.nightlight.design.fragments

import android.content.Context
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
import com.corphish.nightlight.interfaces.ColorPickerCallback
import com.gregacucnik.EditableSeekBar

class TemperatureFragment: Fragment() {
    // Data
    private val _type = Constants.NL_SETTING_MODE_TEMP
    private var colorTemperature = Constants.DEFAULT_COLOR_TEMP

    // Views
    private lateinit var temperatureValue: EditableSeekBar

    // Color picking mode
    private lateinit var colorPickerCallback: ColorPickerCallback
    private var colorPickingMode = false
    private val colorPickedData = Bundle()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.layout_temperature, container, false)

        temperatureValue = root.findViewById(R.id.temperatureValue)

        getValues()
        initSlider()

        PreferenceHelper.putInt(context, Constants.PREF_SETTING_MODE, _type)

        Core.applyNightModeAsync(true, context, colorTemperature)

        if (colorPickingMode) {
            pickColors()
        }

        return root
    }

    private fun getValues() {
        colorTemperature = PreferenceHelper.getInt(context, Constants.PREF_COLOR_TEMP, Constants.DEFAULT_COLOR_TEMP)
    }

    private fun initSlider() {
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
                colorTemperature = value
                Core.applyNightModeAsync(true, context, value)

                if (!colorPickingMode) {
                    PreferenceHelper.putInt(context, Constants.PREF_COLOR_TEMP, value)
                    PreferenceHelper.putInt(context, Constants.PREF_CUR_APPLY_TYPE, Constants.APPLY_TYPE_NON_PROFILE)
                } else {
                    pickColors()
                }
            }
        })

        setSliderValues()
    }

    /**
     * Picks colors in color picking mode.
     */
    private fun pickColors() {
        colorPickedData.putInt(Constants.PREF_SETTING_MODE, Constants.NL_SETTING_MODE_TEMP)
        colorPickedData.putInt(Constants.PREF_COLOR_TEMP, colorTemperature)
        colorPickerCallback.onColorPicked(colorPickedData)
    }

    private fun setSliderValues() {
        temperatureValue.value = colorTemperature
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        colorPickerCallback = context as ColorPickerCallback
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if it is color picking mode
        colorPickingMode = arguments?.getBoolean(Constants.COLOR_PICKER_MODE) ?: false
    }
}