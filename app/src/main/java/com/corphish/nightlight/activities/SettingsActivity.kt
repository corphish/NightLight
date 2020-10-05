package com.corphish.nightlight.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.corphish.nightlight.BuildConfig
import com.corphish.nightlight.R
import com.corphish.nightlight.activities.base.BaseActivity
import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.design.ThemeUtils
import com.corphish.nightlight.design.fragments.AutomationFragment
import com.corphish.nightlight.engine.KCALManager
import com.corphish.nightlight.engine.ProfilesManager
import com.corphish.nightlight.helpers.ExternalLink
import com.corphish.widgets.ktx.dialogs.MessageAlertDialog
import com.corphish.widgets.ktx.dialogs.SingleChoiceAlertDialog
import com.corphish.widgets.ktx.dialogs.properties.IconProperties

private const val TITLE_TAG = "settingsActivityTitle"

class SettingsActivity : BaseActivity(),
        PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(ThemeUtils.getAppTheme(this))
        setContentView(R.layout.activity_settings)

        // SimulationT
        val autoSim = intent?.getBooleanExtra(Constants.SIMULATE_AUTOMATION_SECTION, false) ?: false

        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(
                            R.id.settings,
                            when {
                                autoSim -> AutomationFragment()
                                else -> HeaderFragment()
                            }
                    )
                    .commit()
        } else {
            title = savedInstanceState.getCharSequence(TITLE_TAG)
            setActionBarTitle(title.toString())
        }

        useCollapsingActionBar()
        setActionBarTitle(R.string.title_activity_settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                setActionBarTitle(R.string.title_activity_settings)
            }
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.settings_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.action_about -> {
                startActivity(Intent(this, AboutActivity::class.java))
                true
            }
            R.id.action_faq -> {
                ExternalLink.open(this, "https://github.com/corphish/NightLight/blob/master/notes/usage.md")
                true
            }
            R.id.action_support -> {
                showSupport()
                true
            }
            R.id.action_rate -> {
                ExternalLink.open(
                        this,
                        "market://details?id=com.corphish.nightlight." + (if (BuildConfig.FLAVOR != "generic") "donate" else "generic")
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // Save current activity title so we can set it again after a configuration change
        outState.putCharSequence(TITLE_TAG, title)
    }

    override fun onSupportNavigateUp(): Boolean {
        if (supportFragmentManager.popBackStackImmediate()) {
            return true
        }

        return super.onSupportNavigateUp()
    }

    override fun onPreferenceStartFragment(
            caller: PreferenceFragmentCompat,
            pref: Preference
    ): Boolean {
        // Instantiate the new Fragment
        val args = pref.extras
        val fragment = supportFragmentManager.fragmentFactory.instantiate(
                classLoader,
                pref.fragment
        ).apply {
            arguments = args
            setTargetFragment(caller, 0)
        }


        // Replace the existing Fragment with the new Fragment
        supportFragmentManager.beginTransaction()
                .replace(R.id.settings, fragment)
                .addToBackStack(null)
                .commit()

        title = pref.title
        setActionBarTitle(pref.title.toString())

        return true
    }

    /**
     * This groups the settings available.
     */
    class HeaderFragment : PreferenceFragmentCompat() {
        // Profiles manager
        private lateinit var profilesManager: ProfilesManager

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.header_preferences, rootKey)

            // Update profile count
            updateProfileCount()

            // Show driver info
            findPreference<Preference>("kcal_driver")?.summary = KCALManager.implementation.getImplementationName()

            // Show appreciation fragment
            findPreference<Preference>("show_support")?.setOnPreferenceClickListener {


                true
            }

            // Pro version
            findPreference<Preference>("pro_version")?.setOnPreferenceClickListener {
                MessageAlertDialog(requireContext()).apply {
                    titleResId = R.string.pro_long_title
                    messageResId = R.string.pro_desc
                    positiveButtonProperties = MessageAlertDialog.ButtonProperties(
                            buttonTitleResId = android.R.string.ok,
                            dismissDialogOnButtonClick = true,
                            buttonAction = {
                                ExternalLink.open(requireContext(), "market://details?id=com.corphish.nightlight.donate")
                            }
                    )
                    negativeButtonProperties = MessageAlertDialog.ButtonProperties(
                            buttonTitleResId = android.R.string.cancel,
                            dismissDialogOnButtonClick = true
                    )
                }.show()

                true
            }
        }

        /**
         * Updates profile summary with its count.
         */
        private fun updateProfileCount() {
            // One time init
            if (!this::profilesManager.isInitialized) {
                profilesManager = ProfilesManager(requireContext())
            }

            // Populate count
            profilesManager.loadProfiles()
            val count = profilesManager.profilesList.size

            findPreference<Preference>("profiles")?.summary =
                    requireContext().resources.getQuantityString(R.plurals.profile_count, count, count)
        }

        override fun onResume() {
            super.onResume()

            updateProfileCount()
        }
    }

    private fun showSupport() {
        val background = ContextCompat.getDrawable(this, ThemeUtils.getThemeIconShape(this))

        SingleChoiceAlertDialog(this).apply {
            titleResId = R.string.show_support
            messageResId = R.string.support_desc
            dismissOnChoiceSelection = false
            animationResourceLayout = R.raw.appreciate
            iconProperties = IconProperties(
                    iconColor = if (ThemeUtils.isLightTheme(this@SettingsActivity)) Color.WHITE else Color.BLACK,
                    backgroundDrawable = background
            )
            choiceList = listOf(
                    SingleChoiceAlertDialog.ChoiceItem(
                            titleResId = R.string.rate,
                            iconResId = R.drawable.ic_star,
                            action = {
                                ExternalLink.open(
                                        this@SettingsActivity,
                                        "market://details?id=com.corphish.nightlight." + (if (BuildConfig.FLAVOR != "generic") "donate" else "generic")
                                )
                            }),
                    SingleChoiceAlertDialog.ChoiceItem(
                            titleResId = R.string.translate,
                            iconResId = R.drawable.ic_translate,
                            action = {
                                ExternalLink.open(
                                        this@SettingsActivity,
                                        "https://github.com/corphish/NightLight/blob/master/notes/translate.md"
                                )
                            }),
                    SingleChoiceAlertDialog.ChoiceItem(
                            titleResId = R.string.get_donate,
                            iconResId = R.drawable.ic_money,
                            action = {
                                ExternalLink.open(
                                        this@SettingsActivity,
                                        "https://paypal.me/corphish"
                                )
                            })
            )
        }.show()
    }

    override fun onResume() {
        super.onResume()

        // Supply the resume callback to all child fragments
        for (f in supportFragmentManager.fragments) {
            f.onResume()
        }
    }
}