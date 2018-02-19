package com.corphish.nightlight;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.corphish.nightlight.design.fragments.AdvancedAutomationFragment;

/**
 * Activity to configure advanced automation
 * Advanced automation will be based on decreasing color temperature gradually
 * (Planned) Features
 * 1. Ability to set maximum and minimum temperatures - When advanced automation will be enabled, it will start off with maximum temperature,
 * and then temperature will gradually decrease to minimum temperature (suitable for night viewing).
 * 2. Auto-calculation of scaling factor - To determine how much temperature should be decreased/increased periodically.
 * 3. Ability to set 3 different time periods ->
 * 3.1. Scale down period - In this period, temperature will gradually go down.
 * 3.2. Night period - In this period, temperature remains minimum. (During sleep hours).
 * 3.3. Scale up period - Temperature rises until this period ends.
 * Note : The time period bound must be <strong>within and inclusive</strong> of automatic time schedule.
 * 4. Scaling interval - Interval at which changes are applied (30 mins or 1 hours, we don't want to create an AlarmManager fiasco).
 */

public class AdvancedAutomationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_automation);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.layout_container, new AdvancedAutomationFragment())
                    .commit();
        }
    }
}
