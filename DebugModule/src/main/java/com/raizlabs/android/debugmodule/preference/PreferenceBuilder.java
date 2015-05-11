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
    private SharedPreferences preferences;

    /**
     * The key to retrieve preference from.
     */
    private String prefKey;

    /**
     * The name of the preference to use when display this item.
     */
    private String prefTitleName;

    /**
     * The type of preference to retrieve.
     */
    private Class<PreferenceClass> prefType;

    /**
     * The default value in case preference not found.
     */
    private PreferenceClass defaultValue;

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
            preferences = context.getSharedPreferences(sharedPreferencesKey, mode);
        } else {
            preferences = PreferenceManager.getDefaultSharedPreferences(context);
        }
    }

    /**
     * The key of the preference to retrieve
     *
     * @param prefKey The preference key
     * @return This instance
     */
    public PreferenceBuilder<PreferenceClass> prefKey(String prefKey) {
        this.prefKey = prefKey;
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
        this.prefType = prefType;
        return this;
    }

    /**
     * The default value to use when retrieving the preference
     *
     * @param defaultValue The default value
     * @return This instance
     */
    public PreferenceBuilder<PreferenceClass> defValue(PreferenceClass defaultValue) {
        this.defaultValue = defaultValue;
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
        prefTitleName = titleName;
        return this;
    }

    public String getTitle() {
        if(prefTitleName == null || prefTitleName.isEmpty()) {
            prefTitleName = prefKey;
        }
        return prefTitleName;
    }

    public Class<PreferenceClass> getPrefType() {
        return prefType;
    }

    public PreferenceClass getDefaultValue() {
        return defaultValue;
    }

    /**
     * @return The value from the stored preferences object based on the pref type.
     */
    @SuppressWarnings("unchecked")
    public PreferenceClass getPreference() {
        checkValues();
        Object preference = null;
        boolean isNull = (defaultValue == null);
        if (prefType.equals(Boolean.class)) {
            preference = preferences.getBoolean(prefKey, isNull ? false : (Boolean) defaultValue);
        } else if (prefType.equals(Integer.class)) {
            preference = preferences.getInt(prefKey, isNull ? 0 : (Integer) defaultValue);
        } else if (prefType.equals(Set.class)) {
            preference = preferences.getStringSet(prefKey, (Set<String>) defaultValue);
        } else if (prefType.equals(Float.class)) {
            preference = preferences.getFloat(prefKey, isNull ? 0.0f : (Float) defaultValue);
        } else if (prefType.equals(Long.class)) {
            preference = preferences.getLong(prefKey, isNull ? 0l : (Long) defaultValue);
        } else if (prefType.equals(String.class)) {
            preference = preferences.getString(prefKey, (String) defaultValue);
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
        if (prefType.equals(Boolean.class)) {
            editor.putBoolean(prefKey, preferenceValue == null ? false : (Boolean) preferenceValue);
        } else if (prefType.equals(Integer.class)) {
            editor.putInt(prefKey, preferenceValue == null ? 0 : (Integer) preferenceValue);
        } else if (prefType.equals(Set.class)) {
            editor.putStringSet(prefKey, (Set<String>) preferenceValue);
        } else if (prefType.equals(Float.class)) {
            editor.putFloat(prefKey, preferenceValue == null ? 0.0f : (Float) preferenceValue);
        } else if (prefType.equals(Long.class)) {
            editor.putLong(prefKey, preferenceValue == null ? 0l : (Long) preferenceValue);
        } else if (prefType.equals(String.class)) {
            editor.putString(prefKey, ((String) preferenceValue));
        }
        editor.apply();

        if (listener != null) {
            listener.onPreferenceChanged(prefKey, preferenceValue);
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
        if (prefType.equals(Boolean.class)) {
            preference = new Boolean(text);
        } else if (prefType.equals(Integer.class)) {
            preference = new Integer(text);
        } else if (prefType.equals(Set.class)) {
            preference = new HashSet<>();
            if (text != null) {
                String[] values = text.split(",");
                for (String value : values) {
                    ((HashSet) preference).add(value);
                }
            }
        } else if (prefType.equals(Float.class)) {
            preference = new Float(text);
        } else if (prefType.equals(Long.class)) {
            preference = new Long(text);
        } else if (prefType.equals(String.class)) {
            preference = text;
        }

        return ((PreferenceClass) preference);
    }

    private SharedPreferences.Editor edit() {
        return preferences.edit();
    }

    private void checkValues() {
        if(prefType == null || prefKey == null || prefKey.isEmpty()) {
            throw new IllegalStateException("The preference builder must have both a type and key");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PreferenceBuilder that = (PreferenceBuilder) o;

        if (!prefKey.equals(that.prefKey)) return false;
        if (!prefType.equals(that.prefType)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = prefKey.hashCode();
        result = 31 * result + prefType.hashCode();
        return result;
    }
}
