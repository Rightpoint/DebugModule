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
 * Description: The main attacher to the {@link android.support.v4.app.FragmentActivity}. Call {@link #attach(boolean, android.support.v4.app.FragmentActivity)}
 *  in your {@link android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)} method.
 */
public class Debugger {

    private static Debugger debugger;

    public static Debugger getInstance() {
        if(debugger == null) {
            debugger = new Debugger();
        }

        return debugger;
    }

    private ArrayList<Critter> mCritters = new ArrayList<Critter>();

    /**
     * Attaches itself to the activity as an overlay. Call this in {@link android.app.Activity#onResume()}. Make sure to attach
     * {@link com.raizlabs.android.debugmodule.Critter} before calling this method. The overlay is a right sided {@link android.support.v4.widget.DrawerLayout}
     * @param configDebug Pass in the {@link com.raizlabs.android.debugmodule.BuildConfig#DEBUG} flag to mark this as a debug build
     * @param activity The activity to attach to
     */
    public void attach(boolean configDebug, FragmentActivity activity) {
        // only attach if debug build
        if(configDebug) {
            FrameLayout root = (FrameLayout) activity.findViewById(android.R.id.content);
            View topLevelView = LayoutInflater.from(activity).inflate(R.layout.view_debugger, root, false);
            root.addView(topLevelView);

            // Add the debug menu
            FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
            Fragment fragment = new DebugMenuFragment();
            transaction.replace(R.id.MenuView, fragment).commit();
        }
    }

    /**
     * Attaches an array of {@link com.raizlabs.android.debugmodule.Critter} to use when we are in debug mode
     * @param critters Array of critters that we attach to the menu to use
     */
    public void use(Critter...critters) {
        for(Critter critter: critters) {
            if (!mCritters.contains(critter)) {
                mCritters.add(critter);
            }
        }
    }

    public ArrayList<Critter> getCritters() {
        return mCritters;
    }
}
