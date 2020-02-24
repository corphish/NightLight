package com.corphish.nightlight.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.corphish.nightlight.R
import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.design.ThemeUtils
import com.corphish.nightlight.design.alert.BottomSheetAlertDialog
import com.corphish.nightlight.helpers.PreferenceHelper
import kotlinx.android.synthetic.main.activity_master_switch.*

class MasterSwitchActivity : AppCompatActivity() {

    private var masterSwitchStatus = false
    private var freshStart = false

    private var taskerError = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(ThemeUtils.getAppTheme(this))
        setContentView(R.layout.activity_master_switch)

        masterSwitchStatus = PreferenceHelper.getBoolean(this, Constants.PREF_MASTER_SWITCH, false)
        freshStart = intent.getBooleanExtra(Constants.FRESH_START, false)

        setViews(masterSwitchStatus)
        setOnClickListeners()
        handleIntent()
    }

    private fun setViews(b: Boolean) {
        if (b) {
            masterSwitch.setColorFilter(ThemeUtils.getNLStatusIconBackground(this, true, Constants.INTENSITY_TYPE_MAXIMUM))
            masterSwitchDesc.setText(R.string.master_switch_desc_on)
        } else {
            masterSwitch.setColorFilter(ThemeUtils.getNLStatusIconBackground(this, false, Constants.INTENSITY_TYPE_MAXIMUM))
            masterSwitchDesc.setText(R.string.master_switch_desc_off)
        }
    }

    private fun setOnClickListeners() {
        masterSwitch.setOnClickListener {
            masterSwitchStatus = !masterSwitchStatus

            setViews(masterSwitchStatus)
            conditionallySwitchToMain()

            PreferenceHelper.putBoolean(this, Constants.PREF_MASTER_SWITCH, masterSwitchStatus)
        }
    }

    private fun conditionallySwitchToMain() {
        if (freshStart && masterSwitchStatus) {
            if (taskerError) {
                taskerError = false
                val intent = Intent(this, ProfilesActivity::class.java)
                intent.putExtra(Constants.TASKER_ERROR_STATUS, false)
                startActivityForResult(intent, REQ_CODE)
            } else {
                startActivity(Intent(this, MainActivity::class.java))
            }
            finish()
        }
    }

    private fun handleIntent() {
        if (intent.getBooleanExtra(Constants.TASKER_ERROR_STATUS, false)) {
            taskerError = true
            showTaskerErrorMessage()
        } else conditionallySwitchToMain()
    }

    private fun showTaskerErrorMessage() {
        val bottomSheetAlertDialog = BottomSheetAlertDialog(this)
        bottomSheetAlertDialog.setTitle(R.string.tasker_error_title)
        bottomSheetAlertDialog.setMessage(R.string.tasker_error_desc)
        bottomSheetAlertDialog.setPositiveButton(android.R.string.ok, View.OnClickListener { })
        bottomSheetAlertDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != REQ_CODE) return
        setResult(RESULT_OK, data)
        finish()
    }
}
