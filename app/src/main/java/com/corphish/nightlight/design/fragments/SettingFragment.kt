package com.corphish.nightlight.design.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.corphish.nightlight.R
import com.corphish.nightlight.services.NightLightAppService
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
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
        settingsAdapter.list = listOf(
                SettingOption(R.string.section_color, R.drawable.ic_color_white_24dp, ColorControlFragment()),
                SettingOption(R.string.section_auto, R.drawable.ic_alarm_white_24dp, AutoFragment()),
                SettingOption(R.string.section_kcal_backup, R.drawable.ic_settings_backup_restore_white_24dp, KCALBackupSettingsFragment()),
                SettingOption(R.string.section_sob, R.drawable.ic_timer_white_24dp, SetOnBootDelayFragment())
        )

        recyclerView.invalidateItemDecorations()
        recyclerView.layoutManager = androidx.recyclerview.widget.GridLayoutManager(context, 4)
        recyclerView.adapter = settingsAdapter
        recyclerView.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.setHasFixedSize(false)

        settingsAdapter.notifyDataSetChanged()

        NightLightAppService.instance.incrementViewInitCount()
    }

    private inner class SettingsAdapter : RecyclerView.Adapter<SettingsAdapter.CustomViewHolder>() {
        lateinit var list: List<SettingOption>

        inner class CustomViewHolder internal constructor(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
            internal var icon = v.findViewById<ImageButton>(R.id.settingOptionIcon)
            internal var caption = v.findViewById<TextView>(R.id.settingOptionCaption)

            init {
                v.setOnClickListener(this)
                icon.setOnClickListener(this)
            }

            override fun onClick(v: View) {
                list[adapterPosition].fragment.show(childFragmentManager, "")
            }
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.setting_option_item, parent, false)

            return CustomViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
            holder.icon.setImageResource(list[position].iconId)
            holder.caption.setText(list[position].name)
        }

        override fun getItemCount() = list.size
    }

    /**
     * This data class holds information about a setting option item
     * Each setting icon is represented as a circular icon followed by a label.
     * Tapping on it brings up a bottom sheet for the corresponding option
     * name - Name of the option
     * iconId - Icon res id
     * fragment - Fragment for the corresponding setting
     */
    private data class SettingOption(
            val name: Int,
            val iconId: Int,
            val fragment: BottomSheetDialogFragment
    )
}