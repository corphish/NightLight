package com.corphish.nightlight.design.steps.profile

import android.widget.EditText
import android.text.TextWatcher
import android.text.Editable
import android.view.View
import com.corphish.nightlight.R
import com.corphish.nightlight.engine.ProfilesManager
import ernestoyaquello.com.verticalstepperform.Step

class ProfileNameStep(stepTitle: String?,
                      private val validator: (String) -> Boolean) : Step<String>(stepTitle) {
    private lateinit var profileNameView: EditText
    
    override fun createStepContentLayout(): View {
        // Here we generate the view that will be used by the library as the content of the step.
        // In this case we do it programmatically, but we could also do it by inflating an XML layout.
        profileNameView = EditText(context)
        profileNameView.isSingleLine = true
        profileNameView.hint = context.getString(R.string.profile_create_name)
        profileNameView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // Whenever the user updates the user name text, we update the state of the step.
                // The step will be marked as completed only if its data is valid, which will be
                // checked automatically by the form with a call to isStepDataValid().
                markAsCompletedOrUncompleted(true)
            }

            override fun afterTextChanged(s: Editable) {}
        })
        
        return profileNameView
    }

    override fun isStepDataValid(stepData: String) = IsDataValid(
            validator(stepData), context.getString(R.string.profile_create_name_error))

    override fun getStepData(): String {
        // We get the step's data from the value that the user has typed in the EditText view.
        val userName = profileNameView.text

        return userName?.toString() ?: ""
    }

    override fun getStepDataAsHumanReadableString(): String {
        // Because the step's data is already a human-readable string, we don't need to convert it.
        // However, we return "(Empty)" if the text is empty to avoid not having any text to display.
        // This string will be displayed in the subtitle of the step whenever the step gets closed.
        val userName = stepData
        return if (userName.isNotEmpty()) userName else "(Empty)"
    }

    override fun onStepOpened(animated: Boolean) {}

    override fun onStepClosed(animated: Boolean) {}

    override fun onStepMarkedAsCompleted(animated: Boolean) {}

    override fun onStepMarkedAsUncompleted(animated: Boolean) {}

    override fun restoreStepData(stepData: String) {
        // To restore the step after a configuration change, we restore the text of its EditText view.
        profileNameView.setText(stepData)
    }
}