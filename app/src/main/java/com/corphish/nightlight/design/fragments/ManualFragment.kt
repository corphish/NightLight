package com.corphish.nightlight.design.fragments

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

                PreferenceHelper.putInt(context, Constants.PREF_RED_COLOR, redColor)
                Core.applyNightModeAsync(true, context, redColor, greenColor, blueColor)
                PreferenceHelper.putInt(context, Constants.PREF_CUR_APPLY_TYPE, Constants.APPLY_TYPE_NON_PROFILE)
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

                PreferenceHelper.putInt(context, Constants.PREF_GREEN_COLOR, greenColor)
                Core.applyNightModeAsync(true, context, redColor, greenColor, blueColor)
                PreferenceHelper.putInt(context, Constants.PREF_CUR_APPLY_TYPE, Constants.APPLY_TYPE_NON_PROFILE)
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

                PreferenceHelper.putInt(context, Constants.PREF_BLUE_COLOR, blueColor)
                Core.applyNightModeAsync(true, context, redColor, greenColor, blueColor)
                PreferenceHelper.putInt(context, Constants.PREF_CUR_APPLY_TYPE, Constants.APPLY_TYPE_NON_PROFILE)
            }
        })

        setSliderValues()
    }

    private fun setSliderValues() {
        red.value = redColor
        green.value = greenColor
        blue.value = blueColor
    }
}