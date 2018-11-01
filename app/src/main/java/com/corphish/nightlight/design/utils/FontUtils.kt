package com.corphish.nightlight.design.utils

import android.content.Context
import android.view.View
import android.widget.Switch
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.content.res.ResourcesCompat
import com.corphish.nightlight.R

class FontUtils {
    fun setCustomFont(context: Context, vararg views: View?) {
        try {
            for (view in views) {
                when (view) {
                    is Switch? -> view?.typeface = ResourcesCompat.getFont(context, R.font.cust_font)
                    is AppCompatCheckBox? -> view?.typeface = ResourcesCompat.getFont(context, R.font.cust_font)
                }
            }
        } catch (e: Exception) {
            // For some reason, exceptions apart from Resources.NotFoundException are being reported.
            // Either way, make such situation not crash.
        }
    }
}