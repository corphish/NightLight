package com.corphish.nightlight.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import com.corphish.nightlight.BuildConfig
import com.corphish.nightlight.R

import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.design.ThemeUtils
import com.corphish.nightlight.design.alert.BottomSheetAlertDialog
import com.corphish.nightlight.design.fragments.*
import com.corphish.nightlight.engine.Core
import com.corphish.nightlight.helpers.PreferenceHelper
import com.corphish.nightlight.interfaces.NightLightSettingModeListener
import com.corphish.nightlight.interfaces.NightLightStateListener
import com.corphish.nightlight.services.NightLightAppService
import com.corphish.nightlight.engine.ProfilesManager
import com.corphish.nightlight.extensions.toArrayOfInts
import com.corphish.nightlight.interfaces.ThemeChangeListener
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), MasterSwitchFragment.MasterSwitchClickListener, NightLightStateListener, NightLightSettingModeListener, ThemeChangeListener {

    private var masterSwitchEnabled: Boolean = false
    private val containerId = R.id.container

    private var taskerError: Boolean = false
    private val REQ_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(ThemeUtils.getAppTheme(this))
        setContentView(R.layout.activity_main)

        NightLightAppService.instance
                .registerNightLightStateListener(this)
                .registerNightLightSettingModeChangeListener(this)
                .registerThemeChangeListener(this)
                .startService()

        if (savedInstanceState == null) {
            viewInit()
            setViews(masterSwitchEnabled)
        }

        supportFragmentManager.executePendingTransactions()

        NightLightAppService.instance
                .notifyInitDone()

        applyProfileIfNecessary()

        handleIntent()
    }

    private fun init() {
        masterSwitchEnabled = PreferenceHelper.getBoolean(this, Constants.PREF_MASTER_SWITCH)
    }

    private fun viewInit() {
        // Clear container
        container.removeAllViews()

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)

        fragmentTransaction
                .add(containerId, DashboardFragment())
                //.add(containerId, MasterSwitchFragment())
                .commit()
    }

    override fun onSwitchClicked(checkStatus: Boolean) {
        if (taskerError && checkStatus) {
            taskerError = false
            val intent = Intent(this, ProfilesActivity::class.java)
            intent.putExtra(Constants.TASKER_ERROR_STATUS, false)
            startActivityForResult(intent, REQ_CODE)
        }
        setViews(checkStatus)
    }

    override fun onStateChanged(newState: Boolean) {
        // Sync the force switch in ForceSwitch fragment
        for (fragment in supportFragmentManager.fragments) {
            if (fragment is DashboardFragment) {
                fragment.updateDashboard()
                break
            }
        }
    }

    override fun onModeChanged(newMode: Int) {
        /*for (fragment in supportFragmentManager.fragments) {
            (fragment as? ColorControlFragment)?.onStateChanged(newMode)
        }*/
    }

    private fun setViews(show: Boolean) {
        NightLightAppService.instance
                .resetViewCount()

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)

        if (show) {
            // Add all others conditionally
            //if (isSupported(R.bool.setting_fragment_enabled)) fragmentTransaction.add(containerId, SettingFragment())
        } else {
            val fragmentList = supportFragmentManager.fragments
            for (fragment in fragmentList) {
                if (fragment !is DashboardFragment && fragment !is MasterSwitchFragment) fragmentTransaction.remove(fragment)
            }
        }

        fragmentTransaction.commit()

        NightLightAppService.instance.notifyNewSettingMode(PreferenceHelper.getInt(this, Constants.PREF_SETTING_MODE, Constants.NL_SETTING_MODE_TEMP))
    }

    private fun isSupported(id: Int): Boolean {
        return BuildConfig.DEBUG || resources.getBoolean(id)
    }

    override fun onResume() {
        super.onResume()

        init()
        if (!masterSwitchEnabled) finish()
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        val fragments = supportFragmentManager.fragments
        for (fragment in fragments) {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
        NightLightAppService.instance.destroy()
    }

    /**
     * Checks if the last user setting was a profile or not
     * If it is, then it applies it
     */
    private fun applyProfileIfNecessary() {
        if (!PreferenceHelper.getBoolean(this, Constants.PREF_MASTER_SWITCH, false)) return

        val lastApplyType = PreferenceHelper.getInt(this, Constants.PREF_CUR_APPLY_TYPE, Constants.APPLY_TYPE_NON_PROFILE)
        if (lastApplyType == Constants.APPLY_TYPE_PROFILE) {
            Core.applyNightModeAsync(
                    PreferenceHelper.getBoolean(this, Constants.PREF_CUR_APPLY_EN, false),
                    this,
                    PreferenceHelper.getInt(this, Constants.PREF_CUR_PROF_MODE, Constants.NL_SETTING_MODE_TEMP),
                    PreferenceHelper.getString(this, Constants.PREF_CUR_PROF_VAL, null)!!.toArrayOfInts(",")
            )
        }
    }

    private fun handleIntent() {
        if (intent.getBooleanExtra(Constants.TASKER_ERROR_STATUS, false)) {
            taskerError = true
            showTaskerErrorMessage()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != REQ_CODE) return
        setResult(RESULT_OK, data)
        finish()
    }

    private fun showTaskerErrorMessage() {
        val bottomSheetAlertDialog = BottomSheetAlertDialog(this)
        bottomSheetAlertDialog.setTitle(R.string.tasker_error_title)
        bottomSheetAlertDialog.setMessage(R.string.tasker_error_desc)
        bottomSheetAlertDialog.setPositiveButton(android.R.string.ok, View.OnClickListener { })
        bottomSheetAlertDialog.show()
    }

    private fun getProfileForCurrentSettings(): ProfilesManager.Profile {
        // Setting mode
        val mode = PreferenceHelper.getInt(this, Constants.PREF_SETTING_MODE, Constants.NL_SETTING_MODE_TEMP)

        val type = PreferenceHelper.getInt(this, Constants.PREF_INTENSITY_TYPE, Constants.INTENSITY_TYPE_MAXIMUM)

        val settings =
                if (mode == Constants.NL_SETTING_MODE_MANUAL)
                    intArrayOf(
                            PreferenceHelper.getInt(this, Constants.PREF_RED_COLOR[type], Constants.DEFAULT_RED_COLOR[type]),
                            PreferenceHelper.getInt(this, Constants.PREF_GREEN_COLOR[type], Constants.DEFAULT_GREEN_COLOR[type]),
                            PreferenceHelper.getInt(this, Constants.PREF_BLUE_COLOR[type], Constants.DEFAULT_BLUE_COLOR[type])
                    )
                else
                    intArrayOf(
                            PreferenceHelper.getInt(this, Constants.PREF_COLOR_TEMP[type], Constants.DEFAULT_COLOR_TEMP[type])
                    )

        val status = PreferenceHelper.getBoolean(this, Constants.PREF_FORCE_SWITCH, false)

        return ProfilesManager.Profile(
                name = "",
                isSettingEnabled = status,
                settingMode = mode,
                settings = settings
        )
    }

    override fun onThemeChanged(isLightTheme: Boolean) {
        PreferenceHelper.putBoolean(this, Constants.PREF_THEME_CHANGE_EVENT, true)
        recreate()
    }
}
