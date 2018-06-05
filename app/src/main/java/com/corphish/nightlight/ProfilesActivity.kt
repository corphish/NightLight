package com.corphish.nightlight

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.appcompat.widget.AppCompatSpinner
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView

import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.design.alert.BottomSheetAlertDialog
import com.corphish.nightlight.engine.ProfilesManager
import com.corphish.nightlight.helpers.PreferenceHelper

import java.util.Arrays

class ProfilesActivity : AppCompatActivity(), ProfilesManager.DataChangeListener {

    private var creatorView: View? = null
    private var editText: AppCompatEditText? = null
    private var editTextError: TextView? = null
    private var settingTitle1: TextView? = null
    private var settingTitle2: TextView? = null
    private var cancel: TextView? = null
    private var profileAction: TextView? = null
    private var profileActionDesc: TextView? = null
    private var modes: AppCompatSpinner? = null
    private var settingParam1: AppCompatSeekBar? = null
    private var settingParam2: AppCompatSeekBar? = null
    private var ok: AppCompatButton? = null
    private var nlSwitch: SwitchCompat? = null
    private var bottomSheetDialog: com.google.android.material.bottomsheet.BottomSheetDialog? = null
    private var optionsDialog: com.google.android.material.bottomsheet.BottomSheetDialog? = null
    private var optionsView: View? = null

    private var currentModeSelection = Constants.NL_SETTING_MODE_FILTER
    private var curProfile: ProfilesManager.Profile? = null

    private val MODE_CREATE = 0
    private val MODE_EDIT = 1
    private var curMode = MODE_CREATE

    private var profilesManager: ProfilesManager? = null

    private var profilesAdapter: ProfilesAdapter? = null

    private var profiles: MutableList<ProfilesManager.Profile>? = null

    private var emptyView: View? = null

    private var context: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profiles)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        context = this

        findViewById<View>(R.id.fab).setOnClickListener {
            curProfile = null
            curMode = MODE_CREATE
            bottomSheetDialog = com.google.android.material.bottomsheet.BottomSheetDialog(this@ProfilesActivity, R.style.BottomSheetDialogDark)
            initProfileCreatorViews()
            bottomSheetDialog!!.setContentView(creatorView)
            bottomSheetDialog!!.setCancelable(false)
            bottomSheetDialog!!.show()
        }

        emptyView = findViewById(R.id.emptyView)

        initProfilesManager()
        initViews()
    }

    private fun initProfilesManager() {
        profilesManager = ProfilesManager(this)
        profilesManager!!.registerDataChangeListener(this)
        profilesManager!!.loadProfiles()
        profiles = profilesManager!!.profilesList
    }

    private fun initViews() {
        val recyclerView = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.profiles_holder)
        profilesAdapter = ProfilesAdapter()
        profilesAdapter!!.setProfiles(profiles)

        recyclerView.invalidateItemDecorations()
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        recyclerView.adapter = profilesAdapter
        recyclerView.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.setHasFixedSize(false)

        profilesAdapter!!.notifyDataSetChanged()
    }

    override fun onDataChanged(newDataSize: Int) {
        if (newDataSize < 1)
            emptyView!!.visibility = View.VISIBLE
        else
            emptyView!!.visibility = View.GONE
    }

    private inner class ProfilesAdapter : androidx.recyclerview.widget.RecyclerView.Adapter<ProfilesActivity.ProfilesAdapter.CustomViewHolder>() {
        private var profiles: List<ProfilesManager.Profile>? = null

        internal fun setProfiles(profiles: MutableList<ProfilesManager.Profile>?) {
            this.profiles = profiles
        }

        inner class CustomViewHolder internal constructor(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v), View.OnClickListener {
            internal val name: TextView
            internal val desc: TextView

            init {

                name = v.findViewById(R.id.profile_name)
                desc = v.findViewById(R.id.profile_desc)

                v.setOnClickListener(this)
            }

            override fun onClick(v: View) {
                if (!intent.getBooleanExtra(Constants.TASKER_ERROR_STATUS, true)) {
                    showAlert(R.string.confirm, getString(R.string.tasker_confirm_selection, profiles!![adapterPosition].name), View.OnClickListener { returnBack(profiles!![adapterPosition].name) })
                } else {
                    curProfile = profiles!![adapterPosition]
                    optionsDialog = com.google.android.material.bottomsheet.BottomSheetDialog(this@ProfilesActivity, R.style.BottomSheetDialogDark)
                    getOptionsView(curProfile!!.name)
                    optionsDialog!!.setContentView(optionsView)
                    optionsDialog!!.show()
                }
            }
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.card_profile_item, parent, false)
            return CustomViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
            holder.name.text = profiles!![position].name
            holder.desc.text = getDescription(profiles!![position])
        }

        override fun getItemCount(): Int {
            return profiles!!.size
        }

        private fun getDescription(profile: ProfilesManager.Profile): String {
            var desc = ""
            desc += getString(R.string.app_name) + " : " +
                    (if (profile.isSettingEnabled) getString(R.string.on).toLowerCase() else getString(R.string.off).toLowerCase()) + ", "
            desc += if (profile.settingMode == Constants.NL_SETTING_MODE_TEMP) getString(R.string.color_temperature_title) + " : " + profile.settings[0] + "K"
            else getString(R.string.blue_light) + " : " + profile.settings[0] + ", " +
                        getString(R.string.green_light) + " : " + profile.settings[1]
            return desc
        }
    }

    private fun initProfileCreatorViews() {
        creatorView = View.inflate(this, R.layout.bottom_sheet_create_profile, null)

        profileAction = creatorView!!.findViewById(R.id.profile_action_title)
        profileActionDesc = creatorView!!.findViewById(R.id.profile_action_desc)
        editText = creatorView!!.findViewById(R.id.profile_name_set)
        editTextError = creatorView!!.findViewById(R.id.profile_name_error)
        nlSwitch = creatorView!!.findViewById(R.id.profile_night_light_switch)
        settingTitle1 = creatorView!!.findViewById(R.id.profile_night_light_setting_title1)
        settingTitle2 = creatorView!!.findViewById(R.id.profile_night_light_setting_title2)
        cancel = creatorView!!.findViewById(R.id.button_cancel)
        modes = creatorView!!.findViewById(R.id.profile_night_light_setting_mode)
        settingParam1 = creatorView!!.findViewById(R.id.profile_night_light_setting_param1)
        settingParam2 = creatorView!!.findViewById(R.id.profile_night_light_setting_param2)
        ok = creatorView!!.findViewById(R.id.button_ok)

        modes!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                currentModeSelection = if (curProfile == null) position else curProfile!!.settingMode
                updateProfileCreatorParams(currentModeSelection, curProfile)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        cancel!!.setOnClickListener { bottomSheetDialog!!.dismiss() }

        ok!!.setOnClickListener {
            val retVal = if (curMode == MODE_CREATE) profilesManager!!.createProfile(nlSwitch!!.isChecked,
                        editText!!.editableText.toString(),
                        modes!!.selectedItemPosition,
                        if (modes!!.selectedItemId == Constants.NL_SETTING_MODE_TEMP.toLong()) intArrayOf(settingParam1!!.progress + 3000) else intArrayOf(settingParam1!!.progress, settingParam2!!.progress))
            else profilesManager!!.updateProfile(curProfile!!.name,
                        nlSwitch!!.isChecked,
                        editText!!.editableText.toString(),
                        modes!!.selectedItemPosition,
                        if (modes!!.selectedItemId == Constants.NL_SETTING_MODE_TEMP.toLong()) intArrayOf(settingParam1!!.progress + 3000) else intArrayOf(settingParam1!!.progress, settingParam2!!.progress))
            if (retVal) {
                profiles = profilesManager!!.profilesList
                profilesAdapter!!.notifyDataSetChanged()
                bottomSheetDialog!!.dismiss()
            } else
                editTextError!!.visibility = View.VISIBLE
        }

        if (curProfile != null) {
            nlSwitch!!.isChecked = curProfile!!.isSettingEnabled
            editText!!.setText(curProfile!!.name)
            modes!!.setSelection(curProfile!!.settingMode)
        }

        profileAction!!.setText(
                if (curMode == MODE_CREATE) R.string.profile_create_title else R.string.profile_edit_title
        )

        profileActionDesc!!.setText(
                if (curMode == MODE_CREATE) R.string.profile_create_desc else R.string.profile_edit_desc
        )

        editTextError!!.visibility = View.GONE
    }

    private fun updateProfileCreatorParams(mode: Int, profile: ProfilesManager.Profile?) {
        if (mode == Constants.NL_SETTING_MODE_FILTER) {
            settingParam1!!.isEnabled = true
            settingParam1!!.max = 128
            settingParam2!!.isEnabled = true
            settingParam2!!.max = 48
            settingTitle1!!.isEnabled = true
            settingTitle2!!.isEnabled = true
            settingTitle1!!.setText(R.string.blue_light)
            settingTitle2!!.setText(R.string.green_light)
            if (profile != null) {
                settingParam1!!.progress = profile.settings[0]
                settingParam2!!.progress = profile.settings[1]
            } else {
                settingParam1!!.progress = PreferenceHelper.getInt(this, Constants.PREF_BLUE_INTENSITY, Constants.DEFAULT_BLUE_INTENSITY)
                settingParam2!!.progress = PreferenceHelper.getInt(this, Constants.PREF_GREEN_INTENSITY, Constants.DEFAULT_GREEN_INTENSITY)
            }
        } else {
            settingParam1!!.isEnabled = true
            settingParam1!!.max = 1500
            settingParam2!!.isEnabled = false
            settingTitle1!!.isEnabled = true
            settingTitle2!!.isEnabled = false
            settingTitle1!!.setText(R.string.color_temperature_title)
            settingTitle2!!.setText(R.string.profile_nl_setting_unavailable)
            if (profile != null) {
                settingParam1!!.progress = profile.settings[0] - 3000
            } else {
                settingParam1!!.progress = PreferenceHelper.getInt(this, Constants.PREF_COLOR_TEMP, Constants.DEFAULT_COLOR_TEMP)
            }
        }
    }

    private fun getOptionsView(profileName: String?) {
        optionsView = View.inflate(this, R.layout.bottom_sheet_profile_options, null)

        (optionsView!!.findViewById<View>(R.id.selected_profile_name) as TextView).text = profileName

        optionsView!!.findViewById<View>(R.id.apply).setOnClickListener {
            if (curProfile != null) {
                curProfile!!.apply(this@ProfilesActivity)
                PreferenceHelper.putInt(context, Constants.PREF_CUR_APPLY_TYPE, Constants.APPLY_TYPE_PROFILE)
                PreferenceHelper.putBoolean(context, Constants.PREF_CUR_APPLY_EN, curProfile!!.isSettingEnabled)
                PreferenceHelper.putInt(context, Constants.PREF_CUR_PROF_MODE, curProfile!!.settingMode)
                PreferenceHelper.putString(context, Constants.PREF_CUR_PROF_VAL, Arrays.toString(curProfile!!.settings))
            }
            optionsDialog!!.dismiss()
        }

        optionsView!!.findViewById<View>(R.id.edit).setOnClickListener {
            curMode = MODE_EDIT
            bottomSheetDialog = com.google.android.material.bottomsheet.BottomSheetDialog(this@ProfilesActivity, R.style.BottomSheetDialogDark)
            initProfileCreatorViews()
            bottomSheetDialog!!.setContentView(creatorView)
            bottomSheetDialog!!.setCancelable(false)
            bottomSheetDialog!!.show()
            optionsDialog!!.dismiss()
        }

        optionsView!!.findViewById<View>(R.id.delete).setOnClickListener {
            showAlert(R.string.delete, getString(R.string.delete_details, curProfile!!.name), View.OnClickListener {
                profilesManager!!.deleteProfile(curProfile!!.name)
                val prof = curProfile
                profiles!!.remove(prof)
                curProfile = null
                profilesAdapter!!.notifyDataSetChanged()
            })
            optionsDialog!!.dismiss()
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
