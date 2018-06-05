package com.corphish.nightlight.design.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.corphish.nightlight.helpers.ExternalLink
import com.corphish.nightlight.R

/**
 * Created by avinabadalal on 31/12/17.
 * Contributors fragment
 */

class ContributorsFragment : androidx.fragment.app.Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.card_contributors, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        view!!.setOnClickListener { ExternalLink.open(context, "https://github.com/corphish/NightLight/graphs/contributors") }
    }
}
