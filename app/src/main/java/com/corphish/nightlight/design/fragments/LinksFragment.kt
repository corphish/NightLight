package com.corphish.nightlight.design.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.corphish.nightlight.R
import com.corphish.nightlight.design.fragments.base.BaseBottomSheetDialogFragment
import com.corphish.nightlight.design.models.LinkItem
import com.corphish.nightlight.helpers.ExternalLink
import com.corphish.widgets.ktx.adapters.Adapters
import com.corphish.widgets.ktx.viewholders.ClickableViewHolder
import kotlinx.android.synthetic.main.layout_generic_options.*

class LinksFragment: BaseBottomSheetDialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.layout_generic_options, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val items = listOf(
                LinkItem(R.string.contributors,  R.drawable.ic_link, "https://github.com/corphish/NightLight/graphs/contributors"),
                LinkItem(R.string.github, R.drawable.ic_link, "https://github.com/corphish/NightLight/"),
                LinkItem(R.string.xda,  R.drawable.ic_link, "https://forum.xda-developers.com/android/apps-games/app-night-light-kcal-t3689090")
        )
        
        title.setText(R.string.links)

        recyclerView.invalidateItemDecorations()
        recyclerView.layoutManager = GridLayoutManager(context, 3)
        recyclerView.adapter = Adapters.newStaticAdapter<LinkItem, ClickableViewHolder> {
            layoutResourceId = R.layout.setting_option_item
            listItems = items
            viewHolder = { v ->
                ClickableViewHolder(v, listOf(R.id.settingOptionIcon, R.id.settingOptionCaption)) {_, i ->
                    ExternalLink.open(requireContext(), items[i].link)
                }
            }
            binding = { clickableViewHolder, item ->
                clickableViewHolder.getViewById<ImageButton>(R.id.settingOptionIcon)?.setImageResource(item.imageRes)
                clickableViewHolder.getViewById<TextView>(R.id.settingOptionCaption)?.text = getString(item.captionRes)
            }
            notifyDataSetChanged = true
        }
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.setHasFixedSize(false)
    }

}