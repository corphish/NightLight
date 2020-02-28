package com.corphish.nightlight.design.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.corphish.nightlight.R
import com.corphish.nightlight.design.fragments.base.BaseBottomSheetDialogFragment
import com.corphish.nightlight.engine.KCALManager
import kotlinx.android.synthetic.main.layout_kcal_driver_info.*

class KCALDriverInfoFragment : BaseBottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.layout_kcal_driver_info, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val kcalImplementation = KCALManager.kcalImplementation

        implName.valueText = kcalImplementation.getImplementationName()
        implSwitchPath.valueText = kcalImplementation.getImplementationSwitchPath()
        implFilePaths.valueText = kcalImplementation.getImplementationFilePaths()
        implFormat.valueText = kcalImplementation.getImplementationFormat()
    }
}