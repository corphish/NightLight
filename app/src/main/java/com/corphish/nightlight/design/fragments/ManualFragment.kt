package com.corphish.nightlight.design.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.AppCompatSpinner
import androidx.fragment.app.Fragment
import com.corphish.nightlight.R
import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.design.alert.BottomSheetAlertDialog
import com.corphish.nightlight.engine.Core
import com.corphish.nightlight.helpers.PreferenceHelper
import com.corphish.nightlight.services.NightLightAppService
import com.google.android.material.button.MaterialButton
import com.gregacucnik.EditableSeekBar

class ManualFragment : Fragment() {

    private val _type = Constants.NL_SETTING_MODE_MANUAL
    private var intensityType = Constants.INTENSITY_TYPE_MINIMUM

    private var redColor = Constants.DEFAULT_RED_COLOR[intensityType]
    private var greenColor = Constants.DEFAULT_GREEN_COLOR[intensityType]
    private var blueColor = Constants.DEFAULT_BLUE_COLOR[intensityType]

    // Views
    private lateinit var intensityTypeChooser: AppCompatSpinner
    private lateinit var red: EditableSeekBar
    private lateinit var green: EditableSeekBar
    private lateinit var blue: EditableSeekBar

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_manual, container, false)

        red = root.findViewById(R.id.red)
        green = root.findViewById(R.id.green)
        blue = root.findViewById(R.id.blue)
        intensityTypeChooser = root.findViewById(R.id.intensityTypeChooser)

        intensityType = PreferenceHelper.getInt(context, Constants.PREF_INTENSITY_TYPE, Constants.INTENSITY_TYPE_MINIMUM)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.finish()
            }
        })

        getValues()
        initHeader(root)
        initInfoButton(root)
        initIntensityTypeView()
        initSliders()

        PreferenceHelper.putInt(context, Constants.PREF_SETTING_MODE, _type)

        Core.applyNightModeAsync(true, context, redColor, greenColor, blueColor)

        return root
    }

    private fun getValues() {
        redColor = PreferenceHelper.getInt(context, Constants.PREF_RED_COLOR[intensityType], Constants.DEFAULT_RED_COLOR[intensityType])
        greenColor = PreferenceHelper.getInt(context, Constants.PREF_GREEN_COLOR[intensityType], Constants.DEFAULT_GREEN_COLOR[intensityType])
        blueColor = PreferenceHelper.getInt(context, Constants.PREF_BLUE_COLOR[intensityType], Constants.DEFAULT_BLUE_COLOR[intensityType])
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

                if (NightLightAppService.instance.isInitDone()) {
                    PreferenceHelper.putInt(context, Constants.PREF_RED_COLOR[intensityType], redColor)
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
                    PreferenceHelper.putInt(context, Constants.PREF_GREEN_COLOR[intensityType], greenColor)
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
                    PreferenceHelper.putInt(context, Constants.PREF_BLUE_COLOR[intensityType], blueColor)
                    Core.applyNightModeAsync(true, context, redColor, greenColor, blueColor)
                    PreferenceHelper.putInt(context, Constants.PREF_CUR_APPLY_TYPE, Constants.APPLY_TYPE_NON_PROFILE)
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

    private fun initIntensityTypeView() {
        intensityTypeChooser.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                intensityType = position
                PreferenceHelper.putInt(context, Constants.PREF_INTENSITY_TYPE, position)

                getValues()
                setSliderValues()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        intensityTypeChooser.setSelection(intensityType)
    }

    private fun initHeader(root: View) {
        val bannerTitle = root.findViewById<TextView>(R.id.banner_title)
        val bannerIcon = root.findViewById<ImageButton>(R.id.banner_icon)

        bannerTitle.text = getString(R.string.section_color)
        bannerIcon.setImageResource(R.drawable.ic_color)
    }

    private fun initInfoButton(root: View) {
        val infoButton = root.findViewById<MaterialButton>(R.id.intensityInfo)

        infoButton.setOnClickListener {
            val infoDialog = BottomSheetAlertDialog(requireContext())
            infoDialog.setTitle(R.string.intensity_type_title)
            infoDialog.setMessage(R.string.intensity_type_desc)
            infoDialog.setPositiveButton(android.R.string.ok, View.OnClickListener { infoDialog.dismiss() })
            infoDialog.show()
        }
    }
}