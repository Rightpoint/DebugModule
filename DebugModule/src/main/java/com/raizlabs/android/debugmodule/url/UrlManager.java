package com.raizlabs.android.debugmodule.url;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.List;

/**
 * Description:
 */
public class UrlManager {

    static final String PREFERENCES_NAME = "UrlPreferenceManager";

    static final String PREF_CURRENT_URL = "debugger_pref_current_url";

    private final SharedPreferences mPreferences;

    public UrlManager(Context context) {
        mPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public String getCurrentUrl(String baseUrl) {
        return mPreferences.getString(PREF_CURRENT_URL, baseUrl);
    }

    public void setCurrentUrl(String currentUrl) {
        mPreferences.edit().putString(PREF_CURRENT_URL, currentUrl).commit();
    }

    public List<String> getUrls(Context context) {
        return UrlFileUtils.getUrls(context);
    }

    public void saveUrls(Context context, List<String> urls) {
        UrlFileUtils.saveUrls(context, urls);
    }

    public void clear(Context context) {
        UrlFileUtils.clearUrls(context);
    }
}
