package com.raizlabs.android.app;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.raizlabs.android.debugmodule.Debugger;

/**
 * Description:
 */
public class DebugMenuActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Debugger.getInstance().attachDebugFragment(this, R.id.content, false);
    }
}
