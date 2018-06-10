package com.corphish.nightlight.design.utils

import android.content.Context
import android.content.res.Resources
import android.view.View
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.res.ResourcesCompat
import com.corphish.nightlight.R

class FontUtils {
    fun setCustomFont(context: Context, vararg views: View?) {
        try {
            for (view in views) {
                when (view) {
                    is SwitchCompat? -> view?.typeface = ResourcesCompat.getFont(context, R.font.cust_font)
                    is AppCompatCheckBox? -> view?.typeface = ResourcesCompat.getFont(context, R.font.cust_font)
                }
            }
        } catch (e: Resources.NotFoundException) {}
    }
}