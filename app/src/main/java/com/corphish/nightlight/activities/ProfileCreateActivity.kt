package com.corphish.nightlight.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.corphish.nightlight.R
import com.corphish.nightlight.activities.base.BaseActivity
import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.design.ThemeUtils
import com.corphish.nightlight.design.steps.ProfileDataStep
import com.corphish.nightlight.design.steps.ProfileNameStep
import com.corphish.nightlight.design.steps.ProfileSwitchStep
import com.corphish.nightlight.engine.ProfilesManager
import com.corphish.nightlight.helpers.PreferenceHelper
import com.corphish.widgets.ktx.dialogs.MessageAlertDialog
import ernestoyaquello.com.verticalstepperform.listener.StepperFormListener
import kotlinx.android.synthetic.main.content_profile_create.*

class ProfileCreateActivity : BaseActivity(), StepperFormListener {

    private var profile: ProfilesManager.Profile? = null
    private var isProfileNull = true
    private var operationMode = Constants.MODE_CREATE
    private lateinit var profilesManager: ProfilesManager

    // Steps
    private lateinit var profileNameStep: ProfileNameStep
    private lateinit var profileSwitchStep: ProfileSwitchStep
    private lateinit var profileDataStep: ProfileDataStep

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(ThemeUtils.getAppTheme(this))
        setContentView(R.layout.activity_profile_create)

        useCollapsingActionBar()
        setActionBarTitle(R.string.profile_create_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        profilesManager = ProfilesManager(this)
        profilesManager.loadProfiles()

        // Check if existing profile is being edited
        isProfileNull = !intent.getBooleanExtra(Constants.PROFILE_DATA_PRESENT, false)
        if (!isProfileNull) {
            profile = ProfilesManager.Profile(
                    intent.getStringExtra(Constants.PROFILE_DATA_NAME)!!,
                    intent.getBooleanExtra(Constants.PROFILE_DATA_SETTING_ENABLED, false),
                    intent.getIntExtra(Constants.PROFILE_DATA_SETTING_MODE, PreferenceHelper.getInt(this, Constants.PREF_SETTING_MODE, Constants.NL_SETTING_MODE_TEMP)),
                    intent.getIntArrayExtra(Constants.PROFILE_DATA_SETTING)!!
            )
        }

        operationMode = intent.getIntExtra(Constants.PROFILE_MODE, Constants.MODE_CREATE)

        initViews()
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)

        super.onBackPressed()
    }

    private fun initViews() {
        profileNameStep = ProfileNameStep(getString(R.string.profile_create_name))
        profileDataStep = ProfileDataStep(this, getString(R.string.section_color))
        profileSwitchStep = ProfileSwitchStep(getString(R.string.profile_nl_switch)) {
            profileDataStep.isStepAvailable = it
        }

        profileCreateForm
                .setup(this, profileNameStep, profileSwitchStep, profileDataStep)
                .init()
    }

    private fun createProfileWithCurrentSelections(): Boolean {
        val data = profileDataStep.stepData
        val mode = data.getInt(Constants.PREF_SETTING_MODE)

        val settings: IntArray = if (mode == Constants.NL_SETTING_MODE_TEMP)
            intArrayOf(data.getInt(Constants.PREF_COLOR_TEMP))
        else
            intArrayOf(
                    data.getInt(Constants.PREF_RED_COLOR),
                    data.getInt(Constants.PREF_GREEN_COLOR),
                    data.getInt(Constants.PREF_GREEN_COLOR)
            )

        return profilesManager.createProfile(
                profileSwitchStep.stepData,
                profileNameStep.stepData,
                mode, // Mode
                settings, // Setting as array
        )
    }

    private fun updateProfileWithCurrentSelections(): Boolean {
        val data = profileDataStep.stepData
        val mode = data.getInt(Constants.PREF_SETTING_MODE)

        val settings: IntArray = if (mode == Constants.NL_SETTING_MODE_TEMP)
            intArrayOf(data.getInt(Constants.PREF_COLOR_TEMP))
        else
            intArrayOf(
                    data.getInt(Constants.PREF_RED_COLOR),
                    data.getInt(Constants.PREF_GREEN_COLOR),
                    data.getInt(Constants.PREF_GREEN_COLOR)
            )

        return if (profile == null) false else
            profilesManager.updateProfile(
                    profile!!,
                    profileSwitchStep.stepData,
                    profileNameStep.stepData,
                    mode, // Mode
                    settings, // Setting as array
            )
    }

    /**
     * This method will be called when the user clicks on the last button after all the steps have
     * been marked as completed. It can be used to trigger showing loaders, sending the data, etc.
     *
     * Before this method gets called, the form disables the navigation between steps, as well as
     * all the buttons. To revert the form to normal, call cancelFormCompletionOrCancellationAttempt().
     */
    override fun onCompletedForm() {
        val retVal = if (operationMode == Constants.MODE_CREATE)
            createProfileWithCurrentSelections()
        else
            updateProfileWithCurrentSelections()

        if (retVal) {
            setResult(Activity.RESULT_OK)
            finish()
        } else {
            MessageAlertDialog(this).apply {
                titleResId = R.string.profile_create_title
                messageResId = R.string.profile_create_name_error
                positiveButtonProperties = MessageAlertDialog.ButtonProperties(
                        buttonTitleResId = android.R.string.ok,
                        buttonAction = { dismissDialog() }
                )
            }.show()
        }
    }

    /**
     * This method will be called when the form has been cancelled, which would generally mean that
     * the user has decided to not save/send the data (for example, by clicking on the cancellation
     * button of the confirmation step).
     *
     * Before this method gets called, the form disables the navigation between steps, as well as
     * all the buttons. To revert the form to normal, call cancelFormCompletionOrCancellationAttempt().
     */
    override fun onCancelledForm() {
        setResult(RESULT_CANCELED)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d("NL_ProfileCreate", "onActivityResult, result ok = ${resultCode == RESULT_OK}, $requestCode")

        if (requestCode == 43 && resultCode == RESULT_OK && data != null) {
            val pickedData = Bundle()
            val mode = data.getIntExtra(Constants.PREF_SETTING_MODE, Constants.NL_SETTING_MODE_TEMP)
            pickedData.putInt(Constants.PREF_SETTING_MODE, mode)
            if (mode == Constants.NL_SETTING_MODE_TEMP) {
                pickedData.putInt(Constants.PREF_COLOR_TEMP, data.getIntExtra(Constants.PREF_COLOR_TEMP, Constants.DEFAULT_COLOR_TEMP))
            } else {
                pickedData.putInt(Constants.PREF_RED_COLOR, data.getIntExtra(Constants.PREF_RED_COLOR, Constants.DEFAULT_RED_COLOR))
                pickedData.putInt(Constants.PREF_GREEN_COLOR, data.getIntExtra(Constants.PREF_GREEN_COLOR, Constants.DEFAULT_GREEN_COLOR))
                pickedData.putInt(Constants.PREF_BLUE_COLOR, data.getIntExtra(Constants.PREF_BLUE_COLOR, Constants.DEFAULT_BLUE_COLOR))
            }

            profileDataStep.updateData(pickedData)
        }
    }
}
