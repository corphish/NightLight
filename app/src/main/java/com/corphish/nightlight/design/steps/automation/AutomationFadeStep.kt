package com.corphish.nightlight.design.steps.automation

import android.view.LayoutInflater
import android.view.View
import android.widget.RadioGroup
import com.corphish.nightlight.R
import com.google.android.material.radiobutton.MaterialRadioButton
import ernestoyaquello.com.verticalstepperform.Step

class AutomationFadeStep(stepTitle: String?,
                         private val fadeChangeListener: (Boolean) -> Unit): Step<Int>(stepTitle) {
    // Chosen data.
    var pickedData = 0

    // Views
    private lateinit var radioGroup: RadioGroup

    // RadioButton ids
    private val _ids = listOf(
            R.id.fadeOff,
            R.id.fadeIn,
            R.id.fadeOut
    )

    // Step availability.
    var isStepAvailable = false
        set(value) {
            field = value
            updateUI()
        }

    override fun createStepContentLayout(): View {
        // Here we generate the view that will be used by the library as the content of the step.
        // In this case we do it programmatically, but we could also do it by inflating an XML layout.
        val view = LayoutInflater.from(context).inflate(R.layout.layout_step_fade_behavior, null)

        radioGroup = view.findViewById(R.id.fadeBehaviors)
        radioGroup.setOnCheckedChangeListener { _, i ->
            fadeChangeListener(i == R.id.fadeOff)
            pickedData = _ids.indexOf(i)
        }

        updateUI()

        return view
    }

    override fun isStepDataValid(stepData: Int) = IsDataValid(true, "")

    override fun getStepData() = pickedData

    override fun getStepDataAsHumanReadableString(): String {
        if (!isStepAvailable) {
            return context.getString(R.string.not_applicable_title)
        }

        return radioGroup.findViewById<MaterialRadioButton>(radioGroup.checkedRadioButtonId).text.toString()
    }

    override fun onStepOpened(animated: Boolean) {}

    override fun onStepClosed(animated: Boolean) {}

    override fun onStepMarkedAsCompleted(animated: Boolean) {}

    override fun onStepMarkedAsUncompleted(animated: Boolean) {}

    override fun restoreStepData(stepData: Int) {
        // To restore the step after a configuration change, we restore the text of its EditText view.
        this.pickedData = stepData
        updateUI()
    }

    private fun updateUI() {
        if (this::radioGroup.isInitialized) {
            radioGroup.isEnabled = isStepAvailable
            radioGroup.check(_ids[pickedData])
        }
    }
}