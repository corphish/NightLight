package com.corphish.nightlight.activities

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.SeekBar
import com.corphish.nightlight.R
import com.corphish.nightlight.activities.base.BaseActivity
import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.design.ThemeUtils
import com.corphish.nightlight.design.utils.FontUtils
import com.corphish.nightlight.engine.ProfilesManager
import com.corphish.nightlight.helpers.PreferenceHelper
import com.gregacucnik.EditableSeekBar
import kotlinx.android.synthetic.main.activity_profile_create.*
import kotlinx.android.synthetic.main.content_profile_create.*

class ProfileCreateActivity : BaseActivity() {

    private lateinit var profile: ProfilesManager.Profile
    private var isProfileNull = true
    private var operationMode = Constants.MODE_CREATE
    private lateinit var profilesManager: ProfilesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(ThemeUtils.getAppTheme(this))
        setContentView(R.layout.activity_profile_create)

        useCollapsingActionBar()
        setActionBarTitle(R.string.profile_create_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        profilesManager = ProfilesManager(this)
        profilesManager.loadProfiles()

        isProfileNull = !intent.getBooleanExtra(Constants.PROFILE_DATA_PRESENT, false)
        profile = ProfilesManager.Profile(
                if (isProfileNull) "" else intent.getStringExtra(Constants.PROFILE_DATA_NAME)!!,
                intent.getBooleanExtra(Constants.PROFILE_DATA_SETTING_ENABLED, false),
                intent.getIntExtra(Constants.PROFILE_DATA_SETTING_MODE, PreferenceHelper.getInt(this, Constants.PREF_SETTING_MODE, Constants.NL_SETTING_MODE_TEMP)),
                if (isProfileNull) intArrayOf() else intent.getIntArrayExtra(Constants.PROFILE_DATA_SETTING)!!
        )

        operationMode = intent.getIntExtra(Constants.PROFILE_MODE, Constants.MODE_CREATE)

        fab.setOnClickListener {
            val retval = if (operationMode == Constants.MODE_CREATE)
                createProfileWithCurrentSelections()
            else
                updateProfileWithCurrentSelections()

            if (retval) {
                setResult(Activity.RESULT_OK)
                finish()
            } else {
                editTextLayout.error = getString(R.string.profile_create_name_error)
            }
        }

        initViewEventListeners()
        initViews()
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)

        super.onBackPressed()
    }

    private fun initViewEventListeners() {
        modes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val currentModeSelection = if (isProfileNull) position else profile.settingMode
                updateProfileCreatorParams(currentModeSelection)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }
    private fun initViews() {
        if (!isProfileNull) {
            nlSwitch.isChecked = profile.isSettingEnabled
            editText.setText(profile.name)
            modes.isEnabled = false
        } else  {
            modes.isEnabled = true
        }
        modes.setSelection(profile.settingMode)

        setActionBarTitle(
                if (operationMode == Constants.MODE_CREATE) R.string.profile_create_title else R.string.profile_edit_title
        )

        actionDesc.setText(
                if (operationMode == Constants.MODE_CREATE) R.string.profile_create_desc else R.string.profile_edit_desc
        )

        editTextLayout.error = null

        FontUtils().setCustomFont(this, nlSwitch)
    }

    private fun updateProfileCreatorParams(mode: Int) {
        val type = PreferenceHelper.getInt(this, Constants.PREF_INTENSITY_TYPE, Constants.INTENSITY_TYPE_MAXIMUM)

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

            if (!isProfileNull) {
                settingParam1.value = profile.settings[0]
                settingParam2.value = profile.settings[1]
                settingParam3.value = profile.settings[2]
            } else {
                settingParam1.value = PreferenceHelper.getInt(this, Constants.PREF_RED_COLOR[type], Constants.DEFAULT_RED_COLOR[type])
                settingParam2.value = PreferenceHelper.getInt(this, Constants.PREF_GREEN_COLOR[type], Constants.DEFAULT_GREEN_COLOR[type])
                settingParam3.value = PreferenceHelper.getInt(this, Constants.PREF_BLUE_COLOR[type], Constants.DEFAULT_BLUE_COLOR[type])
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
            settingParam1.setMinValue(resources.getInteger(R.integer.minTemp))
            settingParam1.setMaxValue(resources.getInteger(R.integer.maxTemp))

            settingParam2.isEnabled = false
            settingParam3.isEnabled = false

            settingTitle1.isEnabled = true
            settingTitle2.isEnabled = false
            settingTitle3.isEnabled = false

            settingTitle1.setText(R.string.color_temperature_title)
            settingTitle2.setText(R.string.profile_nl_setting_unavailable)

            if (!isProfileNull) {
                settingParam1.value = profile.settings[0]
            } else {
                settingParam1.value = PreferenceHelper.getInt(this, Constants.PREF_COLOR_TEMP[type], Constants.DEFAULT_COLOR_TEMP[type])
            }

            settingParam1.setOnEditableSeekBarChangeListener(object : EditableSeekBar.OnEditableSeekBarChangeListener{
                override fun onEditableSeekBarProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {}
                override fun onStartTrackingTouch(p0: SeekBar?) {}
                override fun onEditableSeekBarValueChanged(p0: Int) {}

                override fun onEnteredValueTooHigh() {
                    settingParam1.value = resources.getInteger(R.integer.maxTemp)
                }

                override fun onEnteredValueTooLow() {
                    settingParam1.value = resources.getInteger(R.integer.minTemp)
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {}
            })
        }
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
                profile,
                nlSwitch.isChecked,
                editText.editableText.toString(),
                modes.selectedItemPosition,
                if (modes.selectedItemId == Constants.NL_SETTING_MODE_TEMP.toLong())
                    intArrayOf(settingParam1.value)
                else
                    intArrayOf(settingParam1.value, settingParam2.value, settingParam3.value)
        )
    }
}
