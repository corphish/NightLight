package com.corphish.nightlight;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;

import com.corphish.nightlight.Helpers.PreferenceHelper;
import com.corphish.nightlight.UI.Fragments.AboutFragment;
import com.corphish.nightlight.UI.Fragments.AutoFragment;
import com.corphish.nightlight.UI.Fragments.DonateFragment;
import com.corphish.nightlight.UI.Fragments.FilterFragment;
import com.corphish.nightlight.UI.Fragments.ForceSwitchFragment;
import com.corphish.nightlight.UI.Fragments.MasterSwitchFragment;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MasterSwitchFragment.MasterSwitchClickListener {

    private boolean masterSwitchEnabled;
    private final int containerId = R.id.layout_container;

    FragmentTransaction fragmentTransaction;

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
        viewInit();
        setViews(masterSwitchEnabled);
    }

    private void init() {
        masterSwitchEnabled = PreferenceHelper.getMasterSwitchStatus(this);
    }

    private void viewInit() {
        // Clear container
        LinearLayout container = findViewById(containerId);
        container.removeAllViews();

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        fragmentTransaction.add(containerId, new MasterSwitchFragment()).commit();
    }

    @Override
    public void onSwitchClicked(boolean status) {
        setViews(status);
    }

    private void setViews(boolean show) {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        if (show) {
            // Add all others conditionally
            if (isSupported(R.bool.filters_enabled)) fragmentTransaction.add(containerId, new FilterFragment());
            if (isSupported(R.bool.automation_enabled)) fragmentTransaction.add(containerId, new AutoFragment());
            if (isSupported(R.bool.force_switch_enabled)) fragmentTransaction.add(containerId, new ForceSwitchFragment());

            fragmentTransaction.add(containerId, new AboutFragment());

            if (isSupported(R.bool.donation_enabled)) fragmentTransaction.add(containerId, new DonateFragment());
        } else {
            List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
            for (Fragment fragment: fragmentList) {
                if (!(fragment instanceof MasterSwitchFragment)) fragmentTransaction.remove(fragment);
            }
        }

        fragmentTransaction.commit();
    }

    private boolean isSupported (int id) {
        return getResources().getBoolean(id);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment fragment: fragments) {
            if (fragment instanceof AutoFragment)
                fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
