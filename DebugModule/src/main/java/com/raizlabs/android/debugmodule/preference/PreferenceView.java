package com.raizlabs.android.debugmodule.preference;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.KeyEvent;
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

    PreferenceChangeListener mChangeListener;

    PreferenceBuilder mBuilder;

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
        // tapping enter changes value
        valueChooser.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER) {
                    if(mBuilder != null) {
                        mBuilder.applyPreference(mBuilder.toValue(valueChooser.getText().toString()), mChangeListener);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    void populate(PreferenceBuilder preference, PreferenceChangeListener changeListener) {
        mBuilder = preference;
        mChangeListener = changeListener;
        title.setText(preference.getTitle());
        valueChooser.setText(String.valueOf(preference.getPreference()));

        Class type = preference.getPrefType();
        if (type.equals(Boolean.class)) {
            valueChooser.setInputType(InputType.TYPE_CLASS_TEXT);
        } else if (type.equals(Integer.class)) {
            valueChooser.setInputType(InputType.TYPE_CLASS_NUMBER);
        } else if (type.equals(Float.class)) {
            valueChooser.setInputType(InputType.TYPE_CLASS_NUMBER);
        } else if (type.equals(Long.class)) {
            valueChooser.setInputType(InputType.TYPE_CLASS_NUMBER);
        } else if (type.equals(String.class)) {
            valueChooser.setInputType(InputType.TYPE_CLASS_TEXT);
        }
    }
}
