package com.corphish.nightlight.helpers

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * Created by Avinaba on 5/8/2017.
 * To open external links
 */

object ExternalLink {
    /**
     * Opens an external link (an intent with URL perhaps?) with appropriate app available to handle the intent.
     * @param context Context is required to start the activity which will handle the intent
     * @param intent Intent to be handled
     */
    private fun open(context: Context?, intent: Intent) {
        try {
            context!!.startActivity(intent)
        } catch (ignored: ActivityNotFoundException) {}

    }

    /**
     * Opens external URL in browser if present
     * @param context Context is needed to open the URL in browser
     * @param url Url to open in browser
     */
    fun open(context: Context?, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        open(context, intent)
    }
}
