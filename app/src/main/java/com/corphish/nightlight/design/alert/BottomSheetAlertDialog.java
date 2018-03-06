package com.corphish.nightlight.design.alert;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.TextView;

import com.corphish.nightlight.R;

/**
 * Created by avinabadalal on 06/03/18.
 * Bottom sheet alert dialog
 */

public class BottomSheetAlertDialog {

    private final Context context;
    private View contentView;

    private TextView title, content, negativeButton;
    private AppCompatButton positiveButton, neutralButton;

    private BottomSheetDialog bottomSheetDialog;

    public BottomSheetAlertDialog(Context context) {
        this.context = context;

        init();
    }

    private void init() {
        contentView = View.inflate(context, R.layout.bottom_sheet_msg, null);

        title = contentView.findViewById(R.id.title);
        content = contentView.findViewById(R.id.content);
        negativeButton = contentView.findViewById(R.id.negativeButton);
        positiveButton = contentView.findViewById(R.id.positiveButton);
        neutralButton = contentView.findViewById(R.id.neutralButton);

        bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogDark);
    }

    public BottomSheetAlertDialog setTitle(@StringRes int iTitle) {
        title.setText(iTitle);

        return this;
    }

    public BottomSheetAlertDialog setTitle(String sTitle) {
        title.setText(sTitle);

        return this;
    }

    public BottomSheetAlertDialog setMessage(@StringRes int iTitle) {
        content.setText(iTitle);

        return this;
    }

    public BottomSheetAlertDialog setMessage(String sTitle) {
        content.setText(sTitle);

        return this;
    }

    public BottomSheetAlertDialog setPositiveButton(@StringRes int iTitle, final View.OnClickListener onClickListener) {
        positiveButton.setVisibility(View.VISIBLE);
        positiveButton.setText(iTitle);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onClick(view);
                bottomSheetDialog.dismiss();
            }
        });

        return this;
    }

    public BottomSheetAlertDialog setNegativeButton(@StringRes int iTitle, final View.OnClickListener onClickListener) {
        negativeButton.setVisibility(View.VISIBLE);
        negativeButton.setText(iTitle);
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onClick(view);
                bottomSheetDialog.dismiss();
            }
        });

        return this;
    }

    public BottomSheetAlertDialog setNeutralButton(@StringRes int iTitle, final View.OnClickListener onClickListener) {
        neutralButton.setVisibility(View.VISIBLE);
        neutralButton.setText(iTitle);
        neutralButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onClick(view);
                bottomSheetDialog.dismiss();
            }
        });

        return this;
    }

    public BottomSheetAlertDialog setCancelable(boolean b) {
        bottomSheetDialog.setCancelable(b);

        return this;
    }

    public boolean show() {
        bottomSheetDialog.setContentView(contentView);
        bottomSheetDialog.show();

        return true;
    }
}