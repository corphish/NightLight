package com.corphish.nightlight.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import com.corphish.nightlight.R
import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.databinding.ActivityAutomationRoutineBinding
import com.corphish.nightlight.design.ThemeUtils
import com.corphish.nightlight.engine.AutomationRoutineManager
import com.corphish.nightlight.engine.models.AutomationRoutine
import com.corphish.nightlight.engine.models.AutomationRoutine.Companion.resolved
import com.corphish.nightlight.helpers.ExternalLink
import com.corphish.widgets.ktx.adapters.MutableListAdaptable
import com.corphish.widgets.ktx.adapters.MutableListAdapter
import com.corphish.widgets.ktx.dialogs.MessageAlertDialog
import com.corphish.widgets.ktx.viewholders.ClickableViewHolder

class AutomationRoutineActivity : AppCompatActivity() {

    // View binding.
    private lateinit var binding: ActivityAutomationRoutineBinding

    // Mutable adapter.
    private lateinit var adapter: MutableListAdapter<AutomationRoutine, ClickableViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Apply theme based on user selections
        setTheme(ThemeUtils.getAppTheme(this))

        binding = ActivityAutomationRoutineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Load routines.
        AutomationRoutineManager.loadRoutines(this)

        // Setup the recyclerview
        adapter = object : MutableListAdaptable<AutomationRoutine, ClickableViewHolder>() {
            override fun bind(viewHolder: ClickableViewHolder, item: AutomationRoutine, position: Int) {
                viewHolder.getViewById<TextView>(R.id.routineTitle)?.text = item.name

                // Set time
                viewHolder.getViewById<TextView>(R.id.routineTime)?.text =
                        if (item.endTime == AutomationRoutine.TIME_UNSET) {
                            item.startTime.resolved(this@AutomationRoutineActivity)
                        } else {
                            "${item.startTime.resolved(this@AutomationRoutineActivity)} → ${item.endTime.resolved(this@AutomationRoutineActivity)}"
                        }

                // Set color data
                viewHolder.getViewById<TextView>(R.id.routineColor)?.text =
                        if (item.rgbFrom[0] == -1 && item.rgbTo[0] == -1) {
                            getString(R.string.not_applicable_title)
                        } else {
                            val fromString = if (item.fadeBehavior.settingType == Constants.NL_SETTING_MODE_TEMP) {
                                "${item.rgbFrom[0]}K"
                            } else {
                                "RGB(${item.rgbFrom[0]}, ${item.rgbFrom[1]}, ${item.rgbFrom[2]})"
                            }
                            if (item.rgbTo[0] == -1) {
                                fromString
                            } else {
                                val toString = if (item.fadeBehavior.settingType == Constants.NL_SETTING_MODE_TEMP) {
                                    "${item.rgbTo[0]}K"
                                } else {
                                    "RGB(${item.rgbTo[0]}, ${item.rgbTo[1]}, ${item.rgbTo[2]})"
                                }

                                "$fromString → $toString"
                            }
                        }
            }

            override fun getDiffUtilItemCallback() = object : DiffUtil.ItemCallback<AutomationRoutine>() {
                override fun areItemsTheSame(oldItem: AutomationRoutine, newItem: AutomationRoutine) = false

                override fun areContentsTheSame(oldItem: AutomationRoutine, newItem: AutomationRoutine) =
                        oldItem == newItem
            }

            override fun getLayoutResource(viewType: Int) = R.layout.layout_auto_routine_item

            override fun getViewHolder(view: View, viewType: Int) = ClickableViewHolder(
                    view,
                    listOf(R.id.routineTitle, R.id.routineTime, R.id.routineColor)
            ) { _, pos ->
                val intent = Intent(this@AutomationRoutineActivity, RoutineCreateActivity::class.java)
                intent.putExtra(Constants.ROUTINE_UPDATE_INDEX, pos)

                startActivityForResult(intent, 73)
            }

        }.buildAdapter()

        binding.included.recyclerView.adapter = adapter
        binding.included.recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        binding.fab.setOnClickListener { view ->
            startActivityForResult(Intent(this@AutomationRoutineActivity, RoutineCreateActivity::class.java), 73)
        }

        adapter.submitList(AutomationRoutineManager.automationRoutineList)
        if (AutomationRoutineManager.automationRoutineList.isNotEmpty()) {
            binding.included.placeHolder.visibility = View.GONE
        } else {
            binding.included.placeHolder.visibility = View.VISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 73 && resultCode == RESULT_OK) {
            adapter.updateList(AutomationRoutineManager.automationRoutineList)
            if (AutomationRoutineManager.automationRoutineList.isNotEmpty()) {
                binding.included.placeHolder.visibility = View.GONE
            } else {
                binding.included.placeHolder.visibility = View.VISIBLE
            }
        }
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
                ExternalLink.open(this, "https://github.com/corphish/NightLight/blob/master/notes/routines.md")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}