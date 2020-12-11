package com.corphish.nightlight.design.steps.profile

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.corphish.nightlight.R
import com.corphish.nightlight.activities.ColorControlActivity
import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.design.ThemeUtils
import com.corphish.nightlight.engine.models.FadeBehavior
import com.corphish.nightlight.engine.models.PickedColorData
import com.corphish.widgets.ktx.dialogs.SingleChoiceAlertDialog
import com.corphish.widgets.ktx.dialogs.properties.IconProperties
import ernestoyaquello.com.verticalstepperform.Step

class ProfileDataStep(private val activity: Activity, stepTitle: String?): Step<PickedColorData?>(stepTitle) {

    // Bundle data
    private var data: PickedColorData? = null

    // Views
    private lateinit var dataView: TextView
    private lateinit var dataMsg: TextView

    // Step availability
    // If night light switch is off, then we don't need this step
    var isStepAvailable = false
        set(value) {
            field = value
            updateUI()
        }

    override fun createStepContentLayout(): View {
        // Here we generate the view that will be used by the library as the content of the step.
        // In this case we do it programmatically, but we could also do it by inflating an XML layout.
        val view = LayoutInflater.from(context).inflate(R.layout.layout_profile_data_step, null)

        dataView = view.findViewById(R.id.dataView)
        dataMsg = view.findViewById(R.id.dataMsg)

        view.setOnClickListener {
            if (isStepAvailable) {
                SingleChoiceAlertDialog(context).apply {
                    titleResId = R.string.choose_rgb
                    dismissOnChoiceSelection = true
                    iconProperties = IconProperties(
                            iconColor = if (ThemeUtils.isLightTheme(context)) Color.WHITE else Color.BLACK,
                            backgroundDrawable = ContextCompat.getDrawable(context, ThemeUtils.getThemeIconShape(context))
                    )
                    choiceList = listOf(
                            SingleChoiceAlertDialog.ChoiceItem(
                                    titleResId = R.string.section_kcal_backup,
                                    iconResId = R.drawable.ic_color,
                                    action = {
                                        updateData(FadeBehavior.defaultKCALRGB(context))
                                    }
                            ),
                            SingleChoiceAlertDialog.ChoiceItem(
                                    titleResId = R.string.custom_rgb,
                                    iconResId = R.drawable.ic_color,
                                    action = {
                                        val colorPickerIntent = Intent(context, ColorControlActivity::class.java)
                                        colorPickerIntent.putExtra(Constants.COLOR_PICKER_MODE, true)

                                        activity.startActivityForResult(colorPickerIntent, 43)
                                    }
                            )
                    )
                }.show()
            }
        }

        updateUI()

        return view
    }

    override fun isStepDataValid(stepData: PickedColorData?): IsDataValid {
        // We allow no selections only if this step is not available
        return if (!isStepAvailable) {
            IsDataValid(true, "")
        } else {
            val isSelected = data != null

            IsDataValid(isSelected, if (isSelected) "" else context.getString(R.string.profile_data_edit_error))
        }
    }

    override fun getStepData() = data

    override fun getStepDataAsHumanReadableString(): String {
        if (!isStepAvailable) {
            return context.getString(R.string.not_applicable_title)
        }

        return data?.summarise(context) ?: context.getString(R.string.not_selected)
    }

    override fun onStepOpened(animated: Boolean) {}

    override fun onStepClosed(animated: Boolean) {}

    override fun onStepMarkedAsCompleted(animated: Boolean) {}

    override fun onStepMarkedAsUncompleted(animated: Boolean) {}

    override fun restoreStepData(stepData: PickedColorData?) {
        // To restore the step after a configuration change, we restore the text of its EditText view.
        updateData(stepData)
    }

    private fun updateUI() {
        if (this::dataView.isInitialized && this::dataMsg.isInitialized) {
            if (!isStepAvailable) {
                dataView.setText(R.string.not_applicable_title)
                dataMsg.setText(R.string.not_applicable_desc)
            } else {
                dataMsg.setText(R.string.profile_data_edit_msg)
                dataView.text = stepDataAsHumanReadableString
            }
        }
    }

    /**
     * Update data showed in this step.
     *
     * @param data Updated data.
     */
    fun updateData(data: PickedColorData?) {
        this.data = data

        updateUI()
        markAsCompletedOrUncompleted(true)
    }
}