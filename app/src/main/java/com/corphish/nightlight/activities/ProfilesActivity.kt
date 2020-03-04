package com.corphish.nightlight.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.corphish.nightlight.R

import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.design.ThemeUtils
import com.corphish.nightlight.design.alert.BottomSheetAlertDialog
import com.corphish.nightlight.engine.ProfilesManager
import com.corphish.nightlight.helpers.PreferenceHelper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_profiles.*
import kotlinx.android.synthetic.main.content_profiles.*
import kotlinx.android.synthetic.main.layout_header.*
import java.util.*

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

    private val _createProfileCode = 6
    private val _updateProfileCode = 9

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(ThemeUtils.getAppTheme(this))
        setContentView(R.layout.activity_profiles)

        context = this

        fab.setOnClickListener {
            val intent = Intent(this@ProfilesActivity, ProfileCreateActivity::class.java)
            intent.putExtra(Constants.PROFILE_DATA_PRESENT, false)
            intent.putExtra(Constants.PROFILE_MODE, Constants.MODE_CREATE)

            startActivityForResult(intent, _createProfileCode)
        }

        banner_title.text = getString(R.string.profile_title)
        banner_icon.setImageResource(R.drawable.ic_profiles)

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
        recyclerView.layoutManager = GridLayoutManager(this, resources.getInteger(R.integer.gridSpanCount))
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

    private inner class ProfilesAdapter : RecyclerView.Adapter<ProfilesAdapter.CustomViewHolder>() {
        private var profiles: List<ProfilesManager.Profile>? = null

        internal fun setProfiles(profiles: MutableList<ProfilesManager.Profile>?) {
            this.profiles = profiles
        }

        inner class CustomViewHolder internal constructor(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener, View.OnLongClickListener {
            internal val icon: TextView = v.findViewById(R.id.profileIcon)
            internal val title: TextView = v.findViewById(R.id.profileTitle)

            init {
                v.setOnClickListener(this)
                v.setOnLongClickListener(this)
            }

            override fun onClick(v: View) {
                if (!intent.getBooleanExtra(Constants.TASKER_ERROR_STATUS, true)) {
                    showAlert(R.string.confirm, getString(R.string.tasker_confirm_selection, profiles!![adapterPosition].name), View.OnClickListener { returnBack(profiles!![adapterPosition].name) })
                } else {
                    showProfileOverviewDialog(adapterPosition)
                }
            }

            private fun showProfileOverviewDialog(pos: Int) {
                curProfile = profiles!![pos]
                optionsDialog = BottomSheetDialog(this@ProfilesActivity, ThemeUtils.getBottomSheetTheme(context))
                optionsDialog.setOnShowListener {
                    val d = it as BottomSheetDialog
                    val bottomSheetInternal = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                    BottomSheetBehavior.from(bottomSheetInternal!!).setState(BottomSheetBehavior.STATE_EXPANDED)
                }
                getOptionsView(curProfile!!)
                optionsDialog.setContentView(optionsView)
                optionsDialog.show()
            }

            override fun onLongClick(v: View?): Boolean {
                if (!intent.getBooleanExtra(Constants.TASKER_ERROR_STATUS, true)) {
                    showProfileOverviewDialog(adapterPosition)

                    return true
                }

                return false
            }
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_profile_item, parent, false)

            return CustomViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
            val profile = profiles!![position]

            holder.icon.text = if (profile.name.isNotEmpty()) "${profile.name.toUpperCase(Locale.getDefault())[0]}" else ""
            setIconBackground(holder.icon, ThemeUtils.getNLStatusIconBackground(context, profile.isSettingEnabled, getProfileIntensity(profile)))
            holder.icon.setTextColor(ThemeUtils.getNLStatusIconForeground(context, profile.isSettingEnabled, getProfileIntensity(profile)))

            holder.title.text = profile.name
        }

        override fun getItemCount(): Int {
            return profiles!!.size
        }
    }

    private fun getProfileIntensity(profile: ProfilesManager.Profile): Int {
        val profileValue = profile.settings.sum()

        return if (profile.settingMode == Constants.NL_SETTING_MODE_TEMP)
            if (profileValue > midTemp) Constants.INTENSITY_TYPE_MINIMUM else Constants.INTENSITY_TYPE_MAXIMUM
        else
            if (profileValue > midKCALSum) Constants.INTENSITY_TYPE_MINIMUM else Constants.INTENSITY_TYPE_MAXIMUM
    }

    private fun setIconBackground(textView: TextView, color: Int) {
        val drawable = ResourcesCompat.getDrawable(resources, ThemeUtils.getThemeIconShape(this), theme)
        drawable?.setColorFilter(color, PorterDuff.Mode.SRC)

        textView.background = drawable
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == _createProfileCode && resultCode == Activity.RESULT_OK) {
            profilesManager.loadProfiles()
            profiles = profilesManager.profilesList
            profilesAdapter.notifyDataSetChanged()
        } else if (requestCode == _updateProfileCode) {
            if (resultCode == Activity.RESULT_OK) {
                profilesManager.loadProfiles()
                profiles = profilesManager.profilesList
            }
            profilesAdapter.notifyDataSetChanged()
        }
    }

    private fun getOptionsView(profile: ProfilesManager.Profile?) {
        optionsView = View.inflate(this, R.layout.bottom_sheet_profile_options, null)

        val selectedProfileTitle = optionsView.findViewById<TextView>(R.id.selectedProfileTitle)
        val selectedProfileName = optionsView.findViewById<TextView>(R.id.selectedProfileName)
        val powerIcon = optionsView.findViewById<ImageButton>(R.id.powerIcon)
        val powerText = optionsView.findViewById<TextView>(R.id.powerCaption)
        val colorIcon = optionsView.findViewById<ImageButton>(R.id.colorIcon)
        val colorText = optionsView.findViewById<TextView>(R.id.colorCaption)
        val colorTitle = optionsView.findViewById<TextView>(R.id.colorTitle)
        val apply = optionsView.findViewById<View>(R.id.apply)
        val edit = optionsView.findViewById<View>(R.id.edit)
        val delete = optionsView.findViewById<View>(R.id.delete)
        val applyIcon = optionsView.findViewById<View>(R.id.applyIcon)
        val editIcon = optionsView.findViewById<View>(R.id.editIcon)
        val deleteIcon = optionsView.findViewById<View>(R.id.deleteIcon)

        selectedProfileTitle.text = if (profile?.name!!.isNotEmpty()) "${profile.name[0]}" else ""

        val background = ThemeUtils.getNLStatusIconBackground(context, profile.isSettingEnabled, getProfileIntensity(profile))
        val foreground = ThemeUtils.getNLStatusIconForeground(context, profile.isSettingEnabled, getProfileIntensity(profile))

        selectedProfileTitle.setTextColor(foreground)
        setIconBackground(selectedProfileTitle, background)

        selectedProfileName.text = profile.name

        powerIcon.background?.setColorFilter(background, PorterDuff.Mode.SRC_ATOP)
        powerIcon.setColorFilter(foreground)
        powerText.setText(if (profile.isSettingEnabled) R.string.on else R.string.off)

        colorIcon.background?.setColorFilter(background, PorterDuff.Mode.SRC_ATOP)
        colorIcon.setColorFilter(foreground)
        if (profile.settingMode == Constants.NL_SETTING_MODE_TEMP) {
            colorText.text = "${profile.settings[0]}K"
            colorTitle.setText(R.string.color_temperature_title)
        } else {
            colorText.text = "(${profile.settings[0]}, ${profile.settings[1]}, ${profile.settings[2]})"
            colorTitle.setText(R.string.manual_mode_title)
        }

        val applyClickListener = View.OnClickListener {
            if (curProfile != null) {
                curProfile!!.apply(this@ProfilesActivity)
                PreferenceHelper.putInt(context, Constants.PREF_CUR_APPLY_TYPE, Constants.APPLY_TYPE_PROFILE)
                PreferenceHelper.putBoolean(context, Constants.PREF_CUR_APPLY_EN, curProfile!!.isSettingEnabled)
                PreferenceHelper.putInt(context, Constants.PREF_CUR_PROF_MODE, curProfile!!.settingMode)
                PreferenceHelper.putString(context, Constants.PREF_CUR_PROF_VAL, curProfile!!.settings.contentToString())
            }
            optionsDialog.dismiss()
        }

        apply.setOnClickListener(applyClickListener)
        applyIcon.setOnClickListener(applyClickListener)

        val editClickListener = View.OnClickListener {
            val intent = Intent(this@ProfilesActivity, ProfileCreateActivity::class.java)

            intent.putExtra(Constants.PROFILE_DATA_PRESENT, true)
            intent.putExtra(Constants.PROFILE_MODE, Constants.MODE_EDIT)
            intent.putExtra(Constants.PROFILE_DATA_NAME, profile.name)
            intent.putExtra(Constants.PROFILE_DATA_SETTING_ENABLED, profile.isSettingEnabled)
            intent.putExtra(Constants.PROFILE_DATA_SETTING_MODE, profile.settingMode)
            intent.putExtra(Constants.PROFILE_DATA_SETTING, profile.settings)

            startActivityForResult(intent, _updateProfileCode)

            optionsDialog.dismiss()
        }

        edit.setOnClickListener(editClickListener)
        editIcon.setOnClickListener(editClickListener)

        val deleteClickListener = View.OnClickListener {
            showAlert(R.string.delete, getString(R.string.delete_details, curProfile!!.name), View.OnClickListener {
                profilesManager.deleteProfile(curProfile!!)
                val prof = curProfile
                profiles!!.remove(prof)
                curProfile = null
                profilesAdapter.notifyDataSetChanged()
            })
            optionsDialog.dismiss()
        }

        delete.setOnClickListener(deleteClickListener)
        deleteIcon.setOnClickListener(deleteClickListener)
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
