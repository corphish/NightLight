package com.corphish.nightlight.activities

import android.os.Bundle
import com.corphish.nightlight.R
import com.corphish.nightlight.activities.base.BaseActivity
import com.corphish.nightlight.design.ThemeUtils

import com.corphish.nightlight.design.fragments.AboutFragment
import com.corphish.nightlight.design.fragments.LinksFragment
import kotlinx.android.synthetic.main.activity_about.*

/**
 * This activity holds the AboutFragment and the LinksFragment.
 * These show the information and credits section of the app.
 */
class AboutActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Apply theme based on user selections
        setTheme(ThemeUtils.getAppTheme(this))
        setContentView(R.layout.activity_about)

        useCustomActionBar()
        setActionBarTitle(R.string.about)

        // Avoid showing the fragment more than one time
        if (savedInstanceState == null) {
            viewInit()
        }

        // Handle the FAB click
        fab.setOnClickListener {
            LinksFragment().show(supportFragmentManager, "")
        }
    }

    /**
     * Method to initialize and show the AboutFragment.
     */
    private fun viewInit() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)

        val containerId = R.id.layout_container

        fragmentTransaction.add(containerId, AboutFragment())

        fragmentTransaction.commit()
    }
}
