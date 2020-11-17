package com.corphish.nightlight.design.preferences

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceViewHolder

class LongSummaryPreferenceCategory @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null
): PreferenceCategory(context, attrs) {

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        val summary = holder.findViewById(android.R.id.summary) as? TextView
        summary?.let {
            // Enable multiple line support
            summary.isSingleLine = false
            summary.maxLines = 10 // Just need to be high enough I guess
        }
    }
}