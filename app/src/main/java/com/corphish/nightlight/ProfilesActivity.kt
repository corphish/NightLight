package com.corphish.nightlight

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.design.alert.BottomSheetAlertDialog
import com.corphish.nightlight.design.views.ProfileCreator
import com.corphish.nightlight.engine.ProfilesManager
import com.corphish.nightlight.helpers.PreferenceHelper
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_profiles.*
import kotlinx.android.synthetic.main.content_profiles.*

import java.util.Arrays

class ProfilesActivity : AppCompatActivity(), ProfilesManager.DataChangeListener {

    private lateinit var optionsDialog: BottomSheetDialog
    private lateinit var optionsView: View

    private var curProfile: ProfilesManager.Profile? = null

    private lateinit var profilesManager: ProfilesManager

    private lateinit var profilesAdapter: ProfilesAdapter

    private var profiles: MutableList<ProfilesManager.Profile>? = null

    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        findViewById<TextView>(R.id.banner_title).text = getString(R.string.banner_app_name, BuildConfig.VERSION_NAME)

        initProfilesManager()
        initViews()
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
        recyclerView.layoutManager = LinearLayoutManager(this)
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
            internal val name: TextView = v.findViewById(R.id.profile_name)
            internal val desc: TextView = v.findViewById(R.id.profile_desc)

            init {
                v.setOnClickListener(this)
            }

            override fun onClick(v: View) {
                if (!intent.getBooleanExtra(Constants.TASKER_ERROR_STATUS, true)) {
                    showAlert(R.string.confirm, getString(R.string.tasker_confirm_selection, profiles!![adapterPosition].name), View.OnClickListener { returnBack(profiles!![adapterPosition].name) })
                } else {
                    curProfile = profiles!![adapterPosition]
                    optionsDialog = BottomSheetDialog(this@ProfilesActivity, R.style.BottomSheetDialogDark)
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
            else getString(R.string.red) + " : " + profile.settings[0] + ", " +
                    getString(R.string.green) + " : " + profile.settings[1] + ", " +
                    getString(R.string.blue) + " : " + profile.settings[2]
            return desc
        }
    }



    private fun getOptionsView(profile: ProfilesManager.Profile?) {
        optionsView = View.inflate(this, R.layout.bottom_sheet_profile_options, null)

        val selectedProfileName = optionsView.findViewById<TextView>(R.id.selectedProfileName)
        val apply = optionsView.findViewById<View>(R.id.apply)
        val edit = optionsView.findViewById<View>(R.id.edit)
        val delete = optionsView.findViewById<View>(R.id.delete)

        selectedProfileName.text = profile?.name

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
