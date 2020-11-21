package com.corphish.nightlight.design.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.corphish.nightlight.R
import com.corphish.nightlight.activities.SettingsActivity
import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.design.ThemeUtils
import com.corphish.nightlight.engine.Core
import com.corphish.nightlight.helpers.PreferenceHelper
import kotlinx.android.synthetic.main.layout_dashboard_v2.*

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
        return inflater.inflate(R.layout.layout_dashboard_v2, container, false)
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

        moreOptions.setOnClickListener {
            requireContext().startActivity(Intent(requireContext(), SettingsActivity::class.java))
        }
    }

    private fun toggleNightLight() {
        val curState = !PreferenceHelper.getBoolean(context, Constants.PREF_FORCE_SWITCH, false)

        Core.applyNightModeAsync(curState, context)
    }

    fun updateDashboard() {
        val nlState = PreferenceHelper.getBoolean(context, Constants.PREF_FORCE_SWITCH, false)
        nlBulb.setColorFilter(ThemeUtils.getNLStatusIconBackground(requireContext(), nlState))
        nlBulb.setOnClickListener {
            toggleNightLight()
        }
    }
}