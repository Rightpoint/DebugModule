package com.raizlabs.android.app;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.raizlabs.android.app.dbflow.ExampleModel;
import com.raizlabs.android.app.dbflow.LargeExampleModel;
import com.raizlabs.android.debugmodule.Debugger;
import com.raizlabs.android.debugmodule.preference.PreferenceBuilder;
import com.raizlabs.android.debugmodule.preference.PreferenceChangeListener;
import com.raizlabs.android.debugmodule.preference.PreferenceCritter;

import java.util.Random;


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
        findViewById(R.id.openDrawer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Debugger.getInstance().getDebugDrawer().openDrawer(Gravity.RIGHT);
            }
        });
        findViewById(R.id.openFragment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Debugger.getInstance().attachDebugFragment(MainActivity.this, R.id.content);
            }
        });

        ExampleModel model = new ExampleModel();
        model.name = "Test";
        model.duble = 0.5d;
        model.floatie = 0.75f;
        model.isSet = new Random(System.currentTimeMillis()).nextBoolean();
        model.save(false);

        LargeExampleModel largeExampleModel = new LargeExampleModel();
        largeExampleModel.name = "Test";
        largeExampleModel.duble = 0.5d;
        largeExampleModel.floatie = 0.75f;
        largeExampleModel.isSet = new Random(System.currentTimeMillis()).nextBoolean();
        byte[] buffer = new byte[new Random(System.currentTimeMillis()).nextInt(20)];
        new Random(System.currentTimeMillis()).nextBytes(buffer);
        largeExampleModel.anotherName = new String(buffer);
        largeExampleModel.anotherName2 = "wwowowowo";
        largeExampleModel.save(false);
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
            Toast.makeText(MainActivity.this, preferenceKey
                    + " changed to: " + String.valueOf(preferenceValue), Toast.LENGTH_SHORT).show();
        }
    };
}
