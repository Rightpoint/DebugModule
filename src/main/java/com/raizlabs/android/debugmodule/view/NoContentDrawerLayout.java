package com.raizlabs.android.debugmodule.view;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

/**
 * Description:
 */
public class NoContentDrawerLayout extends DrawerLayout {

    int screenWidth;

    int drawerSide;

    boolean isDraggingDrawer = false;

    boolean isDrawerOpen = true;

    int defaultSideSize;

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

    protected void init(Context context) {
        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        defaultSideSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics());
        drawerSide = defaultSideSize;
        setDrawerListener(mListener);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        float x = ev.getX();

        if(ev.getActionMasked() == MotionEvent.ACTION_UP) {
            isDraggingDrawer = false;
        }
        if(isDrawerOpen || isDraggingDrawer || screenWidth-x < drawerSide) {
            isDraggingDrawer = true;
            return super.onInterceptTouchEvent(ev);
        } else {
            return true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        float x = ev.getX();

        if(ev.getActionMasked() == MotionEvent.ACTION_UP) {
            isDraggingDrawer = false;
        }
        if(isDrawerOpen || isDraggingDrawer || screenWidth-x < drawerSide) {
            isDraggingDrawer = true;
            return super.onTouchEvent(ev);
        } else {
            return false;
        }
    }

    private final DrawerListener mListener = new DrawerListener() {
        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
            drawerSide = (int) (((float)screenWidth)*slideOffset);
            if(drawerSide < defaultSideSize) {
                drawerSide = defaultSideSize;
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
