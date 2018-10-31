package com.corphish.nightlight.design


import android.content.Context

import com.corphish.nightlight.R
import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.helpers.PreferenceHelper

object ThemeUtils {
    fun getAppTheme(context: Context) =
            if (PreferenceHelper.getBoolean(context, Constants.PREF_LIGHT_THEME, Constants.DEFAULT_LIGHT_THEME)) R.style.AppThemeLight else R.style.AppTheme

    fun getBottomSheetTheme(context: Context) =
            if (PreferenceHelper.getBoolean(context, Constants.PREF_LIGHT_THEME, Constants.DEFAULT_LIGHT_THEME)) R.style.BottomSheetDialogLight else R.style.BottomSheetDialogDark
}