package com.corphish.nightlight.design.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.corphish.nightlight.R
import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.helpers.PreferenceHelper
import com.corphish.nightlight.helpers.TimeUtils
import kotlinx.android.synthetic.main.layout_dashboard.*

class DashboardFragment: Fragment() {
    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null (which
     * is the default implementation).  This will be called between
     * [.onCreate] and [.onActivityCreated].
     *
     *
     * If you return a View from here, you will later be called in
     * [.onDestroyView] when the view is being released.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return Return the View for the fragment's UI, or null.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_dashboard, container, false)
    }

    /**
     * Called immediately after [.onCreateView]
     * has returned, but before any saved state has been restored in to the view.
     * This gives subclasses a chance to initialize themselves once
     * they know their view hierarchy has been completely created.  The fragment's
     * view hierarchy is not however attached to its parent at this point.
     * @param view The View returned by [.onCreateView].
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        updateDashboard()
    }

    public fun updateDashboard() {
        val masterSwitch = PreferenceHelper.getBoolean(context, Constants.PREF_MASTER_SWITCH, false)
        val nlState = PreferenceHelper.getBoolean(context, Constants.PREF_FORCE_SWITCH, false)

        if (!masterSwitch) {
            onMasterSwitchDisabled()

            return
        }

        val color = context?.resources?.getColor(R.color.colorPrimary)
        nlBulb.setColorFilter(if (nlState) color!! else Color.GRAY)

        nlMainStatus.text = if (nlState) "Night Light is on" else "Night Light is off"

        val autoStartTime = PreferenceHelper.getString(context, Constants.PREF_START_TIME, null)
        val autoEndTime = PreferenceHelper.getString(context, Constants.PREF_END_TIME, null)

        if (autoStartTime == null || autoEndTime == null) {
            nlSubStatus.text = "Automatic schedule is not configured"
        } else {
            val autoStatus = TimeUtils.determineWhetherNLShouldBeOnOrNot(autoStartTime, autoEndTime)
            val remainingHours = if (autoStatus) TimeUtils.getRemainingTimeInSchedule(autoEndTime) else TimeUtils.getRemainingTimeToSchedule(autoStartTime)

            if (autoStatus) {
                nlSubStatus.text = "Inside of automatic schedule time period. Night Light will be automatically turned off in about $remainingHours hour(s)."
            } else {
                nlSubStatus.text = "Outside of automatic schedule time period. Night Light will be automatically turned on in about $remainingHours hour(s)."
            }
        }

        nlDashboardAction.visibility = View.VISIBLE
    }

    private fun onMasterSwitchDisabled() {
        nlBulb.setColorFilter(Color.GRAY)
        nlMainStatus.text = "Night Light is disabled"
        nlSubStatus.text = "Turn on master switch to enable Night Light"
        nlDashboardAction.visibility = View.GONE
    }
}