package com.corphish.nightlight.design.widgets;

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

    /**
     * Sets caption of this KV view
     * @param text Caption text
     */
    public void setCaption(String text) {
        caption.setText(text);
    }

    /**
     * Sets value of this KV View
     * @param text Value text
     */
    public void setValue(String text) {
        value.setText(text);
    }

    /**
     * Gets the value of this KV View as String
     * @return Value as String
     */
    public String getValue() {
        return value.getText().toString();
    }

    /**
     * Enables or disables this view accordingly
     * @param enabled A boolean indicating whether to enable/disable views
     */
    public void setEnabled(boolean enabled) {
        caption.setEnabled(enabled);
        value.setEnabled(enabled);
        linearLayout.setEnabled(enabled);
        super.setEnabled(enabled);
    }
}
