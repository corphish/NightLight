package com.corphish.nightlight.design.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
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

        val iconShapes = arrayOf(
                getString(R.string.circle),
                getString(R.string.square),
                getString(R.string.rounded_square),
                getString(R.string.teardrop)
        )

        lightTheme.isChecked = PreferenceHelper.getBoolean(context, Constants.PREF_LIGHT_THEME, Constants.DEFAULT_LIGHT_THEME)

        lightTheme.setOnCheckedChangeListener { _, b ->
            PreferenceHelper.putBoolean(context, Constants.PREF_LIGHT_THEME, b)
            NightLightAppService.instance.notifyThemeChanged(b)
            dismiss()
        }

        iconShape.valueText = iconShapes[PreferenceHelper.getInt(context, Constants.PREF_ICON_SHAPE, Constants.DEFAULT_ICON_SHAPE)]
        iconShape.setOnClickListener {
            val selector = AlertDialog.Builder(requireContext())
            selector.setTitle(R.string.icon_shape)
            selector.setItems(iconShapes) { _, i ->
                PreferenceHelper.putInt(context, Constants.PREF_ICON_SHAPE, i)
                NightLightAppService.instance.notifyThemeChanged(PreferenceHelper.getBoolean(context, Constants.PREF_LIGHT_THEME, Constants.DEFAULT_LIGHT_THEME))
                dismiss()
            }
            selector.show()
        }

        FontUtils().setCustomFont(requireContext(), lightTheme)
    }
}