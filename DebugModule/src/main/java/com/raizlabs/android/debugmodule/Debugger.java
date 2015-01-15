package com.raizlabs.android.debugmodule;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import com.raizlabs.android.debugmodule.view.NoContentDrawerLayout;

import java.util.HashMap;

/**
 * Description: The main attacher to the {@link android.support.v4.app.FragmentActivity}.
 * Call {@link #attach(android.support.v4.app.FragmentActivity)} in your
 * {@link android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)} method.
 */
public class Debugger {

    private static Debugger debugger;

    public static Debugger getInstance() {
        if (debugger == null) {
            debugger = new Debugger();
        }

        return debugger;
    }

    /**
     * Map of critters defined by key (the name it displays)
     */
    private HashMap<String, Critter> mCritters = new HashMap<>();

    /**
     * The drawer currently attached to the Activity
     */
    private NoContentDrawerLayout mDebugDrawer;

    /**
     * Sets the vertical x size that touches will come to the {@link com.raizlabs.android.debugmodule.view.NoContentDrawerLayout}
     */
    private int mMinimumTouchSize;

    /**
     * The gravity to set the debug drawer to be placed at.
     */
    private int mDrawerGravity = Gravity.RIGHT;

    /**
     * Attaches itself to the activity as an overlay. Call this in {@link android.app.Activity#onResume()}. Make sure to attach
     * {@link com.raizlabs.android.debugmodule.Critter} before calling this method. The overlay is a right sided {@link android.support.v4.widget.DrawerLayout}
     *
     * @param activity The activity to attach to
     */
    public void attach(FragmentActivity activity) {
        // only attach if debug build
        FrameLayout root = (FrameLayout) activity.findViewById(android.R.id.content);
        ViewGroup contentView = (ViewGroup) LayoutInflater.from(activity)
                .inflate(R.layout.view_debug_module_debugger, root, true);
        for (int i = 0; i < contentView.getChildCount(); i++) {
            View child = contentView.getChildAt(i);
            if (child instanceof NoContentDrawerLayout
                    && child.getId() == R.id.view_debug_module_menu_drawer_layout) {
                mDebugDrawer = (NoContentDrawerLayout) child;
                break;
            }
        }
        if (activity.getWindow().hasFeature(Window.FEATURE_ACTION_BAR_OVERLAY)) {
            TypedValue tv = new TypedValue();
            int actionBarHeight = 0;
            if (activity.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, activity.getResources().getDisplayMetrics());
            }
            setDrawerGravity(mDrawerGravity);
            mDebugDrawer.setPadding(mDebugDrawer.getPaddingLeft(), actionBarHeight,
                    mDebugDrawer.getPaddingRight(), mDebugDrawer.getPaddingBottom());

            View menuDrawer = activity.findViewById(R.id.view_debug_module_menu_drawer);
            menuDrawer.setPadding(menuDrawer.getPaddingLeft(), actionBarHeight,
                    menuDrawer.getPaddingRight(), menuDrawer.getPaddingBottom());
        }
        // Add the debug menu
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        Fragment fragment = new DebugMenuFragment();
        transaction.replace(R.id.view_debug_module_menu_drawer, fragment).commit();


    }

    /**
     * Sets what the gravity of the {@link com.raizlabs.android.debugmodule.view.NoContentDrawerLayout}
     * should be.
     * @param gravityInt The {@link android.view.Gravity} int
     */
    public void setDrawerGravity(int gravityInt) {
        mDrawerGravity = gravityInt;

        if(mDebugDrawer != null) {
            View menuDrawer = mDebugDrawer.findViewById(R.id.view_debug_module_menu_drawer);
            NoContentDrawerLayout.LayoutParams params = (NoContentDrawerLayout.LayoutParams) menuDrawer.getLayoutParams();
            if(params == null) {
                params = new NoContentDrawerLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            }
            params.gravity = gravityInt;
            menuDrawer.setLayoutParams(params);
        }
    }

    /**
     * Sets the minimum touch size vertically in x pixels.
     * @param minimumTouchSize The size, in pixels that we want the minimum amount of space that we
     *                         want the touch to be recognized for the drawer.
     */
    public void setMinimumTouchSize(int minimumTouchSize) {
        mMinimumTouchSize = minimumTouchSize;

        if(mDebugDrawer != null && minimumTouchSize !=0) {
            mDebugDrawer.setMinimumTouchSize(mMinimumTouchSize);
        }
    }

    /**
     * @param activity The activity that we've attached to
     * @return True if the debugger consumed the event such when the drawer is open
     * and there are no more fragments on backstack.
     */
    public boolean onBackPressed(FragmentActivity activity) {
        if (activity.getSupportFragmentManager().getBackStackEntryCount() == 0 && mDebugDrawer != null
                && mDebugDrawer.isDrawerOpen(Gravity.RIGHT)) {
            mDebugDrawer.closeDrawer(Gravity.RIGHT);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Attaches an array of {@link com.raizlabs.android.debugmodule.Critter} to use when we are in debug mode
     *
     * @param critterName The name of the critter
     * @param critter     A critter to attach
     */
    public Debugger use(String critterName, Critter critter) {
        mCritters.put(critterName, critter);
        return this;
    }

    /**
     * @param critterName The name of the critter
     * @return The critter given by the name. It can return null if the {@link com.raizlabs.android.debugmodule.Critter}
     * wasn't register.
     */
    public Critter getCritter(String critterName) {
        return mCritters.get(critterName);
    }

    /**
     * @param critterClazz
     * @param critterName
     * @param <CritterClass>
     * @return A precasted critter
     */
    public <CritterClass extends Critter> CritterClass getCritter(Class<CritterClass> critterClazz, String critterName) {
        return (CritterClass) getCritter(critterName);
    }

    /**
     * Internal usage only
     *
     * @return The map that backs these critters
     */
    HashMap<String, Critter> getCritterMap() {
        return mCritters;
    }
}
