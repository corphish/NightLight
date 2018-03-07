package com.corphish.nightlight.design.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.corphish.nightlight.helpers.ExternalLink;
import com.corphish.nightlight.R;

/**
 * Created by Avinaba on 10/24/2017.
 * Donate fragment
 */

public class DonateFragment extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.card_donation_info, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDonateActions();
            }
        });
    }

    private void showDonateActions() {
        final BottomSheetDialog optionsDialog = new BottomSheetDialog(getContext(),R.style.BottomSheetDialogDark);
        View optionsView = View.inflate(getContext(), R.layout.bottom_sheet_donate_actions, null);

        optionsView.findViewById(R.id.donate_action1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExternalLink.open(getContext(), "market://details?id=com.corphish.nightlight.donate");
                optionsDialog.dismiss();
            }
        });

        optionsView.findViewById(R.id.donate_action2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExternalLink.open(getContext(), "https://www.paypal.me/corphish");
                optionsDialog.dismiss();
            }
        });

        optionsDialog.setContentView(optionsView);
        optionsDialog.show();
    }
}
