package com.corphish.nightlight.design.views

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatSpinner
import com.corphish.nightlight.R
import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.design.ThemeUtils
import com.corphish.nightlight.design.utils.FontUtils
import com.corphish.nightlight.engine.ProfilesManager
import com.corphish.nightlight.helpers.PreferenceHelper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.gregacucnik.EditableSeekBar

class ProfileCreator(val context: Context,
                     private val operationMode: Int,
                     val profile: ProfilesManager.Profile? = null,
                     private val onFinishListener: (status: Int) -> Unit) {

    private val creatorView = View.inflate(context, R.layout.bottom_sheet_create_profile, null)

    private val profileAction = creatorView.findViewById<TextView>(R.id.profile_action_title)
    private val profileActionDesc = creatorView.findViewById<TextView>(R.id.profile_action_desc)

    private val editText = creatorView.findViewById<TextInputEditText>(R.id.profile_name_set)
    private val editTextLayout = creatorView.findViewById<TextInputLayout>(R.id.profile_name_set_layout)

    private val nlSwitch = creatorView.findViewById<Switch>(R.id.profile_night_light_switch)

    private val settingTitle1 = creatorView.findViewById<TextView>(R.id.profile_night_light_setting_title1)
    private val settingTitle2 = creatorView.findViewById<TextView>(R.id.profile_night_light_setting_title2)
    private val settingTitle3 = creatorView.findViewById<TextView>(R.id.profile_night_light_setting_title3)

    private val cancel = creatorView.findViewById<TextView>(R.id.button_cancel)
    private val ok = creatorView.findViewById<AppCompatButton>(R.id.button_ok)

    private val modes = creatorView.findViewById<AppCompatSpinner>(R.id.profile_night_light_setting_mode)

    private val settingParam1 = creatorView.findViewById<EditableSeekBar>(R.id.profile_night_light_setting_param1)
    private val settingParam2 = creatorView.findViewById<EditableSeekBar>(R.id.profile_night_light_setting_param2)
    private val settingParam3 = creatorView.findViewById<EditableSeekBar>(R.id.profile_night_light_setting_param3)

    private val bottomSheetDialog = BottomSheetDialog(context, ThemeUtils.getBottomSheetTheme(context))

    private val profilesManager = ProfilesManager(context)

    private val type = PreferenceHelper.getInt(context, Constants.PREF_INTENSITY_TYPE, Constants.INTENSITY_TYPE_MAXIMUM)

    init {
        profilesManager.loadProfiles()
    }

    private fun initViewEventListeners() {
        modes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val currentModeSelection = profile?.settingMode ?: position
                updateProfileCreatorParams(currentModeSelection)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        cancel.setOnClickListener {
            onFinishListener.invoke(STATUS_CANCELED)
            bottomSheetDialog.dismiss()
        }

        ok.setOnClickListener {
            val retVal = if (operationMode == MODE_CREATE)
                createProfileWithCurrentSelections()
            else
                updateProfileWithCurrentSelections()

            if (retVal)
                onFinishListener(STATUS_SUCCESS)
            else
                editTextLayout.error = context.getString(R.string.profile_create_name_error)

            bottomSheetDialog.dismiss()
        }
    }

    private fun initViews() {
        if (profile != null) {
            nlSwitch.isChecked = profile.isSettingEnabled
            editText.setText(profile.name)
            modes.setSelection(profile.settingMode)
            modes.isEnabled = false
        } else  {
            modes.isEnabled = true
        }

        profileAction.setText(
                if (operationMode == MODE_CREATE) R.string.profile_create_title else R.string.profile_edit_title
        )

        profileActionDesc.setText(
                if (operationMode == MODE_CREATE) R.string.profile_create_desc else R.string.profile_edit_desc
        )

        editTextLayout.error = null

        FontUtils().setCustomFont(context, nlSwitch)
    }

    private fun createProfileWithCurrentSelections(): Boolean {
        return profilesManager.createProfile(
                nlSwitch.isChecked,
                editText.editableText.toString(),
                modes.selectedItemPosition,
                if (modes.selectedItemId == Constants.NL_SETTING_MODE_TEMP.toLong())
                    intArrayOf(settingParam1.value)
                else
                    intArrayOf(settingParam1.value, settingParam2.value, settingParam3.value)
        )
    }

    private fun updateProfileWithCurrentSelections(): Boolean {
        return  profilesManager.updateProfile(
                profile!!,
                nlSwitch.isChecked,
                editText.editableText.toString(),
                modes.selectedItemPosition,
                if (modes.selectedItemId == Constants.NL_SETTING_MODE_TEMP.toLong())
                    intArrayOf(settingParam1.value)
                else
                    intArrayOf(settingParam1.value, settingParam2.value, settingParam3.value)
        )
    }

    private fun updateProfileCreatorParams(mode: Int) {
        if (mode == Constants.NL_SETTING_MODE_MANUAL) {
            settingParam1.isEnabled = true
            settingParam1.setMinValue(0)
            settingParam1.setMaxValue(256)

            settingParam2.isEnabled = true
            settingParam1.setMinValue(0)
            settingParam2.setMaxValue(256)

            settingParam3.isEnabled = true
            settingParam1.setMinValue(0)
            settingParam3.setMaxValue(256)

            settingTitle1.isEnabled = true
            settingTitle2.isEnabled = true
            settingTitle3.isEnabled = true

            settingTitle1.setText(R.string.red)
            settingTitle2.setText(R.string.green)
            settingTitle3.setText(R.string.blue)

            if (profile != null) {
                settingParam1.value = profile.settings[0]
                settingParam2.value = profile.settings[1]
                settingParam3.value = profile.settings[2]
            } else {
                settingParam1.value = PreferenceHelper.getInt(context, Constants.PREF_RED_COLOR[type], Constants.DEFAULT_RED_COLOR[type])
                settingParam2.value = PreferenceHelper.getInt(context, Constants.PREF_GREEN_COLOR[type], Constants.DEFAULT_GREEN_COLOR[type])
                settingParam3.value = PreferenceHelper.getInt(context, Constants.PREF_BLUE_COLOR[type], Constants.DEFAULT_BLUE_COLOR[type])
            }

            settingParam1.setOnEditableSeekBarChangeListener(object : EditableSeekBar.OnEditableSeekBarChangeListener{
                override fun onEditableSeekBarProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {}

                override fun onEnteredValueTooLow() {
                    settingParam1.value = 0
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {}

                override fun onEditableSeekBarValueChanged(p0: Int) {}

                override fun onEnteredValueTooHigh() {
                    settingParam1.value = 256
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {}
            })

            settingParam2.setOnEditableSeekBarChangeListener(object : EditableSeekBar.OnEditableSeekBarChangeListener{
                override fun onEditableSeekBarProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {}

                override fun onEnteredValueTooLow() {
                    settingParam1.value = 0
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {}

                override fun onEditableSeekBarValueChanged(p0: Int) {}

                override fun onEnteredValueTooHigh() {
                    settingParam1.value = 256
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {}
            })

            settingParam3.setOnEditableSeekBarChangeListener(object : EditableSeekBar.OnEditableSeekBarChangeListener{
                override fun onEditableSeekBarProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {}

                override fun onEnteredValueTooLow() {
                    settingParam1.value = 0
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {}

                override fun onEditableSeekBarValueChanged(p0: Int) {}

                override fun onEnteredValueTooHigh() {
                    settingParam1.value = 256
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {}
            })

        } else {
            settingParam1.isEnabled = true
            settingParam1.setMinValue(context.resources.getInteger(R.integer.minTemp))
            settingParam1.setMaxValue(context.resources.getInteger(R.integer.maxTemp))

            settingParam2.isEnabled = false
            settingParam3.isEnabled = false

            settingTitle1.isEnabled = true
            settingTitle2.isEnabled = false
            settingTitle3.isEnabled = false

            settingTitle1.setText(R.string.color_temperature_title)
            settingTitle2.setText(R.string.profile_nl_setting_unavailable)

            if (profile != null) {
                settingParam1.value = profile.settings[0]
            } else {
                settingParam1.value = PreferenceHelper.getInt(context, Constants.PREF_COLOR_TEMP[type], Constants.DEFAULT_COLOR_TEMP[type])
            }

            settingParam1.setOnEditableSeekBarChangeListener(object : EditableSeekBar.OnEditableSeekBarChangeListener{
                override fun onEditableSeekBarProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {}

                override fun onStartTrackingTouch(p0: SeekBar?) {}

                override fun onEditableSeekBarValueChanged(p0: Int) {}

                override fun onEnteredValueTooHigh() {
                    settingParam1.value = context.resources.getInteger(R.integer.maxTemp)
                }

                override fun onEnteredValueTooLow() {
                    settingParam1.value = context.resources.getInteger(R.integer.minTemp)
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {}
            })
        }
    }

    fun show() {
        initViews()
        initViewEventListeners()

        bottomSheetDialog.setContentView(creatorView)
        bottomSheetDialog.setOnShowListener {
            val d = it as BottomSheetDialog
            val bottomSheetInternal = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            BottomSheetBehavior.from(bottomSheetInternal!!).setState(BottomSheetBehavior.STATE_EXPANDED)
        }
        bottomSheetDialog.show()
    }

    companion object {
        const val MODE_CREATE = 0
        const val MODE_EDIT = 1

        const val STATUS_SUCCESS = 0
        const val STATUS_CANCELED = 1
    }
}