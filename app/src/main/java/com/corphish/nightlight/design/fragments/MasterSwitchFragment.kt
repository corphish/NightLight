package com.corphish.nightlight.design.fragments

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.engine.Core
import com.corphish.nightlight.helpers.PreferenceHelper
import com.corphish.nightlight.R
import com.corphish.nightlight.design.utils.FontUtils
import com.corphish.nightlight.engine.KCALManager
import kotlinx.android.synthetic.main.layout_master_switch.*

/**
 * Created by Avinaba on 10/23/2017.
 * Master switch fragment
 */

class MasterSwitchFragment : Fragment() {

    private var mCallback: MasterSwitchClickListener? = null
    internal var enabled: Boolean = false

    interface MasterSwitchClickListener {
        fun onSwitchClicked(checkStatus: Boolean)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enabled = PreferenceHelper.getBoolean(context, Constants.PREF_MASTER_SWITCH)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (context as Activity) as MasterSwitchClickListener?
        } catch (e: ClassCastException) {
            throw ClassCastException(activity!!.toString() + " must implement MasterSwitchClickListener")
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.layout_master_switch, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        masterSwitch.isChecked = enabled
        masterSwitch.setOnCheckedChangeListener { _, b ->
            // Backup KCAL values here too, irrespective of whether any backup switches are enabled or not
            // Only backup if b is true though, before applying night light
            // Coz if b is on now, b was off previously, which means night light was off
            // Whether or not to use the backed up values depends on user
            // And this actually gives "Preserve everytime before enabling NL" switch its purpose
            if (b) KCALManager.backupCurrentKCALValues(context)

            Core.applyNightModeAsync(b, context)
            PreferenceHelper.putBoolean(context, Constants.PREF_MASTER_SWITCH, b)
            if (mCallback != null) mCallback!!.onSwitchClicked(b)
        }

        FontUtils().setCustomFont(context!!, masterSwitch)
    }
}
