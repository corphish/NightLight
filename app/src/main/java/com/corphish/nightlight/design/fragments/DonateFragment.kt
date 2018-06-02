package com.corphish.nightlight.design.fragments

import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.corphish.nightlight.helpers.ExternalLink
import com.corphish.nightlight.R

/**
 * Created by Avinaba on 10/24/2017.
 * Donate fragment
 */

class DonateFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.card_donation_info, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        view!!.setOnClickListener { showDonateActions() }
    }

    private fun showDonateActions() {
        val optionsDialog = BottomSheetDialog(context!!, R.style.BottomSheetDialogDark)
        val optionsView = View.inflate(context, R.layout.bottom_sheet_donate_actions, null)

        optionsView.findViewById<View>(R.id.donate_action1).setOnClickListener {
            ExternalLink.open(context, "market://details?id=com.corphish.nightlight.donate")
            optionsDialog.dismiss()
        }

        optionsView.findViewById<View>(R.id.donate_action2).setOnClickListener {
            ExternalLink.open(context, "https://www.paypal.me/corphish")
            optionsDialog.dismiss()
        }

        optionsDialog.setContentView(optionsView)
        optionsDialog.show()
    }
}
