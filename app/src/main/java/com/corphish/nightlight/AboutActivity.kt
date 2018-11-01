package com.corphish.nightlight

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.corphish.nightlight.design.ThemeUtils

import com.corphish.nightlight.design.fragments.AboutFragment
import com.corphish.nightlight.helpers.ExternalLink
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.layout_header.*

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(ThemeUtils.getAppTheme(this))
        setContentView(R.layout.activity_about)

        fab.setOnClickListener {
            showActions()
        }

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

    private fun showActions() {
        val optionsDialog = BottomSheetDialog(this, ThemeUtils.getBottomSheetTheme(this))
        val optionsView = View.inflate(this, R.layout.bottom_sheet_donate_actions, null)

        val donateAction1 = optionsView.findViewById<View>(R.id.donateAction1)
        val donateAction2 = optionsView.findViewById<View>(R.id.donateAction2)

        if (BuildConfig.FLAVOR == "generic" || BuildConfig.FLAVOR == "foss") {
            donateAction1.setOnClickListener {
                ExternalLink.open(this, "market://details?id=com.corphish.nightlight.generic")
                optionsDialog.dismiss()
            }

            donateAction2.setOnClickListener {
                ExternalLink.open(this, "market://details?id=com.corphish.nightlight.donate")
                optionsDialog.dismiss()
            }
        } else {
            donateAction1.setOnClickListener {
                ExternalLink.open(this, "market://details?id=com.corphish.nightlight.donate")
                optionsDialog.dismiss()
            }

            donateAction2.setOnClickListener {
                ExternalLink.open(this, "https://www.paypal.me/corphish")
                optionsDialog.dismiss()
            }
        }

        optionsDialog.setContentView(optionsView)
        optionsDialog.show()
    }
}
