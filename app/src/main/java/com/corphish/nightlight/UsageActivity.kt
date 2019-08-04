package com.corphish.nightlight

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.corphish.nightlight.design.ThemeUtils
import kotlinx.android.synthetic.main.content_usage.*
import kotlinx.android.synthetic.main.layout_header.*

class UsageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(ThemeUtils.getAppTheme(this))
        setContentView(R.layout.activity_usage)

        banner_title.text = getString(R.string.faq)
        banner_icon.setImageResource(R.drawable.ic_help)
        init()
    }

    private fun init() {
        val usageAdapter = UsageAdapter()
        usageAdapter.setQuestionsAndAnswers(
                listOf(
                        R.string.root_question to R.string.root_answer,
                        R.string.master_switch_function_question to R.string.master_switch_function_answer,
                        R.string.master_switch_exist_question to R.string.master_switch_exist_answer,
                        R.string.kcal_backup_question to R.string.kcal_backup_answer,
                        R.string.sob_question to R.string.sob_answer,
                        R.string.filter_modes_question to R.string.filter_modes_answer,
                        R.string.intensity_type_question to R.string.intensity_type_answer,
                        R.string.location_question to R.string.location_answer,
                        R.string.dark_hours_question to R.string.dark_hours_answers,
                        R.string.dark_hours_idea_question to R.string.dark_hours_idea_answer,
                        R.string.external_switch_question to R.string.external_switch_answer,
                        R.string.external_switch_fn_question to R.string.external_switch_fn_answer,
                        R.string.donate_question to R.string.donate_answer
                )
        )

        recyclerView.adapter = usageAdapter
        recyclerView.invalidateItemDecorations()
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        recyclerView.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.setHasFixedSize(false)

        usageAdapter.notifyDataSetChanged()
    }

    private inner class UsageAdapter : RecyclerView.Adapter<UsageAdapter.CustomViewHolder>() {
        private lateinit var questionAnswers: List<Pair<Int, Int>>

        internal fun setQuestionsAndAnswers(questionAnswers: List<Pair<Int, Int>>) {
            this.questionAnswers = questionAnswers
        }

        inner class CustomViewHolder internal constructor(v: View) : RecyclerView.ViewHolder(v) {
            internal val question: TextView = v.findViewById(R.id.question)
            internal val answer: TextView = v.findViewById(R.id.answer  )
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.usage_item, parent, false)

            return CustomViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
            holder.question.setText(questionAnswers[position].first)
            holder.answer.setText(questionAnswers[position].second)
        }

        override fun getItemCount(): Int {
            return questionAnswers.size
        }
    }
}