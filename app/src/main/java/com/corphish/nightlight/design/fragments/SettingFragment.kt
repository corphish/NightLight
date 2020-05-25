package com.corphish.nightlight.design.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.corphish.nightlight.BuildConfig
import com.corphish.nightlight.R
import com.corphish.nightlight.activities.*
import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.helpers.ExternalLink
import com.corphish.nightlight.helpers.PreferenceHelper
import com.corphish.nightlight.services.NightLightAppService
import com.corphish.widgets.ktx.adapters.Adapters
import com.corphish.widgets.ktx.viewholders.ClickableViewHolder
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.layout_settings.*

class SettingFragment: DialogFragment() {
    private lateinit var settingsOptions: List<SettingOption>

    private val _optionsFragmentIndex = 8

    private var toResumeThemeChangeAction = false

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
        settingsOptions = listOfNotNull(
                SettingOption(R.string.section_main, R.drawable.ic_power, activityClass = MasterSwitchActivity::class.java),
                SettingOption(R.string.section_color, R.drawable.ic_color, activityClass = ColorActivity::class.java),
                SettingOption(R.string.bed_time_title, R.drawable.ic_bed_time_icon, activityClass = BedTimeActivity::class.java),
                SettingOption(R.string.section_auto, R.drawable.ic_alarm, activityClass = AutomationActivity::class.java),
                SettingOption(R.string.section_kcal_backup, R.drawable.ic_settings_backup_restore, KCALBackupSettingsFragment()),
                SettingOption(R.string.section_sob, R.drawable.ic_timer, SetOnBootDelayFragment()),
                SettingOption(R.string.profile_title, R.drawable.ic_profiles, activityClass =  ProfilesActivity::class.java),
                SettingOption(R.string.kcal_driver_information_short, R.drawable.ic_driver, KCALDriverInfoFragment()),
                SettingOption(R.string.options, R.drawable.ic_settings, OptionsFragment()),
                SettingOption(R.string.show_support, R.drawable.ic_thumb_up, AppreciationFragment()),
                if (BuildConfig.FLAVOR == "generic") SettingOption(R.string.pro_short_title, R.drawable.ic_pro, ProFragment()) else null,
                SettingOption(R.string.about, R.drawable.ic_info, activityClass = AboutActivity::class.java),
                SettingOption(R.string.faq, R.drawable.ic_help, link = "https://github.com/corphish/NightLight/blob/master/notes/usage.md")
        )

        val onClickHandler: (View, SettingOption) -> Unit = { _: View, i: SettingOption ->
            val fragment = i.fragment
            val activityClass = i.activityClass
            when {
                fragment != null -> fragment.show(childFragmentManager, "")
                activityClass != null -> {
                    context?.startActivity(Intent(context, activityClass))
                }
                i.link != null -> {
                    ExternalLink.open(context, i.link)
                }
            }
        }

        recyclerView.invalidateItemDecorations()
        recyclerView.layoutManager = GridLayoutManager(context, resources.getInteger(R.integer.gridSpanCount))
        recyclerView.adapter = Adapters.newStaticAdapter<SettingOption, ClickableViewHolder> {
            layoutResourceId = R.layout.setting_option_item
            listItems = settingsOptions
            viewHolder = { v ->
                ClickableViewHolder(v, listOf(R.id.settingOptionIcon, R.id.settingOptionCaption)) {view, i -> onClickHandler(view, settingsOptions[i]) }
            }
            binding = { clickableViewHolder, item ->
                clickableViewHolder.getViewById<ImageButton>(R.id.settingOptionIcon)?.setImageResource(item.iconId)
                clickableViewHolder.getViewById<ImageButton>(R.id.settingOptionIcon)?.setOnClickListener {
                    onClickHandler(it, item)
                }
                clickableViewHolder.getViewById<TextView>(R.id.settingOptionCaption)?.text = getString(item.name)
            }
            notifyDataSetChanged = true
        }
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.setHasFixedSize(false)

        NightLightAppService.instance.incrementViewInitCount()

        if (toResumeThemeChangeAction) resumeThemeChangeAction()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toResumeThemeChangeAction = savedInstanceState != null
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
            val fragment: BottomSheetDialogFragment? = null,
            val activityClass: Class<*>? = null,
            val link: String? = null
    )

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        settingsOptions[1].fragment?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun resumeThemeChangeAction() {
        if (PreferenceHelper.getBoolean(context, Constants.PREF_THEME_CHANGE_EVENT, false)) {
            settingsOptions[_optionsFragmentIndex].fragment?.show(childFragmentManager, "")
            PreferenceHelper.putBoolean(context, Constants.PREF_THEME_CHANGE_EVENT, false)
        }

    }
}