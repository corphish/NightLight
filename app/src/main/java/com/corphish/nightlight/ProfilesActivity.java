package com.corphish.nightlight;

import android.os.Bundle;
import android.app.Activity;

public class ProfilesActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiles);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
