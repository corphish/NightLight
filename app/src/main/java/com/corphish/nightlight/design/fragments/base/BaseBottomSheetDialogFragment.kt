package com.corphish.nightlight.design.fragments.base

import android.os.Bundle
import com.corphish.nightlight.design.ThemeUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseBottomSheetDialogFragment: BottomSheetDialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NORMAL, ThemeUtils.getBottomSheetTheme(requireContext()))
    }
}