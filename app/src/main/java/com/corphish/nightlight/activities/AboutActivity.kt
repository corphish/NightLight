package com.corphish.nightlight.activities

import android.graphics.Color
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.corphish.nightlight.BuildConfig
import com.corphish.nightlight.R
import com.corphish.nightlight.activities.base.BaseActivity
import com.corphish.nightlight.databinding.ActivityAboutBinding
import com.corphish.nightlight.design.ThemeUtils

import com.corphish.nightlight.helpers.ExternalLink
import com.corphish.widgets.ktx.dialogs.SingleChoiceAlertDialog
import com.corphish.widgets.ktx.dialogs.properties.IconProperties

/**
 * This activity holds the AboutFragment and the LinksFragment.
 * These show the information and credits section of the app.
 *
 * Animation credits:
 * alarm - https://lottiefiles.com/7988-alarm-clock
 * appreciation - https://lottiefiles.com/23029-submission-thumbs-up
 * day_night - https://lottiefiles.com/175-day-night-cycle
 * time - https://lottiefiles.com/30782-time-icon
 */
class AboutActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Apply theme based on user selections
        setTheme(ThemeUtils.getAppTheme(this))

        // ViewBinding
        val binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        useCollapsingActionBar()
        setActionBarTitle(R.string.about)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Avoid showing the fragment more than one time
        val versionText = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
        binding.included.version.text = versionText
        binding.included.hash.text = BuildConfig.GitHash

        // Handle the FAB click
        binding.fab.setOnClickListener {
            showLinks()
        }
    }

    /**
     * Shows links associated with the app.
     */
    private fun showLinks() {
        val background = ContextCompat.getDrawable(this, ThemeUtils.getThemeIconShape(this))
        SingleChoiceAlertDialog(this).apply {
            titleResId = R.string.links
            iconProperties = IconProperties(
                    iconColor = if (ThemeUtils.isLightTheme(context)) Color.WHITE else Color.BLACK,
                    backgroundDrawable = background
            )
            choiceList = listOf(
                    SingleChoiceAlertDialog.ChoiceItem(
                            titleResId = R.string.contributors,
                            iconResId = R.drawable.ic_link,
                            action = { ExternalLink.open(context, "https://github.com/corphish/NightLight/graphs/contributors") }
                    ),
                    SingleChoiceAlertDialog.ChoiceItem(
                            titleResId = R.string.github,
                            iconResId = R.drawable.ic_link,
                            action = { ExternalLink.open(context, "https://github.com/corphish/NightLight") }
                    ),
                    SingleChoiceAlertDialog.ChoiceItem(
                            titleResId = R.string.xda,
                            iconResId = R.drawable.ic_link,
                            action = { ExternalLink.open(context, "https://forum.xda-developers.com/android/apps-games/app-night-light-kcal-t3689090") }
                    ),
                    SingleChoiceAlertDialog.ChoiceItem(
                            titleResId = R.string.animations,
                            iconResId = R.drawable.ic_link,
                            action = { ExternalLink.open(context, "https://github.com/corphish/NightLight/blob/master/notes/animations.md") }
                    ),
            )
        }.show()
    }
}
