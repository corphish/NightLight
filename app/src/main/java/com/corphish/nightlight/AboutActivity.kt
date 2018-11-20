package com.corphish.nightlight

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.corphish.nightlight.design.ThemeUtils

import com.corphish.nightlight.design.fragments.AboutFragment
import com.corphish.nightlight.helpers.ExternalLink
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.layout_header.*

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(ThemeUtils.getAppTheme(this))
        setContentView(R.layout.activity_about)

        banner_title.text = getString(R.string.about)
        banner_icon.setImageResource(R.drawable.ic_info_24dp)

        if (savedInstanceState == null) viewInit()
    }

    private fun viewInit() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)

        val containerId = R.id.layout_container

        fragmentTransaction.add(containerId, AboutFragment())

        fragmentTransaction.commit()
    }
}
