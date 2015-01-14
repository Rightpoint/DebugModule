package com.raizlabs.android.debugmodule.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Description: Builds a manipulated preference object. This handles how it changes.
 */
public class PreferenceBuilder<PreferenceClass> {

    private static final List<Class> VALID_TYPES = new ArrayList<Class>(Arrays.asList(Boolean.class,
            Integer.class, Float.class, Set.class, Long.class, String.class));

    /**
     * The shared preferences handle that we use.
     */
    private SharedPreferences mPrefs;

    /**
     * The key to retrieve preference from.
     */
    private String mPrefKey;

    /**
     * The name of the preference to use when display this item.
     */
    private String mPrefTitleName;

    /**
     * The type of preference to retrieve.
     */
    private Class<PreferenceClass> mPrefType;

    /**
     * The default value in case preference not found.
     */
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

    /**
     * The key of the preference to retrieve
     *
     * @param prefKey The preference key
     * @return This instance
     */
    public PreferenceBuilder<PreferenceClass> prefKey(String prefKey) {
        mPrefKey = prefKey;
        return this;
    }

    /**
     * The type of preference to retrieve
     *
     * @param prefType The type of preference
     * @return This instance
     */
    public PreferenceBuilder<PreferenceClass> prefType(Class<PreferenceClass> prefType) {
        if(!VALID_TYPES.contains(prefType)) {
            throw new IllegalArgumentException("The specified type: " + prefType + " is not supported in preferences");
        }
        mPrefType = prefType;
        return this;
    }

    /**
     * The default value to use when retrieving the preference
     *
     * @param defaultValue The default value
     * @return This instance
     */
    public PreferenceBuilder<PreferenceClass> defValue(PreferenceClass defaultValue) {
        mDefaultValue = defaultValue;
        return this;
    }

    /**
     * Specifies a name to display on screen when this preference is shown. If not specified, it will be
     * the key name.
     *
     * @param titleName The title of the preference
     * @return This instance
     */
    public PreferenceBuilder<PreferenceClass> titleName(String titleName) {
        mPrefTitleName = titleName;
        return this;
    }

    public String getTitle() {
        if(mPrefTitleName == null || mPrefTitleName.isEmpty()) {
            mPrefTitleName = mPrefKey;
        }
        return mPrefTitleName;
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
        checkValues();
        Object preference = null;
        boolean isNull = (mDefaultValue == null);
        if (mPrefType.equals(Boolean.class)) {
            preference = mPrefs.getBoolean(mPrefKey, isNull ? false : (Boolean) mDefaultValue);
        } else if (mPrefType.equals(Integer.class)) {
            preference = mPrefs.getInt(mPrefKey, isNull ? 0 : (Integer) mDefaultValue);
        } else if (mPrefType.equals(Set.class)) {
            preference = mPrefs.getStringSet(mPrefKey, (Set<String>) mDefaultValue);
        } else if (mPrefType.equals(Float.class)) {
            preference = mPrefs.getFloat(mPrefKey, isNull ? 0.0f : (Float) mDefaultValue);
        } else if (mPrefType.equals(Long.class)) {
            preference = mPrefs.getLong(mPrefKey, isNull ? 0l : (Long) mDefaultValue);
        } else if (mPrefType.equals(String.class)) {
            preference = mPrefs.getString(mPrefKey, (String) mDefaultValue);
        }


        return (PreferenceClass) preference;
    }

    /**
     * Changes the preference
     *
     * @param preferenceValue The value of the preference to apply
     */
    public void applyPreference(PreferenceClass preferenceValue, PreferenceChangeListener listener) {
        checkValues();
        SharedPreferences.Editor editor = edit();
        if (mPrefType.equals(Boolean.class)) {
            editor.putBoolean(mPrefKey, preferenceValue == null ? false : (Boolean) preferenceValue);
        } else if (mPrefType.equals(Integer.class)) {
            editor.putInt(mPrefKey, preferenceValue == null ? 0 : (Integer) preferenceValue);
        } else if (mPrefType.equals(Set.class)) {
            editor.putStringSet(mPrefKey, (Set<String>) preferenceValue);
        } else if (mPrefType.equals(Float.class)) {
            editor.putFloat(mPrefKey, preferenceValue == null ? 0.0f : (Float) preferenceValue);
        } else if (mPrefType.equals(Long.class)) {
            editor.putLong(mPrefKey, preferenceValue == null ? 0l : (Long) preferenceValue);
        } else if (mPrefType.equals(String.class)) {
            editor.putString(mPrefKey, ((String) preferenceValue));
        }
        editor.apply();

        if (listener != null) {
            listener.onPreferenceChanged(mPrefKey, preferenceValue);
        }
    }

    /**
     * Turns text into the proper {@link PreferenceClass}
     *
     * @param text The text convert
     * @return The value
     */
    @SuppressWarnings("unchecked")
    public PreferenceClass toValue(String text) throws NumberFormatException {
        checkValues();
        Object preference = null;
        if (mPrefType.equals(Boolean.class)) {
            preference = new Boolean(text);
        } else if (mPrefType.equals(Integer.class)) {
            preference = new Integer(text);
        } else if (mPrefType.equals(Set.class)) {
            preference = new HashSet<>();
            if (text != null) {
                String[] values = text.split(",");
                for (String value : values) {
                    ((HashSet) preference).add(value);
                }
            }
        } else if (mPrefType.equals(Float.class)) {
            preference = new Float(text);
        } else if (mPrefType.equals(Long.class)) {
            preference = new Long(text);
        } else if (mPrefType.equals(String.class)) {
            preference = text;
        }

        return ((PreferenceClass) preference);
    }

    private SharedPreferences.Editor edit() {
        return mPrefs.edit();
    }

    private void checkValues() {
        if(mPrefType == null || mPrefKey == null || mPrefKey.isEmpty()) {
            throw new IllegalStateException("The preference builder must have both a type and key");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PreferenceBuilder that = (PreferenceBuilder) o;

        if (!mPrefKey.equals(that.mPrefKey)) return false;
        if (!mPrefType.equals(that.mPrefType)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = mPrefKey.hashCode();
        result = 31 * result + mPrefType.hashCode();
        return result;
    }
}
