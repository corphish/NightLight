package com.corphish.nightlight.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.corphish.nightlight.R
import com.corphish.nightlight.design.ThemeUtils

class MasterSwitchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(ThemeUtils.getAppTheme(this))
        setContentView(R.layout.activity_master_switch)
    }
}
