# Preference Critter

The purpose of this ```Critter``` is to enable dynamic, runtime changes to app preferences. It is particularly useful when you want to forgo clearing application memory to reperform a one-shot action. This is also enables you to test to see how the app will respond a custom value you choose.

How to add preferences:

```java

//register with Debugger
Debugger.getInstance().use(new PreferenceCritter());

// add preferences
pref.addPreference(new PreferenceBuilder<String>(this)
                        .prefKey("preference_test_name")
                        .prefType(String.class)
                        .titleName("String example"))
                .addPreference(new PreferenceBuilder<Boolean>(this)
                        .prefKey("preference_boolean")
                        .prefType(Boolean.class)
                        .titleName("Boolean example"))
                .addPreference(new PreferenceBuilder<Integer>(this)
                        .prefKey("preference_int")
                        .prefType(Integer.class)
                        .titleName("Integer example"));

```

```PreferenceBuilder```: a simple wrapper around interacting with ```SharedPreferences``` that enables us to display and declare modifiable preferences easily.

Now when you open the debug menu, go to this page you will see the preferences register. In order to see changes you need to type in a new value and tap enter to have its value changed.
