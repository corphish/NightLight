package com.corphish.nightlight.activities.base

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.annotation.StringRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.corphish.nightlight.R
import com.corphish.nightlight.design.ThemeUtils
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.android.synthetic.main.layout_action_bar.*

/**
 * Base activity provides common functionality to the ones
 * using this.
 */
open class BaseActivity: AppCompatActivity() {
    /*
     * Action bar mode
     * 0 - default
     * 1 - Custom
     * 2 - Collapsing
     */
    private var actionBarMode = 0

    /**
     * Method which indicates the use of custom action bar
     */
    fun useCustomActionBar() {
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.layout_action_bar)

        // According to the docs, colorPrimary attribute defines
        // action bar color. In dark theme this does not hold true,
        // as it is black by default. But in light theme, it is as
        // expected. We would like to have light background in light
        // theme, hence this change.
        if (ThemeUtils.isLightTheme(this)) {
            supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#fefefe")))
            supportActionBar?.elevation = 0f
            if (tvTitle != null) {
                tvTitle.setTextColor(Color.BLACK)
            }
        }

        actionBarMode = 1
    }

    /**
     * Indicates the use of collapsing toolbar.
     */
    fun useCollapsingActionBar() {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        actionBarMode = 2
    }

    /**
     * Method which sets the string title of the custom
     * action bar.
     *
     * @param id String resource id.
     */
    fun setActionBarTitle(@StringRes id: Int) {
        when (actionBarMode) {
            1 -> if (tvTitle != null) tvTitle.setText(id)
            2 -> findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout)?.title = getString(id)
            else -> supportActionBar?.setTitle(id)
        }
    }

    /**
     * Method which sets the string title of the custom
     * action bar.
     *
     * @param str String.
     */
    fun setActionBarTitle(str: String) {
        when (actionBarMode) {
            1 -> if (tvTitle != null) tvTitle.text = str
            2 -> findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout)?.title = str
            else -> supportActionBar?.title = str
        }
    }
}