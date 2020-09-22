package com.corphish.nightlight.activities

import android.content.Intent
import android.os.Bundle
import com.corphish.nightlight.R
import com.corphish.nightlight.activities.base.BaseActivity
import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.design.ThemeUtils
import com.corphish.nightlight.engine.Core
import com.corphish.nightlight.helpers.PreferenceHelper
import kotlinx.android.synthetic.main.activity_bed_time.*

class BedTimeActivity : BaseActivity() {
    var freshBedTime = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(ThemeUtils.getAppTheme(this))
        setContentView(R.layout.activity_bed_time)

        useCustomActionBar()
        setActionBarTitle(R.string.bed_time_title)

        var bedTimeSwitchStatus = PreferenceHelper.getBoolean(this, Constants.PREF_WIND_DOWN, Constants.DEFAULT_WIND_DOWN)
        freshBedTime = intent.getBooleanExtra(Constants.FRESH_BED_TIME, false)
        setViews(bedTimeSwitchStatus)

        bedTimeSwitch.setOnClickListener {
            bedTimeSwitchStatus = !bedTimeSwitchStatus
            Core.applyGrayScaleAsync(bedTimeSwitchStatus, this)
            setViews(bedTimeSwitchStatus)
            PreferenceHelper.putBoolean(this, Constants.PREF_WIND_DOWN, bedTimeSwitchStatus)
            if (!bedTimeSwitchStatus) {
                if (freshBedTime) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
        }
    }

    private fun setViews(b: Boolean) {
        if (b) {
            bedTimeSwitch.setColorFilter(ThemeUtils.getNLStatusIconBackground(this, true, Constants.INTENSITY_TYPE_MINIMUM))
        } else {
            bedTimeSwitch.setColorFilter(ThemeUtils.getNLStatusIconBackground(this, false, Constants.INTENSITY_TYPE_MINIMUM))
        }
    }
}
