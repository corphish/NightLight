package com.corphish.nightlight

import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.design.ThemeUtils
import com.corphish.nightlight.design.alert.BottomSheetAlertDialog
import com.corphish.nightlight.design.views.ProfileCreator
import com.corphish.nightlight.engine.ProfilesManager
import com.corphish.nightlight.helpers.PreferenceHelper
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_profiles.*
import kotlinx.android.synthetic.main.content_profiles.*
import kotlinx.android.synthetic.main.layout_header.*

import java.util.Arrays

class ProfilesActivity : AppCompatActivity(), ProfilesManager.DataChangeListener {

    private lateinit var optionsDialog: BottomSheetDialog
    private lateinit var optionsView: View

    private var curProfile: ProfilesManager.Profile? = null

    private lateinit var profilesManager: ProfilesManager

    private lateinit var profilesAdapter: ProfilesAdapter

    private var profiles: MutableList<ProfilesManager.Profile>? = null

    private lateinit var context: Context

    private var midTemp = 0
    private var midKCALSum = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(ThemeUtils.getAppTheme(this))
        setContentView(R.layout.activity_profiles)

        context = this

        fab.setOnClickListener {
            ProfileCreator(this@ProfilesActivity, ProfileCreator.MODE_CREATE,
                    onFinishListener =  fun(status: Int) {
                        if (status == ProfileCreator.STATUS_SUCCESS) {
                            profilesManager.loadProfiles()
                            profiles = profilesManager.profilesList
                            profilesAdapter.notifyDataSetChanged()
                        }
                    }).show()
        }

        banner_title.text = getString(R.string.profile_title)
        banner_icon.setImageResource(R.drawable.ic_profiles_24dp)

        initProfilesManager()
        initViews()
        initMidValues()
    }

    private fun initMidValues() {
        midTemp = (PreferenceHelper.getInt(context, Constants.PREF_MAX_COLOR_TEMP, Constants.DEFAULT_MAX_COLOR_TEMP) +
                PreferenceHelper.getInt(context, Constants.PREF_MIN_COLOR_TEMP, Constants.DEFAULT_MIN_COLOR_TEMP))/2

        midKCALSum = (PreferenceHelper.getInt(context, Constants.PREF_MAX_RED_COLOR, Constants.DEFAULT_MAX_RED_COLOR) +
                PreferenceHelper.getInt(context, Constants.PREF_MIN_RED_COLOR, Constants.DEFAULT_MIN_RED_COLOR))/2
                +
                (PreferenceHelper.getInt(context, Constants.PREF_MAX_GREEN_COLOR, Constants.DEFAULT_MAX_GREEN_COLOR) +
                        PreferenceHelper.getInt(context, Constants.PREF_MIN_GREEN_COLOR, Constants.DEFAULT_MIN_GREEN_COLOR))/2
                +
                (PreferenceHelper.getInt(context, Constants.PREF_MAX_BLUE_COLOR, Constants.DEFAULT_MAX_BLUE_COLOR) +
                        PreferenceHelper.getInt(context, Constants.PREF_MIN_BLUE_COLOR, Constants.DEFAULT_MIN_BLUE_COLOR))/2
    }

    private fun initProfilesManager() {
        profilesManager = ProfilesManager(this)
        profilesManager.registerDataChangeListener(this)
        profilesManager.loadProfiles()
        profiles = profilesManager.profilesList
    }

    private fun initViews() {
        profilesAdapter = ProfilesAdapter()
        profilesAdapter.setProfiles(profiles)

        recyclerView.invalidateItemDecorations()
        recyclerView.layoutManager = GridLayoutManager(this, 4)
        recyclerView.adapter = profilesAdapter
        recyclerView.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.setHasFixedSize(false)

        profilesAdapter.notifyDataSetChanged()
    }

    override fun onDataChanged(newDataSize: Int) {
        if (newDataSize < 1)
            emptyView.visibility = View.VISIBLE
        else
            emptyView.visibility = View.GONE
    }

    private inner class ProfilesAdapter : RecyclerView.Adapter<ProfilesActivity.ProfilesAdapter.CustomViewHolder>() {
        private var profiles: List<ProfilesManager.Profile>? = null

        internal fun setProfiles(profiles: MutableList<ProfilesManager.Profile>?) {
            this.profiles = profiles
        }

        inner class CustomViewHolder internal constructor(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
            internal val icon: TextView = v.findViewById(R.id.profileIcon)
            internal val title: TextView = v.findViewById(R.id.profileTitle)

            init {
                v.setOnClickListener(this)
            }

            override fun onClick(v: View) {
                if (!intent.getBooleanExtra(Constants.TASKER_ERROR_STATUS, true)) {
                    showAlert(R.string.confirm, getString(R.string.tasker_confirm_selection, profiles!![adapterPosition].name), View.OnClickListener { returnBack(profiles!![adapterPosition].name) })
                } else {
                    curProfile = profiles!![adapterPosition]
                    optionsDialog = BottomSheetDialog(this@ProfilesActivity, ThemeUtils.getBottomSheetTheme(context))
                    getOptionsView(curProfile!!)
                    optionsDialog.setContentView(optionsView)
                    optionsDialog.show()
                }
            }
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_profile_item, parent, false)

            return CustomViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
            val profile = profiles!![position]
            val profileValue = profile.settings.sum()
            val profileIntensity =
                    if (profile.settingMode == Constants.NL_SETTING_MODE_TEMP)
                        if (profileValue > midTemp) Constants.INTENSITY_TYPE_MINIMUM else Constants.INTENSITY_TYPE_MAXIMUM
                    else
                        if (profileValue > midKCALSum) Constants.INTENSITY_TYPE_MINIMUM else Constants.INTENSITY_TYPE_MAXIMUM

            holder.icon.text = if (profile.name.isNotEmpty()) "${profile.name.toUpperCase()[0]}" else ""
            setIconBackground(holder.icon, ThemeUtils.getNLStatusIconBackground(context, profile.isSettingEnabled, profileIntensity))
            holder.icon.setTextColor(ThemeUtils.getNLStatusIconForeground(context, profile.isSettingEnabled, profileIntensity))

            holder.title.text = profile.name
        }

        private fun setIconBackground(textView: TextView, color: Int) {
            val drawable = resources.getDrawable(R.drawable.circle)
            drawable.setColorFilter(color, PorterDuff.Mode.SRC)

            textView.background = drawable
        }

        override fun getItemCount(): Int {
            return profiles!!.size
        }
    }



    private fun getOptionsView(profile: ProfilesManager.Profile?) {
        optionsView = View.inflate(this, R.layout.bottom_sheet_profile_options, null)

        val selectedProfileName = optionsView.findViewById<TextView>(R.id.selectedProfileName)
        val selectedProfileInfo = optionsView.findViewById<TextView>(R.id.selectedProfileInfo)
        val apply = optionsView.findViewById<View>(R.id.apply)
        val edit = optionsView.findViewById<View>(R.id.edit)
        val delete = optionsView.findViewById<View>(R.id.delete)

        selectedProfileName.text = profile?.name
        selectedProfileInfo.text = "${arrayOf(getString(R.string.color_temperature_title), "RGB")[profile?.settingMode!!]}: ${Arrays.toString(profile?.settings!!)}"

        apply.setOnClickListener {
            if (curProfile != null) {
                curProfile!!.apply(this@ProfilesActivity)
                PreferenceHelper.putInt(context, Constants.PREF_CUR_APPLY_TYPE, Constants.APPLY_TYPE_PROFILE)
                PreferenceHelper.putBoolean(context, Constants.PREF_CUR_APPLY_EN, curProfile!!.isSettingEnabled)
                PreferenceHelper.putInt(context, Constants.PREF_CUR_PROF_MODE, curProfile!!.settingMode)
                PreferenceHelper.putString(context, Constants.PREF_CUR_PROF_VAL, Arrays.toString(curProfile!!.settings))
            }
            optionsDialog.dismiss()
        }

        edit.setOnClickListener {
            ProfileCreator(this@ProfilesActivity, ProfileCreator.MODE_EDIT, profile,
                    onFinishListener =  fun(status: Int) {
                        if (status == ProfileCreator.STATUS_SUCCESS) {
                            profiles = profilesManager.profilesList
                            profilesAdapter.notifyDataSetChanged()
                        }
                    }).show()
            optionsDialog.dismiss()
        }

        delete.setOnClickListener {
            showAlert(R.string.delete, getString(R.string.delete_details, curProfile!!.name), View.OnClickListener {
                profilesManager.deleteProfile(curProfile!!)
                val prof = curProfile
                profiles!!.remove(prof)
                curProfile = null
                profilesAdapter.notifyDataSetChanged()
            })
            optionsDialog.dismiss()
        }
    }

    fun returnBack(name: String?) {
        if (!intent.getBooleanExtra(Constants.TASKER_ERROR_STATUS, true)) {
            val bundle = Bundle()
            bundle.putString(Constants.TASKER_SETTING, name)
            val intent = Intent()
            intent.putExtra("com.twofortyfouram.locale.intent.extra.BLURB", name)
            intent.putExtra("com.twofortyfouram.locale.intent.extra.BUNDLE", bundle)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    private fun showAlert(title: Int, message: String, positiveOnClickListener: View.OnClickListener) {
        val bottomSheetAlertDialog = BottomSheetAlertDialog(this)
        bottomSheetAlertDialog.setTitle(title)
        bottomSheetAlertDialog.setMessage(message)
        bottomSheetAlertDialog.setPositiveButton(android.R.string.ok, positiveOnClickListener)
        bottomSheetAlertDialog.setNegativeButton(android.R.string.cancel, View.OnClickListener { })
        bottomSheetAlertDialog.show()
    }
}
