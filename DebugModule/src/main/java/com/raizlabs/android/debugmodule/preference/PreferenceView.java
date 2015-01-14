package com.raizlabs.android.debugmodule.preference;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.raizlabs.android.debugmodule.R;

/**
 * Description:
 */
public class PreferenceView extends LinearLayout {

    TextView title;

    EditText valueChooser;

    public PreferenceView(Context context) {
        super(context);
        init(context);
    }

    public PreferenceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PreferenceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PreferenceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_debug_module_preference, this, true);
        title = (TextView) findViewById(R.id.view_debug_module_preference_title);
        valueChooser = (EditText) findViewById(R.id.view_debug_module_preference_value);
    }

    void populate(PreferenceBuilder preference) {
        title.setText(preference.getPrefKey());
        valueChooser.setText(String.valueOf(preference.getPreference()));
    }
}
