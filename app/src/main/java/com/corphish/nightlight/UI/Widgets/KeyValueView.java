package com.corphish.nightlight.UI.Widgets;

import android.content.Context;
import android.content.res.TypedArray;
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
    LinearLayout linearLayout;

    public KeyValueView(Context context) {
        this(context, null);
    }

    public KeyValueView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public KeyValueView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);

        LayoutInflater.from(context).inflate(R.layout.key_value_view, this);

        caption = findViewById(R.id.caption);
        value = findViewById(R.id.value);
        linearLayout = findViewById(R.id.kv_root);

        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.KeyValueView,0,0);
        String captionText = typedArray.getString(R.styleable.KeyValueView_caption);
        String valueText = typedArray.getString(R.styleable.KeyValueView_value);

        caption.setText(captionText);
        value.setText(valueText);

        typedArray.recycle();
    }

    public void setCaption(String text) {
        caption.setText(text);
    }

    public void setValue(String text) {
        value.setText(text);
    }

    public String getValue() {
        return value.getText().toString();
    }

    public void setEnabled(boolean enabled) {
        caption.setEnabled(enabled);
        value.setEnabled(enabled);
        linearLayout.setEnabled(enabled);
        super.setEnabled(enabled);
    }
}
