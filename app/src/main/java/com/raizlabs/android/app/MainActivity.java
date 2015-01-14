package com.raizlabs.android.app;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.raizlabs.android.debugmodule.Debugger;
import com.raizlabs.android.debugmodule.preference.PreferenceBuilder;
import com.raizlabs.android.debugmodule.preference.PreferenceChangeListener;
import com.raizlabs.android.debugmodule.preference.PreferenceCritter;


public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PreferenceCritter preferenceCritter = getPreferenceCritter();
        preferenceCritter
                .addPreference(new PreferenceBuilder<String>(this)
                        .prefKey("preference_test_name")
                        .prefType(String.class)
                        .titleName("String example"))
                .addPreference(new PreferenceBuilder<Boolean>(this)
                        .prefKey("preference_boolean")
                        .prefType(Boolean.class)
                        .titleName("Boolean example"))
                .addPreference(new PreferenceBuilder<Long>(this)
                        .prefKey("preference_long")
                        .prefType(Long.class)
                        .titleName("Long example"))
                .addPreference(new PreferenceBuilder<Float>(this)
                        .prefKey("preference_float")
                        .prefType(Float.class)
                        .titleName("Float example"))
                .addPreference(new PreferenceBuilder<Integer>(this)
                        .prefKey("preference_int")
                        .prefType(Integer.class)
                        .titleName("Integer example"));
        preferenceCritter.registerPreferenceChangeListener(mPreferenceChangeListener);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Debugger.getInstance().attach(this);
    }

    @Override
    public void onBackPressed() {
        if (!Debugger.getInstance().onBackPressed(this)) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getPreferenceCritter().unregisterPreferenceChangeListener(mPreferenceChangeListener);
    }

    protected PreferenceCritter getPreferenceCritter() {
        return (PreferenceCritter) Debugger.getInstance().getCritter("PreferenceCritter");
    }

    private final PreferenceChangeListener mPreferenceChangeListener = new PreferenceChangeListener() {
        @Override
        public void onPreferenceChanged(String preferenceKey, Object preferenceValue) {
            Toast.makeText(MainActivity.this, "Preference: " + preferenceKey
                    + " changed to: " + String.valueOf(preferenceValue), Toast.LENGTH_SHORT).show();
        }
    };
}
