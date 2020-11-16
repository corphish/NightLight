package com.corphish.nightlight.activities

import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.corphish.nightlight.R
import com.corphish.nightlight.databinding.ActivityAutomationRoutineBinding

class AutomationRoutineActivity : AppCompatActivity() {

    // View binding.
    private lateinit var binding: ActivityAutomationRoutineBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAutomationRoutineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.fab.setOnClickListener { view -> }
    }
}