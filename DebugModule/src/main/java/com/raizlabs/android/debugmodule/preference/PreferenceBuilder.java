package com.raizlabs.android.debugmodule.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Set;

/**
 * Description: Builds a manipulated preference object. This handles how it changes.
 */
public class PreferenceBuilder<PreferenceClass> {

    private SharedPreferences mPrefs;

    private String mPrefKey;

    private Class<PreferenceClass> mPrefType;

    private PreferenceClass mDefaultValue;

    /**
     * Constructs a default object that uses the {@link android.preference.PreferenceManager#getDefaultSharedPreferences(android.content.Context)}
     *
     * @param context The context to retrieve preferences from
     */
    public PreferenceBuilder(Context context) {
        this(context, null, 0);
    }

    /**
     * Constructs new builder, retrieving the {@link android.content.SharedPreferences} object.
     * If the sharedPreferencesKey is null or empty, we use default shared preferences. The default
     * mode for this method is {@link android.content.Context#MODE_PRIVATE}
     *
     * @param context              The context to retrieve preferences from
     * @param sharedPreferencesKey The key of the shared preference to use. If null, default preferences will be used.
     */
    public PreferenceBuilder(Context context, String sharedPreferencesKey) {
        this(context, sharedPreferencesKey, Context.MODE_PRIVATE);
    }

    /**
     * Constructs new builder, retrieving the {@link android.content.SharedPreferences} object.
     * If the sharedPreferencesKey is null or empty, we use default shared preferences
     * {@link android.preference.PreferenceManager#getDefaultSharedPreferences(android.content.Context)}
     *
     * @param context              The context to retrieve preferences from
     * @param sharedPreferencesKey The key of the shared preference to use. If null, default preferences will be used.
     * @param mode                 The mode {@link android.content.Context#MODE_PRIVATE} or other modes.
     */
    public PreferenceBuilder(Context context, String sharedPreferencesKey, int mode) {
        if (sharedPreferencesKey != null && !sharedPreferencesKey.isEmpty()) {
            mPrefs = context.getSharedPreferences(sharedPreferencesKey, mode);
        } else {
            mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        }
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

    public String getPrefKey() {
        return mPrefKey;
    }

    public Class<PreferenceClass> getPrefType() {
        return mPrefType;
    }

    public PreferenceClass getDefaultValue() {
        return mDefaultValue;
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
     *
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
