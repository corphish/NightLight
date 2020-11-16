package com.corphish.nightlight.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.corphish.nightlight.R
import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.databinding.ActivityRoutineCreateBinding
import com.corphish.nightlight.design.ThemeUtils
import com.corphish.nightlight.design.steps.automation.*
import com.corphish.nightlight.engine.AutomationRoutineManager
import com.corphish.nightlight.engine.models.AutomationRoutine
import com.corphish.nightlight.engine.models.FadeBehavior
import com.corphish.nightlight.engine.models.PickedColorData
import com.corphish.widgets.ktx.dialogs.MessageAlertDialog
import ernestoyaquello.com.verticalstepperform.listener.StepperFormListener

class RoutineCreateActivity : AppCompatActivity(), StepperFormListener {

    // View binding
    private lateinit var binding: ActivityRoutineCreateBinding

    // Stepper forms
    private lateinit var switchStep: AutomationSwitchStep
    private lateinit var startTimeStep: AutomationTimeStep
    private lateinit var endTimeStep: AutomationTimeStep
    private lateinit var fadeStep: AutomationFadeStep
    private lateinit var fromColorStep: AutomationDataStep
    private lateinit var toColorStep: AutomationDataStep
    private lateinit var nameStep: AutomationNameStep

    // Update index.
    private var index = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRoutineCreateBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = title
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // See if it is in update mode
        index = intent.getIntExtra(Constants.ROUTINE_UPDATE_INDEX, -1)

        initSteps()
        restoreRoutine()
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)

        super.onBackPressed()
    }

    /**
     * Initialize the form steps.
     */
    private fun initSteps() {
        switchStep = AutomationSwitchStep(getString(R.string.profile_nl_switch)) {
            fadeStep.isStepAvailable = it
            fromColorStep.isStepAvailable = it
            toColorStep.isStepAvailable = it
        }

        startTimeStep = AutomationTimeStep(getString(R.string.start_time), index)
        endTimeStep = AutomationTimeStep(getString(R.string.end_time), index)
        fadeStep = AutomationFadeStep(getString(R.string.fade_behavior)) {
            toColorStep.isStepAvailable = !it
        }

        fromColorStep = AutomationDataStep(this, getString(R.string.start_color), 81)
        toColorStep = AutomationDataStep(this, getString(R.string.end_color), 82)
        nameStep = AutomationNameStep(getString(R.string.routine_name))

        binding.included.routineCreateForm
                .setup(this, switchStep, startTimeStep, endTimeStep, fadeStep, fromColorStep, toColorStep, nameStep)
                .init()
    }

    /**
     * Method to restore routine if update mode is selected.
     */
    private fun restoreRoutine() {
        if (index != -1) {
            val routines = AutomationRoutineManager.automationRoutineList
            if (index < routines.size) {
                val routine = routines[index]

                // Restore
                nameStep.restoreStepData(routine.name)
                switchStep.restoreStepData(routine.switchState)
                startTimeStep.restoreStepData(routine.startTime)
                if (routine.endTime != AutomationRoutine.TIME_UNSET) {
                    endTimeStep.restoreStepData(routine.endTime)
                }
                fadeStep.restoreStepData(routine.fadeBehavior.settingType)
                if (!routine.rgbFrom.contentEquals(FadeBehavior.RGB_UNSET)) {
                    fromColorStep.restoreStepData(PickedColorData(
                            routine.fadeBehavior.settingType,
                            routine.rgbFrom
                    ))
                }
                if (!routine.rgbTo.contentEquals(FadeBehavior.RGB_UNSET)) {
                    toColorStep.restoreStepData(PickedColorData(
                            routine.fadeBehavior.settingType,
                            routine.rgbTo
                    ))
                }

                supportActionBar?.title = getString(R.string.update_routine)
            }
        }
    }

    override fun onCompletedForm() {
        // Build the routine.
        val routine = AutomationRoutine(
                name = nameStep.stepData,
                switchState = switchStep.stepData,
                startTime = startTimeStep.stepData ?: AutomationRoutine.TIME_UNSET,
                endTime = endTimeStep.stepData ?: AutomationRoutine.TIME_UNSET,
                fadeBehavior = FadeBehavior(
                        type = fadeStep.stepData,
                        settingType = fromColorStep.stepData?.settingMode ?: Constants.NL_SETTING_MODE_TEMP,
                        fadeFrom = fromColorStep.stepData?.settings ?: FadeBehavior.RGB_UNSET,
                        fadeTo = toColorStep.stepData?.settings ?: FadeBehavior.RGB_UNSET,
                )
        )

        val retVal = if (index == -1) {
            AutomationRoutineManager.addRoutine(this, routine)
        } else {
            AutomationRoutineManager.updateRoutineAt(this, index, routine)
        }

        if (retVal) {
            AutomationRoutineManager.persistRoutines(this)
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    override fun onCancelledForm() {
        setResult(Activity.RESULT_CANCELED)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if ((requestCode == 81 || requestCode == 82) && resultCode == RESULT_OK && data != null) {
            val pickedData = PickedColorData.fromIntent(data)
            if (requestCode == 81) {
                fromColorStep.updateData(pickedData)
            } else if (requestCode == 82) {
                toColorStep.updateData(pickedData)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.routine_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.action_delete -> {
                if (index == -1) {
                    // Avoid
                } else {
                    MessageAlertDialog(this).apply {
                        titleResId = R.string.delete
                        messageResId = R.string.delete_routine
                        animationResourceLayout = R.raw.delete
                        positiveButtonProperties = MessageAlertDialog.ButtonProperties(
                                buttonTitleResId = android.R.string.ok,
                                buttonColor = ThemeUtils.getNLStatusIconForeground(this@RoutineCreateActivity, true),
                                buttonAction = {
                                    AutomationRoutineManager.deleteRoutineAt(index)
                                    AutomationRoutineManager.persistRoutines(this@RoutineCreateActivity)
                                    setResult(RESULT_OK)
                                    dismissDialog()
                                    finish()
                                }
                        )
                        negativeButtonProperties = MessageAlertDialog.ButtonProperties(
                                buttonTitleResId = android.R.string.cancel,
                                buttonColor = ThemeUtils.getNLStatusIconForeground(this@RoutineCreateActivity, true),
                                buttonAction = { dismissDialog() }
                        )
                    }.show()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}