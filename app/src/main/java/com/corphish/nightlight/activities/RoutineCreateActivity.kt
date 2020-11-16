package com.corphish.nightlight.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.corphish.nightlight.R
import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.databinding.ActivityRoutineCreateBinding
import com.corphish.nightlight.design.steps.automation.*
import com.corphish.nightlight.engine.AutomationRoutineManager
import com.corphish.nightlight.engine.models.AutomationRoutine
import com.corphish.nightlight.engine.models.FadeBehavior
import com.corphish.nightlight.engine.models.PickedColorData
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
        binding.toolbarLayout.title = title

        // See if it is in update mode
        index = intent.getIntExtra(Constants.ROUTINE_UPDATE_INDEX, -1)

        initSteps()
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
    }

    override fun onCompletedForm() {
        // Build the routine.
        val routine = AutomationRoutine(
                name = nameStep.stepData,
                switchState = switchStep.stepData,
                startTime = startTimeStep.stepData ?: Constants.DEFAULT_START_TIME,
                endTime = endTimeStep.stepData ?: Constants.DEFAULT_END_TIME,
                fadeBehavior = FadeBehavior(
                        type = fadeStep.stepData,
                        fadeFrom = fromColorStep.stepData?.settings ?: intArrayOf(256, 256, 256),
                        fadeTo = toColorStep.stepData?.settings ?: intArrayOf(256, 256, 256),
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
            val pickedData = PickedColorData.fromIntent(intent)
            if (requestCode == 81) {
                fromColorStep.updateData(pickedData)
            } else if (requestCode == 82) {
                toColorStep.updateData(pickedData)
            }
        }
    }
}