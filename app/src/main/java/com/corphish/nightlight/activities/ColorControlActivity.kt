package com.corphish.nightlight.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.corphish.nightlight.R
import com.corphish.nightlight.activities.base.BaseActivity
import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.design.ThemeUtils
import com.corphish.nightlight.design.fragments.ManualFragment
import com.corphish.nightlight.design.fragments.TemperatureFragment
import com.corphish.nightlight.engine.Core
import com.corphish.nightlight.engine.models.PickedColorData
import com.corphish.nightlight.helpers.PreferenceHelper
import com.corphish.nightlight.interfaces.ColorPickerCallback
import com.corphish.widgets.ktx.dialogs.OnBoardingDialog
import com.corphish.widgets.ktx.dialogs.SingleChoiceAlertDialog
import com.corphish.widgets.ktx.dialogs.properties.IconProperties

class ColorControlActivity : BaseActivity(), ColorPickerCallback {

    // Original state
    private var originalState = false

    // We temporarily disable fade setting when in this activity
    private var originalFadeSetting = false

    // Color picker mode
    private var colorPickingMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(ThemeUtils.getAppTheme(this))
        setContentView(R.layout.activity_color_control)

        useCollapsingActionBar()

        originalState = PreferenceHelper.getBoolean(this, Constants.PREF_FORCE_SWITCH, false)
        colorPickingMode = intent.getBooleanExtra(Constants.COLOR_PICKER_MODE, false)

        // By default, set color picking as ok
        if (colorPickingMode) {
            setResult(RESULT_OK)
            showColorPickingHelp()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(!colorPickingMode)

        originalFadeSetting = PreferenceHelper.getBoolean(this, Constants.PREF_FADE_ENABLED, false)
        PreferenceHelper.putBoolean(this, Constants.PREF_FADE_ENABLED, false)

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
        // Set color picking mode
        val colorPickingModeData = Bundle()
        colorPickingModeData.putBoolean(Constants.COLOR_PICKER_MODE, colorPickingMode)

        val fragment: Fragment = if (mode == Constants.NL_SETTING_MODE_TEMP)
            TemperatureFragment() else ManualFragment()

        fragment.arguments = colorPickingModeData

        supportFragmentManager.beginTransaction()
                .replace(
                        R.id.fragmentHolder,
                        fragment
                )
                .commit()
    }

    override fun onDestroy() {
        super.onDestroy()

        PreferenceHelper.putBoolean(this, Constants.PREF_FADE_ENABLED, originalFadeSetting)
        Core.fixNightMode(this, originalState)
    }

    /**
     * Called when user picks a color.
     *
     * @param pickedData Picked color.
     */
    override fun onColorPicked(pickedData: PickedColorData) {
        val resIntent = Intent()
        pickedData.updateIntent(resIntent)
        setResult(RESULT_OK, resIntent)
    }

    /**
     * Shows the color picking tutorial to the users.
     */
    private fun showColorPickingHelp() {
        val prefKey = "pref_color_pick_help"
        val isShown = PreferenceHelper.getBoolean(this, prefKey, false)

        if (!isShown) {
            OnBoardingDialog(this).apply {
                slides = listOf(
                        OnBoardingDialog.Slide(
                                titleResId = R.string.color_picking_mode_title,
                                messageResId = R.string.color_picking_slide_1,
                                animation = R.raw.colors
                        ),
                        OnBoardingDialog.Slide(
                                titleResId = R.string.color_picking_mode_title,
                                messageResId = R.string.color_picking_slide_2,
                                animation = R.raw.colors
                        ),
                )
            }.show()
            PreferenceHelper.putBoolean(this, prefKey, true)
        }
    }
}