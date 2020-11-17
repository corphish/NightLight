package com.corphish.nightlight.design.steps.automation

import android.app.TimePickerDialog
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.corphish.nightlight.R
import com.corphish.nightlight.design.ThemeUtils
import com.corphish.nightlight.engine.AutomationRoutineManager
import com.corphish.nightlight.engine.models.AutomationRoutine
import com.corphish.nightlight.engine.models.AutomationRoutine.Companion.resolved
import com.corphish.nightlight.helpers.TimeUtils
import com.corphish.widgets.ktx.dialogs.SingleChoiceAlertDialog
import com.corphish.widgets.ktx.dialogs.properties.IconProperties
import ernestoyaquello.com.verticalstepperform.Step

class AutomationTimeStep(stepTitle: String?, val index: Int = -1): Step<String?>(stepTitle) {
    // Chosen data.
    var pickedData: String? = null

    // Views
    private lateinit var dataView: TextView
    private lateinit var dataMsg: TextView

    override fun createStepContentLayout(): View {
        // Here we generate the view that will be used by the library as the content of the step.
        // In this case we do it programmatically, but we could also do it by inflating an XML layout.
        val view = LayoutInflater.from(context).inflate(R.layout.layout_profile_data_step, null)

        dataView = view.findViewById(R.id.dataView)
        dataMsg = view.findViewById(R.id.dataMsg)

        view.setOnClickListener {
            SingleChoiceAlertDialog(context).apply {
                titleResId = R.string.pick_time
                animationResourceLayout = R.raw.time
                dismissOnChoiceSelection = true
                iconProperties = IconProperties(
                        iconColor = if (ThemeUtils.isLightTheme(context)) Color.WHITE else Color.BLACK,
                        backgroundDrawable = ContextCompat.getDrawable(context, ThemeUtils.getThemeIconShape(context))
                )
                choiceList = listOf(
                        SingleChoiceAlertDialog.ChoiceItem(
                                titleResId = R.string.sunset,
                                iconResId = R.drawable.ic_alarm,
                                action = {
                                    pickedData = AutomationRoutine.TIME_SUNSET
                                    updateUI()
                                    markAsCompletedOrUncompleted(true)
                                }
                        ),
                        SingleChoiceAlertDialog.ChoiceItem(
                                titleResId = R.string.sunrise,
                                iconResId = R.drawable.ic_alarm,
                                action = {
                                    pickedData = AutomationRoutine.TIME_SUNRISE
                                    updateUI()
                                    markAsCompletedOrUncompleted(true)
                                }
                        ),
                        SingleChoiceAlertDialog.ChoiceItem(
                                titleResId = R.string.custom_time,
                                iconResId = R.drawable.ic_alarm,
                                action = {
                                    val time = TimeUtils.currentTimeAsHourAndMinutes
                                    val timePickerDialog = TimePickerDialog(context, { _, i, i1 ->
                                        val selectedHour = if (i < 10) "0$i" else "" + i
                                        val selectedMinute = if (i1 < 10) "0$i1" else "" + i1
                                        pickedData = "$selectedHour:$selectedMinute"
                                        updateUI()
                                        markAsCompletedOrUncompleted(true)
                                    }, time[0], time[1], false)
                                    timePickerDialog.show()
                                }
                        ),
                )
            }.show()
        }

        updateUI()

        return view
    }

    override fun isStepDataValid(stepData: String?): IsDataValid {
        // We allow no selections only if this step is not available
        return if (pickedData == null) {
            IsDataValid(false, context.getString(R.string.time_error_empty))
        } else {
            val isValid = !AutomationRoutineManager.doesOverlap(context, AutomationRoutine(startTime = pickedData!!), index)
            IsDataValid(isValid, if (isValid) "" else context.getString(R.string.time_error_overlap))
        }
    }

    override fun getStepData() = pickedData

    override fun getStepDataAsHumanReadableString(): String {
        return pickedData?.resolved(context) ?: ""
    }

    override fun onStepOpened(animated: Boolean) {}

    override fun onStepClosed(animated: Boolean) {}

    override fun onStepMarkedAsCompleted(animated: Boolean) {}

    override fun onStepMarkedAsUncompleted(animated: Boolean) {}

    override fun restoreStepData(stepData: String?) {
        // To restore the step after a configuration change, we restore the text of its EditText view.
        this.pickedData = stepData
        updateUI()
    }

    private fun updateUI() {
        if (this::dataView.isInitialized && this::dataMsg.isInitialized) {
            dataMsg.setText(R.string.time_select)
            dataView.text = stepDataAsHumanReadableString
        }
    }
}

