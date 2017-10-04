package com.corphish.nightlight;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;

import com.corphish.nightlight.Helpers.RootUtils;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    Switch masterSwitch;
    SeekBar slider;

    /**
     * Formula for calculating effective intensity
     * = 256 - <seekbar/defaultValue>
     */
    int defaultIntensity = 64, currentIntensity = defaultIntensity;
    int presetIntensities[] = {32,64,96};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init() {
        viewInit();
        new CompatibilityChecker().execute();
    }

    private void viewInit() {
        masterSwitch = findViewById(R.id.master_switch);
        slider = findViewById(R.id.intensity);

        boolean enabled = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Constants.PREF_MASTER_SWITCH, false);
        masterSwitch.setChecked(enabled);
        masterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                toggleSwitch(b);
            }
        });

        slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                currentIntensity = seekBar.getProgress();
                new Switcher(true, false).execute();
                PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit()
                        .putInt(Constants.PREF_CUSTOM_VAL, currentIntensity)
                        .apply();
            }
        });

        slider.setProgress(PreferenceManager.getDefaultSharedPreferences(this).getInt(Constants.PREF_CUSTOM_VAL, defaultIntensity));

        enableOrDisableViews(enabled);
    }

    private void enableOrDisableViews(boolean enabled) {
        slider.setEnabled(enabled);
    }

    private void toggleSwitch(boolean enabled) {
        new Switcher(enabled).execute();
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putBoolean(Constants.PREF_MASTER_SWITCH, enabled)
                .apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAlertDialog(int caption, int msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(caption);
        builder.setMessage(msg);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.show();
    }

    private class CompatibilityChecker extends AsyncTask<String, String, String> {
        boolean rootAccessAvailable = false, kcalSupported = false;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage(getString(R.string.compat_check));
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... booms) {
            rootAccessAvailable = RootUtils.getRootAccess();
            kcalSupported = new File(Constants.KCAL_ADJUST).exists();
            return null;
        }

        @Override
        protected void onPostExecute(String boom) {
            progressDialog.hide();
            if (!rootAccessAvailable) showAlertDialog(R.string.no_root_access, R.string.no_root_desc);
            else if (!kcalSupported) showAlertDialog(R.string.no_kcal, R.string.no_kcal_desc);
        }
    }

    private class Switcher extends AsyncTask<String, String, String> {
        boolean enabled, toModifyViews;
        Switcher(boolean b) {
            enabled = b;
            toModifyViews = true;
        }

        Switcher(boolean e, boolean m) {
            enabled = e;
            toModifyViews = m;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... booms) {
            Core.applyNightMode(enabled, currentIntensity);
            return null;
        }

        @Override
        protected void onPostExecute(String boom) {
            if (toModifyViews) enableOrDisableViews(enabled);
        }
    }
}
