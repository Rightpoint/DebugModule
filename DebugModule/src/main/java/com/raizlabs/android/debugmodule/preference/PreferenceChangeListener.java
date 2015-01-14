package com.raizlabs.android.debugmodule.preference;

/**
 * Description: Callback for when a tester changes a preference value. We can handle custom changes here.
 */
public interface PreferenceChangeListener {

    /**
     * Called when a preference changes.
     *
     * @param preferenceKey   The key of the preference that has changed.
     * @param preferenceValue The value that it changed to.
     */
    public void onPreferenceChanged(String preferenceKey, Object preferenceValue);
}
