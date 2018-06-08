package com.corphish.nightlight

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

import com.corphish.nightlight.design.fragments.AboutFragment
import com.corphish.nightlight.helpers.ExternalLink
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            showActions()
        }

        findViewById<TextView>(R.id.banner_title).text = getString(R.string.banner_app_name, BuildConfig.VERSION_NAME)

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
        val optionsDialog = BottomSheetDialog(this, R.style.BottomSheetDialogDark)
        val optionsView = View.inflate(this, R.layout.bottom_sheet_donate_actions, null)

        optionsView.findViewById<View>(R.id.donate_action1).setOnClickListener {
            ExternalLink.open(this, "market://details?id=com.corphish.nightlight.donate")
            optionsDialog.dismiss()
        }

        optionsView.findViewById<View>(R.id.donate_action2).setOnClickListener {
            ExternalLink.open(this, "https://www.paypal.me/corphish")
            optionsDialog.dismiss()
        }

        optionsDialog.setContentView(optionsView)
        optionsDialog.show()
    }
}
