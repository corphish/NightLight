package com.corphish.nightlight.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.corphish.nightlight.R
import com.corphish.nightlight.activities.base.BaseActivity
import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.databinding.ActivityMasterSwitchBinding
import com.corphish.nightlight.design.ThemeUtils
import com.corphish.nightlight.design.alert.BottomSheetAlertDialog
import com.corphish.nightlight.helpers.PreferenceHelper

const val REQ_CODE = 100
class MasterSwitchActivity : BaseActivity() {

    private var masterSwitchStatus = false
    private var freshStart = false

    private var taskerError = false

    // ViewBinding
    private lateinit var binding: ActivityMasterSwitchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(ThemeUtils.getAppTheme(this))

        binding = ActivityMasterSwitchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        useCustomActionBar()
        setActionBarTitle(R.string.app_name)

        masterSwitchStatus = PreferenceHelper.getBoolean(this, Constants.PREF_MASTER_SWITCH, false)
        freshStart = intent.getBooleanExtra(Constants.FRESH_START, false)

        setViews(masterSwitchStatus)
        setOnClickListeners()
        handleIntent()
    }

    private fun setViews(b: Boolean) {
        if (b) {
            binding.masterSwitch.setColorFilter(ThemeUtils.getNLStatusIconBackground(this, true))
            binding.masterSwitchDesc.setText(R.string.master_switch_desc_on)
        } else {
            binding.masterSwitch.setColorFilter(ThemeUtils.getNLStatusIconBackground(this, false))
            binding.masterSwitchDesc.setText(R.string.master_switch_desc_off)
        }
    }

    private fun setOnClickListeners() {
        binding.masterSwitch.setOnClickListener {
            masterSwitchStatus = !masterSwitchStatus

            setViews(masterSwitchStatus)
            conditionallySwitchToMain()

            PreferenceHelper.putBoolean(this, Constants.PREF_MASTER_SWITCH, masterSwitchStatus)
        }
    }

    private fun conditionallySwitchToMain() {
        if (freshStart && masterSwitchStatus) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else if (masterSwitchStatus && taskerError) {
            taskerError = false
            val intent = Intent(this, ProfilesActivity::class.java)
            intent.putExtra(Constants.TASKER_ERROR_STATUS, false)
            startActivityForResult(intent, REQ_CODE)
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
