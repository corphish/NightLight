package com.corphish.nightlight.design.steps.automation

import android.view.View
import com.corphish.nightlight.R
import com.google.android.material.switchmaterial.SwitchMaterial
import ernestoyaquello.com.verticalstepperform.Step

class AutomationSwitchStep(stepTitle: String?,
                        private val switchChangeCallback: (Boolean) -> Unit) : Step<Boolean>(stepTitle) {
    private lateinit var autoSwitch: SwitchMaterial

    override fun createStepContentLayout(): View {
        // Here we generate the view that will be used by the library as the content of the step.
        // In this case we do it programmatically, but we could also do it by inflating an XML layout.
        autoSwitch = SwitchMaterial(context)
        autoSwitch.text = context.getString(R.string.profile_nl_switch)
        autoSwitch.setOnCheckedChangeListener { _, isChecked -> switchChangeCallback(isChecked) }

        return autoSwitch
    }

    override fun isStepDataValid(stepData: Boolean) = IsDataValid(true, "")

    override fun getStepData() = autoSwitch.isChecked

    override fun getStepDataAsHumanReadableString(): String {
        return context.getString(if (autoSwitch.isChecked) R.string.on else R.string.off)
    }

    override fun onStepOpened(animated: Boolean) {}

    override fun onStepClosed(animated: Boolean) {}

    override fun onStepMarkedAsCompleted(animated: Boolean) {}

    override fun onStepMarkedAsUncompleted(animated: Boolean) {}

    override fun restoreStepData(stepData: Boolean) {
        // To restore the step after a configuration change, we restore the text of its EditText view.
        autoSwitch.isChecked = stepData
    }
}