package com.corphish.nightlight.design.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.corphish.nightlight.BuildConfig
import com.corphish.nightlight.R
import com.corphish.nightlight.design.fragments.base.BaseBottomSheetDialogFragment
import com.corphish.nightlight.helpers.ExternalLink
import kotlinx.android.synthetic.main.layout_appreciation.*

class AppreciationFragment: BaseBottomSheetDialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.layout_appreciation, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val optionsAdapter = OptionsAdapter()

        var captionList = listOf(
                R.string.rate,
                R.string.translate,
                R.string.paypal
        )

        var imageList = listOf(
                R.drawable.ic_star_24dp,
                R.drawable.ic_translate_24dp,
                R.drawable.ic_money_24dp
        )

        var links = listOf(
                "market://details?id=com.corphish.nightlight." + (if (BuildConfig.FLAVOR.equals("donate")) "donate" else "generic"),
                "https://github.com/corphish/NightLight/blob/master/notes/translate.md",
                "https://www.paypal.me/corphish"
        )

        if (!BuildConfig.FLAVOR.equals("donate")) {
            captionList += R.string.get_donate
            imageList += R.drawable.ic_store_24dp
            links += "market://details?id=com.corphish.nightlight.donate"
        }

        optionsAdapter.captionRes = captionList
        optionsAdapter.imageRes = imageList
        optionsAdapter.links = links

        recyclerView.invalidateItemDecorations()
        recyclerView.layoutManager = GridLayoutManager(context, 3)
        recyclerView.adapter = optionsAdapter
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.setHasFixedSize(false)

        optionsAdapter.notifyDataSetChanged()
    }

    private inner class OptionsAdapter: RecyclerView.Adapter<OptionsAdapter.OptionsViewHolder>() {
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
}