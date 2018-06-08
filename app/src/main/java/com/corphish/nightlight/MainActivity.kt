package com.corphish.nightlight

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout

import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.design.alert.BottomSheetAlertDialog
import com.corphish.nightlight.design.fragments.ColorTemperatureFragment
import com.corphish.nightlight.design.fragments.SetOnBootDelayFragment
import com.corphish.nightlight.engine.Core
import com.corphish.nightlight.helpers.PreferenceHelper
import com.corphish.nightlight.interfaces.NightLightSettingModeListener
import com.corphish.nightlight.interfaces.NightLightStateListener
import com.corphish.nightlight.services.NightLightAppService
import com.corphish.nightlight.design.fragments.AutoFragment
import com.corphish.nightlight.design.fragments.FilterFragment
import com.corphish.nightlight.design.fragments.ForceSwitchFragment
import com.corphish.nightlight.design.fragments.MasterSwitchFragment
import com.corphish.nightlight.extensions.toArrayOfInts
import com.google.android.material.bottomappbar.BottomAppBar

class MainActivity : AppCompatActivity(), MasterSwitchFragment.MasterSwitchClickListener, NightLightStateListener, NightLightSettingModeListener {

    private var masterSwitchEnabled: Boolean = false
    private val containerId = R.id.layout_container

    private var taskerError: Boolean = false
    private val REQ_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomAppBar = findViewById<BottomAppBar>(R.id.bottom_app_bar)
        setSupportActionBar(bottomAppBar)

        NightLightAppService.instance
                .registerNightLightStateListener(this)
                .registerNightLightSettingModeChangeListener(this)
                .startService()

        if (savedInstanceState == null) {
            init()
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
        val container = findViewById<LinearLayout>(containerId)
        container.removeAllViews()

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)

        fragmentTransaction.add(containerId, MasterSwitchFragment()).commit()
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
            if (fragment is ForceSwitchFragment) {
                fragment.updateSwitch(newState)
                break
            }
        }
    }

    override fun onModeChanged(newMode: Int) {
        for (fragment in supportFragmentManager.fragments) {
            (fragment as? FilterFragment)?.onStateChanged(newMode)
            (fragment as? ColorTemperatureFragment)?.onStateChanged(newMode)
        }
    }

    private fun setViews(show: Boolean) {
        NightLightAppService.instance
                .resetViewCount()

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)

        if (show) {
            // Add all others conditionally
            if (isSupported(R.bool.filters_enabled)) fragmentTransaction.add(containerId, FilterFragment())
            if (isSupported(R.bool.color_temperature_enabled)) fragmentTransaction.add(containerId, ColorTemperatureFragment())
            if (isSupported(R.bool.set_on_boot_delay_enabled) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) fragmentTransaction.add(containerId, SetOnBootDelayFragment())
            if (isSupported(R.bool.automation_enabled)) fragmentTransaction.add(containerId, AutoFragment())
            if (isSupported(R.bool.force_switch_enabled)) fragmentTransaction.add(containerId, ForceSwitchFragment())
        } else {
            val fragmentList = supportFragmentManager.fragments
            for (fragment in fragmentList) {
                if (fragment !is MasterSwitchFragment) fragmentTransaction.remove(fragment)
            }
        }

        fragmentTransaction.commit()

        NightLightAppService.instance.notifyNewSettingMode(PreferenceHelper.getInt(this, Constants.PREF_SETTING_MODE, Constants.NL_SETTING_MODE_FILTER))
    }

    private fun isSupported(id: Int): Boolean {
        return BuildConfig.DEBUG || resources.getBoolean(id)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.about_menu) showAbout()
        if (id == R.id.about_profiles) showProfiles()

        return super.onOptionsItemSelected(item)
    }

    private fun showAbout() {
        startActivity(Intent(this, AboutActivity::class.java))
    }

    private fun showProfiles() {
        startActivity(Intent(this, ProfilesActivity::class.java))
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        val fragments = supportFragmentManager.fragments
        for (fragment in fragments) {
            if (fragment is AutoFragment)
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
        val lastApplyType = PreferenceHelper.getInt(this, Constants.PREF_CUR_APPLY_TYPE, Constants.APPLY_TYPE_NON_PROFILE)
        if (lastApplyType == Constants.APPLY_TYPE_PROFILE) {
            Core.applyNightModeAsync(
                    PreferenceHelper.getBoolean(this, Constants.PREF_CUR_APPLY_EN, false),
                    this,
                    PreferenceHelper.getInt(this, Constants.PREF_CUR_PROF_MODE, Constants.NL_SETTING_MODE_FILTER),
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
        setResult(AppCompatActivity.RESULT_OK, data)
        finish()
    }

    private fun showTaskerErrorMessage() {
        val bottomSheetAlertDialog = BottomSheetAlertDialog(this)
        bottomSheetAlertDialog.setTitle(R.string.tasker_error_title)
        bottomSheetAlertDialog.setMessage(R.string.tasker_error_desc)
        bottomSheetAlertDialog.setPositiveButton(android.R.string.ok, View.OnClickListener { })
        bottomSheetAlertDialog.show()
    }
}
