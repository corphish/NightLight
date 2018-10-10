package com.corphish.nightlight.design.fragments.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

abstract class FullyExpandedBottomSheetDialogFragment: BaseBottomSheetDialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // From https://stackoverflow.com/questions/35937453/set-state-of-bottomsheetdialogfragment-to-expanded
        dialog.setOnShowListener {
            val d = it as BottomSheetDialog
            val bottomSheetInternal = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            BottomSheetBehavior.from(bottomSheetInternal!!).setState(BottomSheetBehavior.STATE_EXPANDED)
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }
}