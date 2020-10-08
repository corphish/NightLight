package com.corphish.nightlight.activities

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.corphish.nightlight.R
import com.corphish.nightlight.activities.base.BaseActivity
import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.design.ThemeUtils
import com.corphish.nightlight.engine.Core
import com.corphish.nightlight.helpers.PreferenceHelper

class ColorActivity : BaseActivity() {

    private var originalState = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(ThemeUtils.getAppTheme(this))
        setContentView(R.layout.activity_color)

        useCollapsingActionBar()
        setActionBarTitle(R.string.section_color)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        originalState = PreferenceHelper.getBoolean(this, Constants.PREF_FORCE_SWITCH, false)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)

        navView.setupWithNavController(navController)

        val settingType = PreferenceHelper.getInt(this, Constants.PREF_SETTING_MODE, Constants.NL_SETTING_MODE_TEMP)
        navView.selectedItemId = arrayOf(R.id.navigation_temperature, R.id.navigation_manual)[settingType]

        setActionBarTitle(title.toString())
    }

    override fun onDestroy() {
        super.onDestroy()

        Core.fixNightMode(this, originalState)
    }
}
