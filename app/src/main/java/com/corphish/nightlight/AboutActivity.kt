package com.corphish.nightlight

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar

import com.corphish.nightlight.design.fragments.AboutFragment
import com.corphish.nightlight.design.fragments.ContributorsFragment
import com.corphish.nightlight.design.fragments.DonateFragment

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        if (savedInstanceState == null) viewInit()
    }

    private fun viewInit() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)

        val containerId = R.id.layout_container

        fragmentTransaction.add(containerId, AboutFragment())
        if (resources.getBoolean(R.bool.contributors_card_enabled)) fragmentTransaction.add(containerId, ContributorsFragment())
        fragmentTransaction.add(containerId, DonateFragment())

        fragmentTransaction.commit()
    }
}
