package com.corphish.nightlight.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import com.corphish.nightlight.R
import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.databinding.ActivityAutomationRoutineBinding
import com.corphish.nightlight.engine.models.AutomationRoutine
import com.corphish.nightlight.engine.models.AutomationRoutine.Companion.resolved
import com.corphish.widgets.ktx.adapters.MutableListAdaptable
import com.corphish.widgets.ktx.viewholders.ClickableViewHolder

class AutomationRoutineActivity : AppCompatActivity() {

    // View binding.
    private lateinit var binding: ActivityAutomationRoutineBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAutomationRoutineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        // Setup the recyclerview
        binding.included.recyclerView.adapter = object : MutableListAdaptable<AutomationRoutine, ClickableViewHolder>() {
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
                            if (item.rgbTo[0] == -1) {
                                "RGB(${item.rgbFrom[0]}, ${item.rgbFrom[1]}, ${item.rgbFrom[2]}"
                            } else {
                                "RGB(${item.rgbFrom[0]}, ${item.rgbFrom[1]}, ${item.rgbFrom[2]} → RGB(${item.rgbTo[0]}, ${item.rgbTo[1]}, ${item.rgbTo[2]}"
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

        binding.fab.setOnClickListener { view -> }
    }
}