package com.corphish.nightlight.activities.base

import androidx.annotation.StringRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.corphish.nightlight.R
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