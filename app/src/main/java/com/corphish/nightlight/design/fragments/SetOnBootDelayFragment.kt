package com.corphish.nightlight.design.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView

import com.corphish.nightlight.R
import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.helpers.PreferenceHelper
import com.corphish.nightlight.services.NightLightAppService

/**
 * Created by avinabadalal on 13/02/18.
 * Set on boot delay fragment
 */

class SetOnBootDelayFragment : Fragment() {
    private var bootDelay: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bootDelay = PreferenceHelper.getInt(context, Constants.PREF_BOOT_DELAY, Constants.DEFAULT_BOOT_DELAY)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.card_set_on_boot_delay, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val textView = view!!.findViewById<TextView>(R.id.set_on_boot_desc_tv)
        textView.text = getString(R.string.set_on_boot_delay_desc, bootDelay.toString() + "s")

        val seekBar = view!!.findViewById<SeekBar>(R.id.set_on_boot_delay)
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                bootDelay = seekBar.progress
                textView.text = getString(R.string.set_on_boot_delay_desc, bootDelay.toString() + "s")
                PreferenceHelper.putInt(context, Constants.PREF_BOOT_DELAY, bootDelay)
            }
        })

        seekBar.progress = bootDelay

        val warn = view!!.findViewById<TextView>(R.id.set_on_boot_warn)
        warn.visibility = if (PreferenceHelper.getBoolean(context, Constants.PREF_LAST_BOOT_RES, true)) View.GONE else View.VISIBLE

        NightLightAppService.instance
                .incrementViewInitCount()
    }
}
