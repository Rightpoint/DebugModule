package com.raizlabs.android.debugmodule;

import android.view.View;

/**
 * Author: andrewgrosner
 * Contributors: { }
 * Description:
 */
public interface Critter {

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

    /**
     * The name of component that will be displayed in the menu
     * @return
     */
    public String getName();
}
