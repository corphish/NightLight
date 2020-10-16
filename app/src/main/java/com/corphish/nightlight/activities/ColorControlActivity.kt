package com.corphish.nightlight.activities

import android.graphics.Color
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.corphish.nightlight.R
import com.corphish.nightlight.activities.base.BaseActivity
import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.design.ThemeUtils
import com.corphish.nightlight.design.fragments.ManualFragment
import com.corphish.nightlight.design.fragments.TemperatureFragment
import com.corphish.nightlight.engine.Core
import com.corphish.nightlight.helpers.PreferenceHelper
import com.corphish.widgets.ktx.dialogs.SingleChoiceAlertDialog
import com.corphish.widgets.ktx.dialogs.properties.IconProperties

class ColorControlActivity : BaseActivity() {

    // Original state
    private var originalState = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(ThemeUtils.getAppTheme(this))
        setContentView(R.layout.activity_color_control)

        useCollapsingActionBar()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        originalState = PreferenceHelper.getBoolean(this, Constants.PREF_FORCE_SWITCH, false)

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            val background = ContextCompat.getDrawable(this, ThemeUtils.getThemeIconShape(this))
            SingleChoiceAlertDialog(this).apply {
                titleResId = R.string.profile_setting_mode
                iconProperties = IconProperties(
                        iconColor = if (ThemeUtils.isLightTheme(this@ColorControlActivity)) Color.WHITE else Color.BLACK,
                        backgroundDrawable = background
                )
                dismissOnChoiceSelection = true
                choiceList = listOf(
                        SingleChoiceAlertDialog.ChoiceItem(
                                titleResId = R.string.color_temperature_title,
                                iconResId = R.drawable.ic_temperature,
                                action = { showUIForMode(Constants.NL_SETTING_MODE_TEMP) }
                        ),
                        SingleChoiceAlertDialog.ChoiceItem(
                                titleResId = R.string.manual_mode_title,
                                iconResId = R.drawable.ic_manual,
                                action = { showUIForMode(Constants.NL_SETTING_MODE_MANUAL) }
                        )
                )
            }.show()
        }

        val mode = PreferenceHelper.getInt(this, Constants.PREF_SETTING_MODE, Constants.NL_SETTING_MODE_TEMP)
        showUIForMode(mode)
    }

    /**
     * Shows UI for he selected mode.
     *
     * @param mode Mode.
     */
    private fun showUIForMode(mode: Int) {
        supportFragmentManager.beginTransaction()
                .replace(
                        R.id.fragmentHolder,
                        if (mode == Constants.NL_SETTING_MODE_TEMP) TemperatureFragment() else ManualFragment()
                )
                .commit()
    }

    override fun onDestroy() {
        super.onDestroy()

        Core.fixNightMode(this, originalState)
    }
}