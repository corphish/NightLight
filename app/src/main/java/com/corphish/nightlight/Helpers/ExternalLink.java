package com.corphish.nightlight.Helpers;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by Avinaba on 5/8/2017.
 * To open external links
 */

public class ExternalLink {
    private static void open(Context context, Intent intent) {
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException ignored) {}
    }

    public static void open(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        open(context, intent);
    }
}
