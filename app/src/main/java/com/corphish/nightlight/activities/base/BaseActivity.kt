package com.corphish.nightlight.activities.base

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.annotation.StringRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.corphish.nightlight.R
import com.corphish.nightlight.design.ThemeUtils
import kotlinx.android.synthetic.main.layout_action_bar.*

/**
 * Base activity provides common functionality to the ones
 * using this.
 */
open class BaseActivity: AppCompatActivity() {
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
            if (tvTitle != null) {
                tvTitle.setTextColor(Color.BLACK)
            }
        }
    }

    /**
     * Method which sets the string title of the custom
     * action bar.
     *
     * @param id String resource id.
     */
    fun setActionBarTitle(@StringRes id: Int) {
        tvTitle.setText(id)
    }

    /**
     * Method which sets the string title of the custom
     * action bar.
     *
     * @param str String.
     */
    fun setActionBarTitle(str: String) {
        if (tvTitle != null) {
            tvTitle.text = str
        }
    }
}