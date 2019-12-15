package com.corphish.nightlight.design

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat

import com.corphish.nightlight.R
import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.helpers.PreferenceHelper

object ThemeUtils {
    private val THEMES = mapOf(
            Constants.ICON_SHAPE_CIRCLE to arrayOf(R.style.AppThemeCircular, R.style.AppThemeLightCircular),
            Constants.ICON_SHAPE_SQUARE to arrayOf(R.style.AppThemeSquare, R.style.AppThemeLightSquare),
            Constants.ICON_SHAPE_ROUNDED_SQUARE to arrayOf(R.style.AppThemeRoundedSquare, R.style.AppThemeLightRoundedSquare),
            Constants.ICON_SHAPE_TEARDROP to arrayOf(R.style.AppThemeTeardrop, R.style.AppThemeLightTeardrop)
    )

    fun getAppTheme(context: Context): Int {
        val lightIndex = if (PreferenceHelper.getBoolean(context, Constants.PREF_LIGHT_THEME, Constants.DEFAULT_LIGHT_THEME)) 1 else 0
        return (THEMES[PreferenceHelper.getInt(context, Constants.PREF_ICON_SHAPE, Constants.DEFAULT_ICON_SHAPE)] ?: error(""))[lightIndex]
    }

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

    fun getThemeIconShape(context: Context) =
            arrayOf(
                    R.drawable.circle,
                    R.drawable.square,
                    R.drawable.rounded_square,
                    R.drawable.teardrop
            )[PreferenceHelper.getInt(context, Constants.PREF_ICON_SHAPE, Constants.DEFAULT_ICON_SHAPE)]
}