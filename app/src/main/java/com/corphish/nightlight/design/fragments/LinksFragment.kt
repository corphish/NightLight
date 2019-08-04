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

class LinksFragment: BaseBottomSheetDialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.layout_generic_options, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val optionsAdapter = GenericOptionsAdapter(context!!)

        val captionList = listOf(
                R.string.contributors,
                R.string.github,
                R.string.xda
        )

        val imageList = listOf(
                R.drawable.ic_link,
                R.drawable.ic_link,
                R.drawable.ic_link
        )

        val links = listOf(
                "https://github.com/corphish/NightLight/graphs/contributors",
                "https://github.com/corphish/NightLight/",
                "https://forum.xda-developers.com/android/apps-games/app-night-light-kcal-t3689090"
        )
        
        title.setText(R.string.links)

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