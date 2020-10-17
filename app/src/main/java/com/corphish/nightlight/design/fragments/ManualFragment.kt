package com.corphish.nightlight.design.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.corphish.nightlight.R
import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.engine.Core
import com.corphish.nightlight.helpers.PreferenceHelper
import com.corphish.nightlight.interfaces.ColorPickerCallback
import com.gregacucnik.EditableSeekBar

class ManualFragment : Fragment() {

    private val _type = Constants.NL_SETTING_MODE_MANUAL

    private var redColor = Constants.DEFAULT_RED_COLOR
    private var greenColor = Constants.DEFAULT_GREEN_COLOR
    private var blueColor = Constants.DEFAULT_BLUE_COLOR

    // Views
    private lateinit var red: EditableSeekBar
    private lateinit var green: EditableSeekBar
    private lateinit var blue: EditableSeekBar

    // Color picking mode
    private lateinit var colorPickerCallback: ColorPickerCallback
    private var colorPickingMode = false
    private val colorPickedData = Bundle()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.layout_manual_colors, container, false)

        red = root.findViewById(R.id.red)
        green = root.findViewById(R.id.green)
        blue = root.findViewById(R.id.blue)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.finish()
            }
        })

        getValues()
        initSliders()

        PreferenceHelper.putInt(context, Constants.PREF_SETTING_MODE, _type)

        Core.applyNightModeAsync(true, context, redColor, greenColor, blueColor)

        return root
    }

    private fun getValues() {
        redColor = PreferenceHelper.getInt(context, Constants.PREF_RED_COLOR, Constants.DEFAULT_RED_COLOR)
        greenColor = PreferenceHelper.getInt(context, Constants.PREF_GREEN_COLOR, Constants.DEFAULT_GREEN_COLOR)
        blueColor = PreferenceHelper.getInt(context, Constants.PREF_BLUE_COLOR, Constants.DEFAULT_BLUE_COLOR)
    }

    private fun initSliders() {
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
                Core.applyNightModeAsync(true, context, redColor, greenColor, blueColor)

                if (!colorPickingMode) {
                    PreferenceHelper.putInt(context, Constants.PREF_RED_COLOR, redColor)
                    PreferenceHelper.putInt(context, Constants.PREF_CUR_APPLY_TYPE, Constants.APPLY_TYPE_NON_PROFILE)
                } else {
                    colorPickedData.putInt(Constants.PREF_SETTING_MODE, Constants.NL_SETTING_MODE_MANUAL)
                    colorPickedData.putInt(Constants.PREF_RED_COLOR, redColor)
                    colorPickedData.putInt(Constants.PREF_GREEN_COLOR, greenColor)
                    colorPickedData.putInt(Constants.PREF_BLUE_COLOR, blueColor)
                    colorPickerCallback.onColorPicked(colorPickedData)
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
                Core.applyNightModeAsync(true, context, redColor, greenColor, blueColor)

                if (!colorPickingMode) {
                    PreferenceHelper.putInt(context, Constants.PREF_GREEN_COLOR, greenColor)
                    PreferenceHelper.putInt(context, Constants.PREF_CUR_APPLY_TYPE, Constants.APPLY_TYPE_NON_PROFILE)
                }else {
                    colorPickedData.putInt(Constants.PREF_SETTING_MODE, Constants.NL_SETTING_MODE_MANUAL)
                    colorPickedData.putInt(Constants.PREF_RED_COLOR, redColor)
                    colorPickedData.putInt(Constants.PREF_GREEN_COLOR, greenColor)
                    colorPickedData.putInt(Constants.PREF_BLUE_COLOR, blueColor)
                    colorPickerCallback.onColorPicked(colorPickedData)
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
                Core.applyNightModeAsync(true, context, redColor, greenColor, blueColor)

                if (!colorPickingMode) {
                    PreferenceHelper.putInt(context, Constants.PREF_BLUE_COLOR, blueColor)
                    PreferenceHelper.putInt(context, Constants.PREF_CUR_APPLY_TYPE, Constants.APPLY_TYPE_NON_PROFILE)
                } else {
                    colorPickedData.putInt(Constants.PREF_SETTING_MODE, Constants.NL_SETTING_MODE_MANUAL)
                    colorPickedData.putInt(Constants.PREF_RED_COLOR, redColor)
                    colorPickedData.putInt(Constants.PREF_GREEN_COLOR, greenColor)
                    colorPickedData.putInt(Constants.PREF_BLUE_COLOR, blueColor)
                    colorPickerCallback.onColorPicked(colorPickedData)
                }
            }
        })

        setSliderValues()
    }

    private fun setSliderValues() {
        red.value = redColor
        green.value = greenColor
        blue.value = blueColor
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