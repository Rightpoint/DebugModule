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

    private static final String ROOT_TAG = "MainFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(getSupportFragmentManager().findFragmentByTag(ROOT_TAG) == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content, new MainFragment())
                    .commit();
        }
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

}
