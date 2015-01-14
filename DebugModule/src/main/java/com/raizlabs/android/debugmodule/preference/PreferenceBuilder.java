package com.raizlabs.android.debugmodule.preference;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

/**
 * Description: Builds a manipulated preference object. This handles how it changes.
 */
public class PreferenceBuilder<PreferenceClass> {

    private SharedPreferences mPrefs;

    private String mPrefKey;

    private Class<PreferenceClass> mPrefType;

    private PreferenceClass mDefaultValue;

    public PreferenceBuilder(Context context, String preferenceKey, int mode) {
        mPrefs = context.getSharedPreferences(preferenceKey, mode);
    }

    public PreferenceBuilder<PreferenceClass> prefKey(String prefKey) {
        mPrefKey = prefKey;
        return this;
    }

    public PreferenceBuilder<PreferenceClass> prefType(Class<PreferenceClass> prefType) {
        mPrefType = prefType;
        return this;
    }

    public PreferenceBuilder<PreferenceClass> defValue(PreferenceClass defaultValue) {
        mDefaultValue = defaultValue;
        return this;
    }

    /**
     * @return The value from the stored preferences object based on the pref type.
     */
    @SuppressWarnings("unchecked")
    public PreferenceClass getPreference() {
        Object preference = null;
        if (mPrefType.equals(Boolean.class)) {
            preference = mPrefs.getBoolean(mPrefKey, (Boolean) mDefaultValue);
        } else if (mPrefType.equals(Integer.class)) {
            preference = mPrefs.getInt(mPrefKey, (Integer) mDefaultValue);
        } else if (mPrefType.equals(Set.class)) {
            preference = mPrefs.getStringSet(mPrefKey, (Set<String>) mDefaultValue);
        } else if (mPrefType.equals(Float.class)) {
            preference = mPrefs.getFloat(mPrefKey, (Float) mDefaultValue);
        } else if (mPrefType.equals(Long.class)) {
            preference = mPrefs.getLong(mPrefKey, (Long) mDefaultValue);
        }


        return (PreferenceClass) preference;
    }

    /**
     * Changes the preference
     * @param preferenceValue The value of the preference to apply
     */
    public void applyPreference(PreferenceClass preferenceValue, PreferenceChangeListener listener) {
        SharedPreferences.Editor editor = edit();
        if (mPrefType.equals(Boolean.class)) {
            editor.putBoolean(mPrefKey, (Boolean) preferenceValue);
        } else if (mPrefType.equals(Integer.class)) {
            editor.putInt(mPrefKey, (Integer) preferenceValue);
        } else if (mPrefType.equals(Set.class)) {
            editor.putStringSet(mPrefKey, (Set<String>) preferenceValue);
        } else if (mPrefType.equals(Float.class)) {
            editor.putFloat(mPrefKey, (Float) preferenceValue);
        } else if (mPrefType.equals(Long.class)) {
            editor.putLong(mPrefKey, (Long) preferenceValue);
        }
        editor.apply();

        listener.onPreferenceChanged(mPrefKey, preferenceValue);
    }

    private SharedPreferences.Editor edit() {
        return mPrefs.edit();
    }
}
