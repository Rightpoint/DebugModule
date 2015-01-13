package com.raizlabs.android.debugmodule.view;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

/**
 * Description: Lets touches fall through that are not explicit drawer right swipes. This is so
 * we can have a {@link android.support.v4.widget.DrawerLayout} on top of the main content view of the root.
 */
public class NoContentDrawerLayout extends DrawerLayout {

    /**
     * The width of the screen, in pixels
     */
    int screenWidth;

    /**
     * The vertical touch width allowed for th {@link android.support.v4.widget.DrawerLayout}. It
     * shrinks and expands when the drawer slides. It shrinks so we can allow touches to the attached activity.
     */
    int drawerSide;

    /**
     * If true, we are allow all touches as normal to the {@link android.support.v4.widget.DrawerLayout}
     */
    boolean isDraggingDrawer = false;

    /**
     * If true, we allow all touches as normal to the {@link android.support.v4.widget.DrawerLayout}
     */
    boolean isDrawerOpen = false;

    /**
     * The minimum touch size in pixels.
     */
    int minimumTouchSize;

    public NoContentDrawerLayout(Context context) {
        super(context);
        init(context);
    }

    public NoContentDrawerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public NoContentDrawerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    /**
     * Called when this class is created. Sets up our defaults.
     *
     * @param context The context of the activity.åß
     */
    protected void init(Context context) {
        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        minimumTouchSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics());
        drawerSide = minimumTouchSize;
        setDrawerListener(mListener);
    }

    /**
     * Sets the minimum touch size of the debug drawer, in pixels.
     * @param touchSizePixels The vertical x touch size, in pixels that passes the touches to the drawer.
     */
    public void setMinimumTouchSize(int touchSizePixels) {
        minimumTouchSize = touchSizePixels;
        if (drawerSide < minimumTouchSize) {
            drawerSide = minimumTouchSize;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        float x = ev.getX();

        if (ev.getActionMasked() == MotionEvent.ACTION_UP) {
            isDraggingDrawer = false;
        }
        if (isDrawerOpen || isDraggingDrawer || screenWidth - x < drawerSide) {
            isDraggingDrawer = true;
            return super.onInterceptTouchEvent(ev);
        } else {
            return true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        float x = ev.getX();

        if (ev.getActionMasked() == MotionEvent.ACTION_UP) {
            isDraggingDrawer = false;
        }
        if (isDrawerOpen || isDraggingDrawer || screenWidth - x < drawerSide) {
            isDraggingDrawer = true;
            return super.onTouchEvent(ev);
        } else {
            return false;
        }
    }

    /**
     * Listens for slide events and shrinks and grows the touch target of this view as the user
     * drags the drawer. It is so we can adequately swipe open the drawer and enable touches to "fall through"
     * to the main content area at the same time.
     */
    private final DrawerListener mListener = new DrawerListener() {
        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
            drawerSide = (int) (((float) screenWidth) * slideOffset);

            // minimum touch size
            if (drawerSide < minimumTouchSize) {
                drawerSide = minimumTouchSize;
            }
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            isDrawerOpen = true;
        }

        @Override
        public void onDrawerClosed(View drawerView) {
            isDrawerOpen = false;
        }

        @Override
        public void onDrawerStateChanged(int newState) {

        }
    };
}
