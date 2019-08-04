package com.corphish.nightlight.design.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.corphish.nightlight.R
import com.corphish.nightlight.helpers.ExternalLink

class GenericOptionsAdapter(val context: Context): RecyclerView.Adapter<GenericOptionsAdapter.OptionsViewHolder>() {
    lateinit var captionRes: List<Int>
    lateinit var imageRes: List<Int>
    lateinit var links: List<String>

    inner class OptionsViewHolder internal constructor(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
        internal var icon = v.findViewById<ImageButton>(R.id.settingOptionIcon)
        internal var caption = v.findViewById<TextView>(R.id.settingOptionCaption)

        init {
            v.setOnClickListener(this)
            icon.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            ExternalLink.open(context, links[adapterPosition])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionsViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.setting_option_item, parent, false)

        return OptionsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: OptionsViewHolder, position: Int) {
        holder.icon.setImageResource(imageRes[position])
        holder.caption.setText(captionRes[position])
    }

    override fun getItemCount() = links.size
}