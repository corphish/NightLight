package com.corphish.nightlight.design.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.corphish.nightlight.BuildConfig
import com.corphish.nightlight.R
import com.corphish.nightlight.design.adapters.GenericOptionsAdapter
import com.corphish.nightlight.design.fragments.base.BaseBottomSheetDialogFragment
import kotlinx.android.synthetic.main.layout_generic_options.*

class AppreciationFragment: BaseBottomSheetDialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.layout_generic_options, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val optionsAdapter = GenericOptionsAdapter(context!!)
        val isGeneric = BuildConfig.FLAVOR == "generic"

        val captionList = listOf(
                R.string.rate,
                R.string.translate,
                R.string.get_donate
        )

        val imageList = listOf(
                R.drawable.ic_star,
                R.drawable.ic_translate,
                R.drawable.ic_money
        )

        val links = listOf(
                "market://details?id=com.corphish.nightlight." + (if (!isGeneric) "donate" else "generic"),
                "https://github.com/corphish/NightLight/blob/master/notes/translate.md",
                if (isGeneric)"market://details?id=com.corphish.nightlight.donate" else "https://paypal.me/corphish"
        )

        title.setText(R.string.show_support)

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
}