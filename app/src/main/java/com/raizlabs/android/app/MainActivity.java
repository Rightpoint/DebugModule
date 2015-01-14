package com.raizlabs.android.app;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.raizlabs.android.debugmodule.Debugger;
import com.raizlabs.android.debugmodule.preference.PreferenceBuilder;
import com.raizlabs.android.debugmodule.preference.PreferenceChangeListener;
import com.raizlabs.android.debugmodule.preference.PreferenceCritter;


public class MainActivity extends FragmentActivity {

    static final String PREFERENCE_TEST_NAME = "preference_test_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PreferenceCritter preferenceCritter = getPreferenceCritter();
        preferenceCritter
                .addPreference(new PreferenceBuilder<String>(this).prefKey(PREFERENCE_TEST_NAME).prefType(String.class));
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
