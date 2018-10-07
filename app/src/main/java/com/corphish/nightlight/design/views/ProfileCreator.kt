package com.corphish.nightlight.design.views

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.SwitchCompat
import com.corphish.nightlight.R
import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.design.utils.FontUtils
import com.corphish.nightlight.engine.ProfilesManager
import com.corphish.nightlight.helpers.PreferenceHelper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ProfileCreator(val context: Context,
                     private val operationMode: Int,
                     val profile: ProfilesManager.Profile? = null,
                     private val onFinishListener: (status: Int) -> Unit) {

    private val creatorView = View.inflate(context, R.layout.bottom_sheet_create_profile, null)

    private val profileAction = creatorView.findViewById<TextView>(R.id.profile_action_title)
    private val profileActionDesc = creatorView.findViewById<TextView>(R.id.profile_action_desc)

    private val editText = creatorView.findViewById<TextInputEditText>(R.id.profile_name_set)
    private val editTextLayout = creatorView.findViewById<TextInputLayout>(R.id.profile_name_set_layout)

    private val nlSwitch = creatorView.findViewById<SwitchCompat>(R.id.profile_night_light_switch)

    private val settingTitle1 = creatorView.findViewById<TextView>(R.id.profile_night_light_setting_title1)
    private val settingTitle2 = creatorView.findViewById<TextView>(R.id.profile_night_light_setting_title2)
    private val settingTitle3 = creatorView.findViewById<TextView>(R.id.profile_night_light_setting_title3)

    private val cancel = creatorView.findViewById<TextView>(R.id.button_cancel)
    private val ok = creatorView.findViewById<AppCompatButton>(R.id.button_ok)

    private val modes = creatorView.findViewById<AppCompatSpinner>(R.id.profile_night_light_setting_mode)

    private val settingParam1 = creatorView.findViewById<AppCompatSeekBar>(R.id.profile_night_light_setting_param1)
    private val settingParam2 = creatorView.findViewById<AppCompatSeekBar>(R.id.profile_night_light_setting_param2)
    private val settingParam3 = creatorView.findViewById<AppCompatSeekBar>(R.id.profile_night_light_setting_param3)

    private val bottomSheetDialog = BottomSheetDialog(context, R.style.BottomSheetDialogDark)

    private val profilesManager = ProfilesManager(context)

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
                    intArrayOf(settingParam1.progress + 3000)
                else
                    intArrayOf(settingParam1.progress, settingParam2.progress, settingParam3.progress)
        )
    }

    private fun updateProfileWithCurrentSelections(): Boolean {
        return  profilesManager.updateProfile(
                profile!!,
                nlSwitch.isChecked,
                editText.editableText.toString(),
                modes.selectedItemPosition,
                if (modes.selectedItemId == Constants.NL_SETTING_MODE_TEMP.toLong())
                    intArrayOf(settingParam1.progress + 3000)
                else
                    intArrayOf(settingParam1.progress, settingParam2.progress, settingParam3.progress)
        )
    }

    private fun updateProfileCreatorParams(mode: Int) {
        if (mode == Constants.NL_SETTING_MODE_MANUAL) {
            settingParam1.isEnabled = true
            settingParam1.max = 256

            settingParam2.isEnabled = true
            settingParam2.max = 256

            settingParam3.isEnabled = true
            settingParam3.max = 256

            settingTitle1.isEnabled = true
            settingTitle2.isEnabled = true
            settingTitle3.isEnabled = true

            settingTitle1.setText(R.string.red)
            settingTitle2.setText(R.string.green)
            settingTitle3.setText(R.string.blue)

            if (profile != null) {
                settingParam1.progress = profile.settings[0]
                settingParam2.progress = profile.settings[1]
                settingParam3.progress = profile.settings[2]
            } else {
                settingParam1.progress = PreferenceHelper.getInt(context, Constants.PREF_RED_COLOR, Constants.DEFAULT_RED_COLOR)
                settingParam2.progress = PreferenceHelper.getInt(context, Constants.PREF_GREEN_COLOR, Constants.DEFAULT_GREEN_COLOR)
                settingParam3.progress = PreferenceHelper.getInt(context, Constants.PREF_BLUE_COLOR, Constants.DEFAULT_BLUE_COLOR)
            }
        } else {
            settingParam1.isEnabled = true
            settingParam1.max = 1500

            settingParam2.isEnabled = false
            settingParam3.isEnabled = false

            settingTitle1.isEnabled = true
            settingTitle2.isEnabled = false
            settingTitle3.isEnabled = false

            settingTitle1.setText(R.string.color_temperature_title)
            settingTitle2.setText(R.string.profile_nl_setting_unavailable)

            if (profile != null) {
                settingParam1.progress = profile.settings[0] - 3000
            } else {
                settingParam1.progress = PreferenceHelper.getInt(context, Constants.PREF_COLOR_TEMP, Constants.DEFAULT_COLOR_TEMP)
            }
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