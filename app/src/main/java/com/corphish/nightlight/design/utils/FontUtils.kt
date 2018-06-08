package com.corphish.nightlight.design.utils

import android.content.Context
import android.view.View
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.res.ResourcesCompat
import com.corphish.nightlight.R

class FontUtils {
    fun setCustomFont(context: Context, vararg views: View?) {
        for (view in views) {
            when (view) {
                is SwitchCompat? -> view?.typeface = ResourcesCompat.getFont(context, R.font.acme)
                is AppCompatCheckBox? -> view?.typeface = ResourcesCompat.getFont(context, R.font.acme)
            }
        }
    }
}