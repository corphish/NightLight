package com.corphish.nightlight.design.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.corphish.nightlight.R
import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.design.fragments.base.BaseBottomSheetDialogFragment
import com.corphish.nightlight.design.utils.FontUtils
import com.corphish.nightlight.helpers.PreferenceHelper
import com.corphish.nightlight.services.NightLightAppService
import kotlinx.android.synthetic.main.layout_options.*

class OptionsFragment: BaseBottomSheetDialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.layout_options, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        lightTheme.isChecked = PreferenceHelper.getBoolean(context, Constants.PREF_LIGHT_THEME, Constants.DEFAULT_LIGHT_THEME)

        lightTheme.setOnCheckedChangeListener { _, b ->
            PreferenceHelper.putBoolean(context, Constants.PREF_LIGHT_THEME, b)
            NightLightAppService.instance.notifyThemeChanged(b)
            dismiss()
        }

        showInfo.isChecked = PreferenceHelper.getBoolean(context, Constants.PREF_SHOW_INFO, Constants.DEFAULT_SHOW_INFO)

        showInfo.setOnCheckedChangeListener { _, b ->
            PreferenceHelper.putBoolean(context, Constants.PREF_SHOW_INFO, b)
        }

        FontUtils().setCustomFont(context!!, lightTheme, showInfo)
    }
}