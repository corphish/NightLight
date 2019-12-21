package com.corphish.nightlight.design.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
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

class TemperatureFragment: Fragment() {
    // Data
    private val _type = Constants.NL_SETTING_MODE_TEMP
    private var intensityType = Constants.INTENSITY_TYPE_MINIMUM
    private var colorTemperature = Constants.DEFAULT_COLOR_TEMP[intensityType]

    // Views
    private lateinit var intensityTypeChooser: AppCompatSpinner
    private lateinit var temperatureValue: EditableSeekBar

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_temperature, container, false)

        intensityTypeChooser = root.findViewById(R.id.intensityTypeChooser)
        temperatureValue = root.findViewById(R.id.temperatureValue)

        intensityType = PreferenceHelper.getInt(context, Constants.PREF_INTENSITY_TYPE, Constants.INTENSITY_TYPE_MINIMUM)

        getValues()
        initHeader(root)
        initInfoButton(root)
        initIntensityTypeView()
        initSlider()

        PreferenceHelper.putInt(context, Constants.PREF_SETTING_MODE, _type)

        Core.applyNightModeAsync(true, context, colorTemperature)

        return root
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

    private fun getValues() {
        colorTemperature = PreferenceHelper.getInt(context, Constants.PREF_COLOR_TEMP[intensityType], Constants.DEFAULT_COLOR_TEMP[intensityType])
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
                if (NightLightAppService.instance.isInitDone()) {
                    PreferenceHelper.putInt(context, Constants.PREF_COLOR_TEMP[intensityType], value)
                    Core.applyNightModeAsync(true, context, value)
                    PreferenceHelper.putInt(context, Constants.PREF_CUR_APPLY_TYPE, Constants.APPLY_TYPE_NON_PROFILE)
                }
            }
        })

        setSliderValues()
    }

    private fun setSliderValues() {
        temperatureValue.value = colorTemperature
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
            val infoDialog = BottomSheetAlertDialog(context!!)
            infoDialog.setTitle(R.string.intensity_type_title)
            infoDialog.setMessage(R.string.intensity_type_desc)
            infoDialog.setPositiveButton(android.R.string.ok, View.OnClickListener { infoDialog.dismiss() })
            infoDialog.show()
        }
    }
}