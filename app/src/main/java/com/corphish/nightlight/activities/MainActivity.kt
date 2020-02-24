package com.corphish.nightlight.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.corphish.nightlight.R

import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.design.ThemeUtils
import com.corphish.nightlight.design.fragments.*
import com.corphish.nightlight.engine.Core
import com.corphish.nightlight.helpers.PreferenceHelper
import com.corphish.nightlight.interfaces.NightLightSettingModeListener
import com.corphish.nightlight.interfaces.NightLightStateListener
import com.corphish.nightlight.services.NightLightAppService
import com.corphish.nightlight.extensions.toArrayOfInts
import com.corphish.nightlight.interfaces.ThemeChangeListener
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), NightLightStateListener, NightLightSettingModeListener, ThemeChangeListener {

    private var masterSwitchEnabled: Boolean = false
    private val containerId = R.id.container

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

    override fun onStateChanged(newState: Boolean) {
        // Sync the force switch in ForceSwitch fragment
        for (fragment in supportFragmentManager.fragments) {
            if (fragment is DashboardFragment) {
                fragment.updateDashboard()
                break
            }
        }
    }

    override fun onModeChanged(newMode: Int) {}

    private fun setViews(show: Boolean) {
        NightLightAppService.instance
                .resetViewCount()

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)

        if (!show) {
            val fragmentList = supportFragmentManager.fragments
            for (fragment in fragmentList) {
                if (fragment !is DashboardFragment) fragmentTransaction.remove(fragment)
            }
        }

        fragmentTransaction.commit()

        NightLightAppService.instance.notifyNewSettingMode(PreferenceHelper.getInt(this, Constants.PREF_SETTING_MODE, Constants.NL_SETTING_MODE_TEMP))
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

    override fun onThemeChanged(isLightTheme: Boolean) {
        PreferenceHelper.putBoolean(this, Constants.PREF_THEME_CHANGE_EVENT, true)
        recreate()
    }
}
