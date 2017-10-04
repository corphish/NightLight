package com.corphish.nightlight.Widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.corphish.nightlight.R;

/**
 * Created by Avinaba on 10/4/2017.
 * Custom KeyValueView
 */

public class KeyValueView extends LinearLayout {
    TextView caption, value;

    public KeyValueView(Context context) {
        this(context, null);
    }

    public KeyValueView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public KeyValueView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);

        LayoutInflater.from(context).inflate(R.layout.key_value_view, this);


    }
}
