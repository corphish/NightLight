package com.corphish.nightlight.design.preference

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageButton
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.corphish.nightlight.R

class SectionPreference @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet,
        defStyleAttr: Int = 0
) : Preference(context, attrs, defStyleAttr) {

    init {
        layoutResource = R.layout.layout_section_preference
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        with(holder.itemView) {
            // do the view initialization here...
            val iconButton = findViewById<ImageButton>(android.R.id.icon)
            iconButton.setImageDrawable(getIcon())
        }
    }
}