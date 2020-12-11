package com.corphish.nightlight.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.*
import com.corphish.nightlight.R
import com.corphish.nightlight.activities.base.BaseActivity

import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.databinding.ActivityProfilesBinding
import com.corphish.nightlight.databinding.BottomSheetProfileOptionsBinding
import com.corphish.nightlight.design.ThemeUtils
import com.corphish.nightlight.design.alert.BottomSheetAlertDialog
import com.corphish.nightlight.engine.ProfilesManager
import com.corphish.nightlight.helpers.PreferenceHelper
import com.corphish.widgets.ktx.adapters.MutableListAdaptable
import com.corphish.widgets.ktx.adapters.MutableListAdapter
import com.corphish.widgets.ktx.dialogs.OnBoardingDialog
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.*

class ProfilesActivity : BaseActivity(), ProfilesManager.DataChangeListener {

    private lateinit var optionsDialog: BottomSheetDialog
    private lateinit var optionsView: View

    private var curProfile: ProfilesManager.Profile? = null

    private lateinit var profilesManager: ProfilesManager

    // Profile adapter
    private lateinit var profileAdapter: MutableListAdapter<ProfilesManager.Profile, CustomViewHolder>

    private lateinit var context: Context

    private val _createProfileCode = 6
    private val _updateProfileCode = 9

    // View binding
    private lateinit var binding: ActivityProfilesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(ThemeUtils.getAppTheme(this))

        binding = ActivityProfilesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        context = this

        binding.fab.setOnClickListener {
            val intent = Intent(this@ProfilesActivity, ProfileCreateActivity::class.java)
            intent.putExtra(Constants.PROFILE_DATA_PRESENT, false)
            intent.putExtra(Constants.PROFILE_MODE, Constants.MODE_CREATE)

            startActivityForResult(intent, _createProfileCode)
        }

        initProfilesManager()
        initViews()
    }

    private fun initProfilesManager() {
        profilesManager = ProfilesManager(this)
        profilesManager.registerDataChangeListener(this)
        profilesManager.loadProfiles()
    }

    private fun initViews() {
        binding.included.recyclerView.layoutManager =  LinearLayoutManager(this)
        profileAdapter = object: MutableListAdaptable<ProfilesManager.Profile, CustomViewHolder>() {
            override fun bind(viewHolder: CustomViewHolder, item: ProfilesManager.Profile, position: Int) {
                viewHolder.icon.text = if (item.name.isNotEmpty()) "${item.name.toUpperCase(Locale.getDefault())[0]}" else ""
                setIconBackground(viewHolder.icon, ThemeUtils.getNLStatusIconBackground(context, item.isSettingEnabled))
                viewHolder.icon.setTextColor(ThemeUtils.getNLStatusIconForeground(context, item.isSettingEnabled))

                viewHolder.title.text = item.name
            }

            override fun getDiffUtilItemCallback() = object: DiffUtil.ItemCallback<ProfilesManager.Profile>() {
                override fun areItemsTheSame(oldItem: ProfilesManager.Profile, newItem: ProfilesManager.Profile) =
                        oldItem.name == newItem.name

                override fun areContentsTheSame(oldItem: ProfilesManager.Profile, newItem: ProfilesManager.Profile) =
                        oldItem == newItem
            }

            override fun getLayoutResource(viewType: Int) = R.layout.layout_profile_item
            override fun getViewHolder(view: View, viewType: Int) = CustomViewHolder(view)

        }.buildAdapter()

        binding.included.recyclerView.adapter = profileAdapter
        profileAdapter.submitList(profilesManager.profilesList)
    }

    override fun onDataChanged(newDataSize: Int) {
        if (newDataSize < 1) {
            binding.included.emptyView.visibility = View.VISIBLE
        } else {
            binding.included.emptyView.visibility = View.GONE
        }
    }

    private inner class CustomViewHolder constructor(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener, View.OnLongClickListener {
        val icon: TextView = v.findViewById(R.id.profileIcon)
        val title: TextView = v.findViewById(R.id.profileTitle)

        init {
            v.setOnClickListener(this)
            v.setOnLongClickListener(this)
        }

        override fun onClick(v: View) {
            if (!intent.getBooleanExtra(Constants.TASKER_ERROR_STATUS, true)) {
                showAlert(R.string.confirm, getString(R.string.tasker_confirm_selection, profilesManager.profilesList[adapterPosition].name)) { returnBack(profilesManager.profilesList[adapterPosition].name) }
            } else {
                showProfileOverviewDialog(adapterPosition)
            }
        }

        private fun showProfileOverviewDialog(pos: Int) {
            curProfile = profilesManager.profilesList[pos]
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

    private fun setIconBackground(textView: TextView, color: Int) {
        val drawable = ResourcesCompat.getDrawable(resources, ThemeUtils.getThemeIconShape(this), theme)
        drawable?.setColorFilter(color, PorterDuff.Mode.SRC)

        textView.background = drawable
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if ((requestCode == _createProfileCode || requestCode == _updateProfileCode) && resultCode == Activity.RESULT_OK) {
            profilesManager.loadProfiles()
            Log.d("NL_Profile", "New size = ${profilesManager.profilesList.size}")
            profileAdapter.updateList(profilesManager.profilesList)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getOptionsView(profile: ProfilesManager.Profile?) {
        optionsView = View.inflate(this, R.layout.bottom_sheet_profile_options, null)
        val profileBinding = BottomSheetProfileOptionsBinding.bind(optionsView)

        profileBinding.selectedProfileTitle.text = if (profile?.name!!.isNotEmpty()) "${profile.name[0]}" else ""

        val background = ThemeUtils.getNLStatusIconBackground(context, profile.isSettingEnabled)
        val foreground = ThemeUtils.getNLStatusIconForeground(context, profile.isSettingEnabled)

        profileBinding.selectedProfileTitle.setTextColor(foreground)
        setIconBackground(profileBinding.selectedProfileTitle, background)

        profileBinding.selectedProfileName.text = profile.name

        profileBinding.powerIcon.background?.setColorFilter(background, PorterDuff.Mode.SRC_ATOP)
        profileBinding.powerIcon.setColorFilter(foreground)
        profileBinding.powerCaption.setText(if (profile.isSettingEnabled) R.string.on else R.string.off)

        profileBinding.colorIcon.background?.setColorFilter(background, PorterDuff.Mode.SRC_ATOP)
        profileBinding.colorIcon.setColorFilter(foreground)
        if (profile.settingMode == Constants.NL_SETTING_MODE_TEMP) {
            profileBinding.colorCaption.text = "${profile.settings[0]}K"
            profileBinding.colorTitle.setText(R.string.color_temperature_title)
        } else {
            profileBinding.colorCaption.text = "(${profile.settings[0]}, ${profile.settings[1]}, ${profile.settings[2]})"
            profileBinding.colorTitle.setText(R.string.manual_mode_title)
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

        profileBinding.apply.setOnClickListener(applyClickListener)
        profileBinding.applyIcon.setOnClickListener(applyClickListener)

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

        profileBinding.edit.setOnClickListener(editClickListener)
        profileBinding.editIcon.setOnClickListener(editClickListener)

        val deleteClickListener = View.OnClickListener {
            showAlert(R.string.delete, getString(R.string.delete_details, curProfile!!.name)) {
                profilesManager.deleteProfile(curProfile!!)
                curProfile = null
                profileAdapter.updateList(profilesManager.profilesList)
            }
            optionsDialog.dismiss()
        }

        profileBinding.delete.setOnClickListener(deleteClickListener)
        profileBinding.deleteIcon.setOnClickListener(deleteClickListener)
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
        bottomSheetAlertDialog.setNegativeButton(android.R.string.cancel) { }
        bottomSheetAlertDialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.automation_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.action_help -> {
                OnBoardingDialog(this).apply {
                    slides = listOf(
                            OnBoardingDialog.Slide(
                                    titleResId = R.string.profile_title,
                                    messageResId = R.string.profiles_desc
                            )
                    )
                }.show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
