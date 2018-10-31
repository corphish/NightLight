package com.corphish.nightlight.design.fragments.base

import android.os.Bundle
import com.corphish.nightlight.R
import com.corphish.nightlight.design.ThemeUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseBottomSheetDialogFragment: BottomSheetDialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, ThemeUtils.getBottomSheetTheme(context!!))
    }
}