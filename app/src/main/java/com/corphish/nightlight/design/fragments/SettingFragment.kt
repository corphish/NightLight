package com.corphish.nightlight.design.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.corphish.nightlight.R
import kotlinx.android.synthetic.main.layout_settings.*

class SettingFragment: Fragment() {
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
        return inflater.inflate(R.layout.layout_settings, container, false)
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
        val settingsAdapter = SettingsAdapter()

        recyclerView.invalidateItemDecorations()
        recyclerView.layoutManager = androidx.recyclerview.widget.GridLayoutManager(context, 4)
        recyclerView.adapter = settingsAdapter
        recyclerView.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.setHasFixedSize(false)

        settingsAdapter.notifyDataSetChanged()
    }

    private inner class SettingsAdapter : RecyclerView.Adapter<SettingsAdapter.CustomViewHolder>() {
        lateinit var list: List<SettingOption>

        inner class CustomViewHolder internal constructor(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {


            init {
                v.setOnClickListener(this)
            }

            override fun onClick(v: View) {
            }
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.setting_option_item, parent, false)

            return CustomViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {

        }

        override fun getItemCount() = list.size
    }

    private data class SettingOption(
            val name: Int,
            val iconId: Int,
            val fragments: List<Fragment>,
            val descriptionComputer: (() -> String)
    )
}