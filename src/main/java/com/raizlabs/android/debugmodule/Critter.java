package com.raizlabs.android.debugmodule;

import android.view.View;

import java.io.Serializable;

/**
 * Author: andrewgrosner
 * Contributors: { }
 * Description: The main interface for constructing a debug submenu. It enables the developer to add
 * any kind of debug menu they wish for this application.
 */
public interface Critter extends Serializable {

    /**
     * Return the layout to inflate in the menu when the user clicks on its option.
     * @return
     */
    public int getLayoutResId();

    /**
     * The layout for this critter has been created. Perform any logic you need
     * @param view
     */
    public void handleView(View view);

}
