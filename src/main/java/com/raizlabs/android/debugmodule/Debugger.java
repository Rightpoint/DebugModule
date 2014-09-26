package com.raizlabs.android.debugmodule;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;

/**
 * Author: andrewgrosner
 * Contributors: { }
 * Description:
 */
public class Debugger {

    private static Debugger debugger;

    public static Debugger getInstance() {
        if(debugger == null) {
            debugger = new Debugger();
        }

        return debugger;
    }

    private FragmentActivity mCurrentActivity;

    private ArrayList<Critter> mCritters = new ArrayList<Critter>();

    /**
     * Attaches itself to the activity as an overlay. Call this in {@link android.app.Activity#onResume()}. Make sure to attach
     * {@link com.raizlabs.android.debugmodule.Critter} before calling this method.
     * @param activity
     */
    public void attach(FragmentActivity activity) {
        FrameLayout root = (FrameLayout) activity.findViewById(android.R.id.content);
        View topLevelView = LayoutInflater.from(activity).inflate(R.layout.view_debugger, root, false);
        root.addView(topLevelView);

        // Add the debug menu
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        Fragment fragment = new DebugMenuFragment();
        transaction.replace(R.id.MenuView, fragment).commit();

    }

    /**
     * Attaches a {@link com.raizlabs.android.debugmodule.Critter} to use when we are in debug mode
     * @param critter
     */
    public void use(Critter critter) {
        if(!mCritters.contains(critter)) {
            mCritters.add(critter);
        }
    }

    /**
     * Releases the hold on this activity. Call this in {@link android.app.Activity#onPause()}
     */
    public void detach() {
        mCurrentActivity = null;
    }

    public ArrayList<Critter> getCritters() {
        return mCritters;
    }
}
