package com.corphish.nightlight.design

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat

import com.corphish.nightlight.R
import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.helpers.PreferenceHelper

object ThemeUtils {
    fun getAppTheme(context: Context) =
            if (PreferenceHelper.getBoolean(context, Constants.PREF_LIGHT_THEME, Constants.DEFAULT_LIGHT_THEME)) R.style.AppThemeLight else R.style.AppTheme

    fun getBottomSheetTheme(context: Context) =
            if (PreferenceHelper.getBoolean(context, Constants.PREF_LIGHT_THEME, Constants.DEFAULT_LIGHT_THEME)) R.style.BottomSheetDialogLight else R.style.BottomSheetDialogDark

    fun getNLStatusIconBackground(context: Context, enabled: Boolean, intensityType: Int): Int {
        val lightThemeEnabled = PreferenceHelper.getBoolean(context, Constants.PREF_LIGHT_THEME, Constants.DEFAULT_LIGHT_THEME)

        if (lightThemeEnabled) {
            return when (enabled) {
                false -> Color.parseColor("#e6e6e6")
                true -> when (intensityType) {
                    Constants.INTENSITY_TYPE_MAXIMUM -> Color.parseColor("#ffd6cc")
                    else ->  ContextCompat.getColor(context, R.color.circleBackgroundLight)
                }
            }
        }
        else {
            return when (enabled) {
                false -> Color.parseColor("#666666")
                true -> when (intensityType) {
                    Constants.INTENSITY_TYPE_MAXIMUM ->  ContextCompat.getColor(context, R.color.colorAccent)
                    else ->  ContextCompat.getColor(context, R.color.colorPrimary)
                }
            }

        }
    }

    fun getNLStatusIconForeground(context: Context, enabled: Boolean, intensityType: Int): Int {
        val lightThemeEnabled = PreferenceHelper.getBoolean(context, Constants.PREF_LIGHT_THEME, Constants.DEFAULT_LIGHT_THEME)

        if (lightThemeEnabled) {
            return when (enabled) {
                false -> Color.parseColor("#666666")
                true -> when (intensityType) {
                    Constants.INTENSITY_TYPE_MAXIMUM -> ContextCompat.getColor(context, R.color.colorAccent)
                    else ->  ContextCompat.getColor(context, R.color.colorPrimary)
                }
            }
        }

        return Color.parseColor("#000000")
    }
}