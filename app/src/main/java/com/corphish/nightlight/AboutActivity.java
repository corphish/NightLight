package com.corphish.nightlight;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.corphish.nightlight.UI.Fragments.AboutFragment;
import com.corphish.nightlight.UI.Fragments.DonateFragment;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.layout_container, new AboutFragment())
                .add(R.id.layout_container, new DonateFragment())
                .commit();
    }
}
