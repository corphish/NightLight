package com.corphish.nightlight.design.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.corphish.nightlight.R
import com.corphish.nightlight.design.fragments.base.BaseBottomSheetDialogFragment
import com.corphish.nightlight.helpers.ExternalLink
import kotlinx.android.synthetic.main.bottom_sheet_msg.*

class ProFragment: BaseBottomSheetDialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.bottom_sheet_msg, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        title.text = getString(R.string.pro_long_title)
        content.text = getString(R.string.pro_desc)
        positiveButton.text = getString(android.R.string.ok)
        positiveButton.visibility = View.VISIBLE
        positiveButton.setOnClickListener {
            ExternalLink.open(requireContext(), "market://details?id=com.corphish.nightlight.donate")
            dismiss()
        }
        negativeButton.text = getString(android.R.string.cancel)
        negativeButton.visibility = View.VISIBLE
        negativeButton.setOnClickListener { dismiss() }
    }
}