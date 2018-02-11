package com.corphish.nightlight.helpers;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by Avinaba on 5/8/2017.
 * To open external links
 */

public class ExternalLink {
    /**
     * Opens an external link (an intent with URL perhaps?) with appropriate app available to handle the intent.
     * @param context Context is required to start the activity which will handle the intent
     * @param intent Intent to be handled
     */
    private static void open(Context context, Intent intent) {
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException ignored) {}
    }

    /**
     * Opens external URL in browser if present
     * @param context Context is needed to open the URL in browser
     * @param url Url to open in browser
     */
    public static void open(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        open(context, intent);
    }
}
