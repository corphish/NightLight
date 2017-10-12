package com.corphish.nightlight;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Avinaba on 10/12/2017.
 * Base application class
 */

public class ShortcutActivity extends AppCompatActivity {

    private final String SHORTCUT_INTENT_STRING = "android.intent.action.TOGGLE";
    private final String SHORTCUT_ID            = "toggle";

    @Override
    public void onCreate(Bundle s) {
        super.onCreate(s);

        handleIntent();
    }

    private void handleIntent() {
        if (getIntent().getAction() == null || getIntent().getAction().isEmpty()) return;
    }
}