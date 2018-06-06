package com.corphish.nightlight.design.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.corphish.nightlight.BuildConfig
import com.corphish.nightlight.helpers.ExternalLink
import com.corphish.nightlight.R

/**
 * Created by Avinaba on 10/24/2017.
 * About fragment
 */

class AboutFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.card_info, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        view?.setOnClickListener { ExternalLink.open(context, "market://details?id=" + context!!.packageName) }

        (view?.findViewById<View>(R.id.app_version) as TextView).text = getString(R.string.app_name) + " v" + BuildConfig.VERSION_NAME
    }
}
