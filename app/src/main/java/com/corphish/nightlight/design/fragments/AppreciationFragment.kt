package com.corphish.nightlight.design.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.corphish.nightlight.BuildConfig
import com.corphish.nightlight.R
import com.corphish.nightlight.design.fragments.base.BaseBottomSheetDialogFragment
import com.corphish.nightlight.design.models.LinkItem
import com.corphish.nightlight.helpers.ExternalLink
import com.corphish.widgets.ktx.adapters.Adapters
import com.corphish.widgets.ktx.viewholders.ClickableViewHolder
import kotlinx.android.synthetic.main.layout_generic_options.*

class AppreciationFragment: BaseBottomSheetDialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.layout_generic_options, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val isGeneric = BuildConfig.FLAVOR == "generic"

        val items = listOf(
                LinkItem(R.string.rate, R.drawable.ic_star, "market://details?id=com.corphish.nightlight." + (if (!isGeneric) "donate" else "generic")),
                LinkItem(R.string.translate, R.drawable.ic_translate, "https://github.com/corphish/NightLight/blob/master/notes/translate.md"),
                LinkItem(R.string.get_donate, R.drawable.ic_money, "https://paypal.me/corphish")
        )

        title.setText(R.string.show_support)

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