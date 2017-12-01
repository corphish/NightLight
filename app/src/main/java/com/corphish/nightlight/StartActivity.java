package com.corphish.nightlight;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;

import com.corphish.nightlight.Data.Constants;
import com.corphish.nightlight.Helpers.PreferenceHelper;
import com.corphish.nightlight.Helpers.RootUtils;

import java.io.File;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (!BuildConfig.DEBUG) {
            if (!PreferenceHelper.getCompatibilityStatusTest(this))
                new CompatibilityChecker().execute();
        } else switchToMain();
    }

    private void showAlertDialog(int caption, int msg) {
        if (isFinishing()) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AppTheme));
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
        View progressBar, alertImage;

        @Override
        protected void onPreExecute() {
            progressBar = findViewById(R.id.progressBar);
            alertImage = findViewById(R.id.alertPlaceholder);
        }

        @Override
        protected String doInBackground(String... booms) {
            rootAccessAvailable = RootUtils.getRootAccess();
            kcalSupported = new File(Constants.KCAL_ADJUST).exists();
            return null;
        }

        @Override
        protected void onPostExecute(String boom) {
            progressBar.setVisibility(View.GONE);
            if (!rootAccessAvailable) {
                showAlertDialog(R.string.no_root_access, R.string.no_root_desc);
                alertImage.setVisibility(View.VISIBLE);
            }
            else if (!kcalSupported) {
                showAlertDialog(R.string.no_kcal, R.string.no_kcal_desc);
                alertImage.setVisibility(View.VISIBLE);
            }
            else {
                PreferenceHelper.putCompatibilityStatusTest(getApplicationContext(), true);
                switchToMain();
            }
        }
    }

    private void switchToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
