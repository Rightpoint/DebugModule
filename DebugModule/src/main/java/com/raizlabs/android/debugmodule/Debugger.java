package com.raizlabs.android.debugmodule;

import android.support.annotation.IdRes;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Description: The main attacher to the {@link android.support.v4.app.FragmentActivity}.
 * Call {@link #attach(android.support.v4.app.FragmentActivity)} in your
 * {@link android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)} method.
 */
public class Debugger {

    private static final String TAG_DEBUG_FRAGMENT = "DebugMenuFragment";

    /**
     * Called when the critter is removed from the {@link com.raizlabs.android.debugmodule.Debugger}
     */
    public interface CritterRemoveListener {

        /**
         * Critter was removed
         *
         * @param critter The critter that was removed.
         */
        void onCritterRemoved(Critter critter);
    }

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
    private HashMap<String, Critter> critters = new HashMap<>();

    /**
     * The drawer currently attached to the Activity
     */
    private NoContentDrawerLayout debugDrawer;

    /**
     * Sets the vertical x size that touches will come to the {@link com.raizlabs.android.debugmodule.view.NoContentDrawerLayout}
     */
    private int minimumTouchSize;

    /**
     * The gravity to set the debug drawer to be placed at.
     */
    private int drawerGravity = Gravity.RIGHT;

    private List<CritterRemoveListener> lListeners = new ArrayList<>();

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
                debugDrawer = (NoContentDrawerLayout) child;
                break;
            }
        }
        if (activity.getWindow().hasFeature(Window.FEATURE_ACTION_BAR_OVERLAY)) {
            TypedValue tv = new TypedValue();
            int actionBarHeight = 0;
            if (activity.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,
                                                                         activity.getResources().getDisplayMetrics());
            }
            setDrawerGravity(drawerGravity);
            debugDrawer.setPadding(debugDrawer.getPaddingLeft(), actionBarHeight,
                                    debugDrawer.getPaddingRight(), debugDrawer.getPaddingBottom());

            View menuDrawer = activity.findViewById(R.id.view_debug_module_menu_drawer);
            menuDrawer.setPadding(menuDrawer.getPaddingLeft(), actionBarHeight,
                                  menuDrawer.getPaddingRight(), menuDrawer.getPaddingBottom());
        }
        // Add the debug menu
        attachDebugFragment(activity, R.id.view_debug_module_menu_drawer);
    }

    /**
     * Attaches the {@link com.raizlabs.android.debugmodule.DebugMenuFragment} into the specified
     * activity.
     *
     * @param activity     The activity to attach to
     * @param debugFrame   The container id of the layout to put the fragment into.
     * @param useBackStack Add this fragment to the backstack.
     */
    public DebugMenuFragment attachDebugFragment(FragmentActivity activity, @IdRes int debugFrame,
                                                 boolean useBackStack) {
        DebugMenuFragment fragment = (DebugMenuFragment) activity.getSupportFragmentManager().findFragmentByTag(
                TAG_DEBUG_FRAGMENT);
        if (fragment == null) {
            fragment = DebugMenuFragment.newInstance(debugFrame);
            FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager()
                    .beginTransaction();
            if (useBackStack) {
                fragmentTransaction.addToBackStack(null);
            }
            fragmentTransaction.replace(debugFrame, fragment)
                    .commit();
        }
        return fragment;
    }

    /**
     * Attaches the {@link com.raizlabs.android.debugmodule.DebugMenuFragment} into the specified
     * activity.
     *
     * @param activity     The activity to attach to
     * @param debugFrame   The container id of the layout to put the fragment into.
     */
    public DebugMenuFragment attachDebugFragment(FragmentActivity activity, @IdRes int debugFrame) {
        return attachDebugFragment(activity, debugFrame, false);
    }

    /**
     * Sets what the gravity of the {@link com.raizlabs.android.debugmodule.view.NoContentDrawerLayout}
     * should be.
     *
     * @param gravityInt The {@link android.view.Gravity} int
     */
    public void setDrawerGravity(int gravityInt) {
        drawerGravity = gravityInt;

        if (debugDrawer != null) {
            View menuDrawer = debugDrawer.findViewById(R.id.view_debug_module_menu_drawer);
            NoContentDrawerLayout.LayoutParams params = (NoContentDrawerLayout.LayoutParams) menuDrawer.getLayoutParams();
            if (params == null) {
                params = new NoContentDrawerLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                ViewGroup.LayoutParams.MATCH_PARENT);
            }
            params.gravity = gravityInt;
            menuDrawer.setLayoutParams(params);
        }
    }

    /**
     * Sets the minimum touch size vertically in x pixels.
     *
     * @param minimumTouchSize The size, in pixels that we want the minimum amount of space that we
     *                         want the touch to be recognized for the drawer.
     */
    public void setMinimumTouchSize(int minimumTouchSize) {
        this.minimumTouchSize = minimumTouchSize;

        if (debugDrawer != null && minimumTouchSize != 0) {
            debugDrawer.setMinimumTouchSize(this.minimumTouchSize);
        }
    }

    /**
     * Listens for removal callbacks
     *
     * @param removeListener Called when critter removed
     */
    public void registerCritterRemoveListener(CritterRemoveListener removeListener) {
        if (!lListeners.contains(removeListener)) {
            lListeners.add(removeListener);
        }
    }

    /**
     * Cancels removal callbacks
     *
     * @param removeListener The callback to remove.
     */
    public void unregisterCritterRemoveListener(CritterRemoveListener removeListener) {
        lListeners.remove(removeListener);
    }

    /**
     * @param activity The activity that we've attached to
     * @return True if the debugger consumed the event such when the drawer is open
     * and there are no more fragments on backstack.
     */
    public boolean onBackPressed(FragmentActivity activity) {
        if (activity.getSupportFragmentManager().getBackStackEntryCount() == 0 && debugDrawer != null
            && debugDrawer.isDrawerOpen(Gravity.RIGHT)) {
            debugDrawer.closeDrawer(Gravity.RIGHT);
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
        critters.put(critterName, critter);
        return this;
    }

    /**
     * @param critterName The name of the critter
     * @return The critter given by the name. It can return null if the {@link com.raizlabs.android.debugmodule.Critter}
     * wasn't register.
     */
    public Critter getCritter(String critterName) {
        return critters.get(critterName);
    }

    /**
     * @param critterClazz   The class of the critter to cast to
     * @param critterName    The name of the critter
     * @param <CritterClass> The class that implements {@link com.raizlabs.android.debugmodule.Critter}
     * @return A pre-casted critter
     */
    @SuppressWarnings("unchecked")
    public <CritterClass extends Critter> CritterClass getCritter(Class<CritterClass> critterClazz,
                                                                  String critterName) {
        return (CritterClass) getCritter(critterName);
    }

    /**
     * @param critter The critter to find name of
     * @return The name for the critter specified
     */
    public String getCritterName(Critter critter) {
        String critterName = null;

        Set<String> keySet = critters.keySet();
        for (String key : keySet) {
            if (critters.get(key).equals(critter)) {
                critterName = key;
            }
        }
        return critterName;
    }

    /**
     * Removes and returns the {@link com.raizlabs.android.debugmodule.Critter} that was removed. Notifies {@link CritterRemoveListener}
     * that it has been removed.
     *
     * @param critterName The name of the critter
     * @return The removed critter
     */
    public Critter dispose(String critterName) {
        Critter critter = critters.remove(critterName);
        mInternalListener.onCritterRemoved(critter);
        return critter;
    }

    /**
     * Removes and returns the {@link Critter} that was removed.
     *
     * @param critterName The name of the critter
     * @return The removed critter.
     */
    public Critter disposeQuietly(String critterName) {
        return critters.remove(critterName);
    }

    /**
     * Removes all specified critters
     *
     * @param critterNames The names of critters to dispose of
     */
    public void disposeAll(String... critterNames) {
        for (String critterName : critterNames) {
            dispose(critterName);
        }
    }

    /**
     * Clears all active critters from the debugger. You will need to reattach the debugger after.
     */
    public void clear() {
        critters.clear();
    }

    public DrawerLayout getDebugDrawer() {
        return debugDrawer;
    }

    /**
     * Internal usage only
     *
     * @return The map that backs these critters
     */
    HashMap<String, Critter> getCritterMap() {
        return critters;
    }

    private final CritterRemoveListener mInternalListener = new CritterRemoveListener() {
        @Override
        public void onCritterRemoved(Critter critter) {
            for (CritterRemoveListener listener : lListeners) {
                listener.onCritterRemoved(critter);
            }
        }
    };
}
