package com.raizlabs.android.debugmodule;

import android.support.annotation.LayoutRes;
import android.view.View;

import java.io.Serializable;

/**
 * Description: The main interface for constructing a debug submenu. It enables the developer to add
 * any kind of debug menu they wish for this application.
 */
public interface Critter extends Serializable {

    /**
     * @return the layout to inflate in the menu when the user clicks on its option.
     */
    int getLayoutResId();

    /**
     * The layout for this critter has been created. Perform any logic you need
     *
     * @param layoutResource The top-level layout resource
     * @param view           The view to handle post-inflation
     */
    void handleView(@LayoutRes int layoutResource, View view);

    /**
     * Release any unused resources here.
     */
    void cleanup();
}
