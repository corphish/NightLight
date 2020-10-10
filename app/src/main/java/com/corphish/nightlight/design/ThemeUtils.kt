package com.corphish.nightlight.design

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat

import com.corphish.nightlight.R
import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.helpers.PreferenceHelper

object ThemeUtils {
    private val THEMES = mapOf(
            Constants.ICON_SHAPE_CIRCLE to arrayOf(R.style.AppThemeCircular_NoActionBar, R.style.AppThemeLightCircular_NoActionBar),
            Constants.ICON_SHAPE_SQUARE to arrayOf(R.style.AppThemeSquare_NoActionBar, R.style.AppThemeLightSquare_NoActionBar),
            Constants.ICON_SHAPE_ROUNDED_SQUARE to arrayOf(R.style.AppThemeRoundedSquare_NoActionBar, R.style.AppThemeLightRoundedSquare_NoActionBar),
            Constants.ICON_SHAPE_TEARDROP to arrayOf(R.style.AppThemeTeardrop_NoActionBar, R.style.AppThemeLightTeardrop_NoActionBar)
    )

    /**
     * Method to check if light theme is being used or not.
     *
     * @return Boolean indicating whether light theme is used or not.
     */
    fun isLightTheme(context: Context) =
            PreferenceHelper.getBoolean(context, Constants.PREF_LIGHT_THEME, Constants.DEFAULT_LIGHT_THEME)

    fun getAppTheme(context: Context): Int {
        val lightIndex = if (PreferenceHelper.getBoolean(context, Constants.PREF_LIGHT_THEME, Constants.DEFAULT_LIGHT_THEME)) 1 else 0
        return (THEMES[PreferenceHelper.getString(context, Constants.PREF_ICON_SHAPE, Constants.DEFAULT_ICON_SHAPE)?.toInt()] ?: error(""))[lightIndex]
    }

    fun getBottomSheetTheme(context: Context) =
            if (PreferenceHelper.getBoolean(context, Constants.PREF_LIGHT_THEME, Constants.DEFAULT_LIGHT_THEME)) R.style.BottomSheetDialogLight else R.style.BottomSheetDialogDark

    fun getNLStatusIconBackground(context: Context, enabled: Boolean): Int {
        val lightThemeEnabled = PreferenceHelper.getBoolean(context, Constants.PREF_LIGHT_THEME, Constants.DEFAULT_LIGHT_THEME)

        return if (lightThemeEnabled) {
            when (enabled) {
                false -> Color.parseColor("#e6e6e6")
                true -> ContextCompat.getColor(context, R.color.circleBackgroundLight)
            }
        } else {
            when (enabled) {
                false -> Color.parseColor("#666666")
                true -> ContextCompat.getColor(context, R.color.colorPrimary)
            }

        }
    }

    fun getNLStatusIconForeground(context: Context, enabled: Boolean): Int {
        val lightThemeEnabled = PreferenceHelper.getBoolean(context, Constants.PREF_LIGHT_THEME, Constants.DEFAULT_LIGHT_THEME)

        if (lightThemeEnabled) {
            return when (enabled) {
                false -> Color.parseColor("#666666")
                true -> ContextCompat.getColor(context, R.color.colorPrimary)
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
            )[PreferenceHelper.getString(context, Constants.PREF_ICON_SHAPE, Constants.DEFAULT_ICON_SHAPE)?.toInt() ?: 0]
}