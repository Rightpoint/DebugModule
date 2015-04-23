[![Raizlabs Repository](http://img.shields.io/badge/Raizlabs%20Repository-2.0.0-blue.svg?style=flat)](https://github.com/Raizlabs/maven-releases)

# DebugModule

A powerful debug module that is fully pluggable, extendable, and very useful. It enables you to create your own ```Critter``` that contain
UI elements which enable you configure your application on the fly.

It also can inject a right-facing ```DrawerLayout``` into your activity on top of
all other content, making it very unobtrusive and accessible from __everywhere within your application__.

## Getting Started

Add the library to the project-level build.gradle, using the
[Griddle](https://github.com/Raizlabs/Griddle) plugin to simplify your build.gradle and link sources:

```groovy

  apply plugin: 'com.raizlabs.griddle'

  dependencies {
    mod 'com.raizlabs.android:DebugModule:2.0.0'
  }

```

or by standard Gradle use (without linking sources support):

```groovy

  dependencies {
    compile "com.raizlabs.android:DebugModule:2.0.0"
  }

```

### Injection

In order to make this work as expected, this section will contain how to properly ensure interaction between the ```Debugger``` and
the application.

#### Create A Common Interface or Abstract Class

The common abstract class or interface that you include in ```src/main/java``` will ensure that our completed implementations
can differ between ```release``` and ```debug``` as well as allow the implementation to be uniformly used.

Here is an example of a provider with ```UrlCritter``` capabilities:

```java

public abstract class AppOptionsProvider {

    private static CompletedAppUrlProvider provider;

    /**
     * @return The static instance of the provider. Choosing debug vs. release will pick the correct
     * provider to use.
     */
    public static CompletedAppUrlProvider getProvider() {
        if (provider == null) {
            provider = new CompletedAppUrlProvider();
        }
        return provider;
    }

    /**
     * Called in the Application class. It will perform some
     * setup here.
     *
     * @param context The application context.
     */
    public abstract void init(Context context);

    /**
     * Attaches the debug options to the activity if its in debug mode, otherwise we do nothing.
     *
     * @param activity The activity that the debugger is attached to
     */
    public void attach(FragmentActivity activity) {

    }

    /**
     * @return The URL that the app uses
     */
    public abstract String getConfigUrl();

    /**
     * @param activity The activity that the debugger is attached to.
     * @return true if the CompletedAppUrlProvider consumed the event.
     */
    public boolean onBackPressed(FragmentActivity activity) {
        return false;
    }
}

```

#### Implement the Abstract Class or Interface in all variants

For this example, we are relying on the ```buildType``` to implement the abstract class properly. First we will show what the
```release``` version is expected to do (in ```src/release/java```):

```java

/**
 * Description: Defines the implementation for an DebugOptionsProvider for release.
 * We only return the main app endpoint.
 */
public class CompletedDebugOptionsProvider extends AppOptionsProvider {

    private String mUrl;

    @Override
    public void init(Context context) {
        mUrl = context.getString(R.string.Endpoint_AppConfig);
    }

    @Override
    public String getConfigUrl() {
        return mUrl;
    }
}


```

In this case an abstract class is an advantage, since we only need to implement methods that we intend to use. Using that,
all other methods as part of ```AppOptionsProvider``` will do nothing.

Here is the ```debug``` version of the implementation. Please note that the package name and class name **must** be the same.

```java

/**
 * Description: Defines the implementation for an {@link DebugOptionsProvider}
 * for debug. We use the debug module to show the menu.
 */
public class CompletedAppUrlProvider extends AppOptionsProvider {

    public static final String CRITTER_URL_NAME = "Select App Endpoint";

    public static final String CRITTER_APP_NAME = "Build Information";

    @Override
    public void init(Context context) {
        UrlCritter urlCritter = new UrlCritter(context.getString(R.string.Endpoint_AppConfig), context);
        urlCritter.addUrlTypedArray(R.array.AppConfig_Endpoints, context);
        Debugger.getInstance().use(CRITTER_URL_NAME, urlCritter)
                .use(CRITTER_APP_NAME, new AppInformationCritter(BuildConfig.FLAVOR, BuildConfig.BUILD_TYPE));

        urlCritter.registerUrlChangeListener(mChangeListener);
    }

    @Override
    public void attach(FragmentActivity fragmentActivity) {
        Debugger.getInstance().attach(fragmentActivity);
    }

    @Override
    public String getConfigUrl() {
        return ((UrlCritter) Debugger.getInstance().getCritter(CRITTER_URL_NAME)).getCurrentUrl();
    }

    @Override
    public boolean onBackPressed(FragmentActivity activity) {
        return Debugger.getInstance().onBackPressed(activity);
    }

    /**
     * Called when URL changes
     */
    private final UrlCritter.UrlChangeListener mChangeListener = new UrlCritter.UrlChangeListener() {
        @Override
        public void onUrlChanged(String url) {
            // URL for the app changed, do something here
        }
    };
}

```

#### Inject the AbstractClass/Interface methods


Since we created the common interface, now we implement the methods where we intended them to be used:

In ```Application``` we initialize the provider:

```java

public class ExampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DebugOptionsProvider.getProvider().init(this);
    }
}


```

Create a ```BaseActivity``` so that all our activities get access to the debugger when running the ```debug``` build variant:

```java

public abstract class BaseActivity extends FragmentActivity {

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        DebugOptionsProvider.getProvider().attach(this);
    }

    @Override
    public void onBackPressed() {
        if(!DebugOptionsProvider.getProvider().onBackPressed(this)) {
            super.onBackPressed();
        }
    }
}

```

And that's it. Now you have a debugger that is only added to the ```.apk``` when running in ```debug``` variant! The release version will not contain this module at all if done properly.

## Critters

A ```Critter``` is the main interface for constructing a debug submenu. It, at its base, only requires specifying a UI. That UI drives how the tester can change properties in an application during runtime.

We have provided a few default ```Critter``` for URL switching and app information display.

```UrlCritter```: Displays a list of URLs that a tester can select from, providing callbacks for when those change. It also enables adding custom endpoints at runtime (which are persisted in app data).

```AppInformationCritter```: Displays package name, build type, build variant, app version, and more.

```PreferenceCritter```: Enables dynamic changes to preferences you provide while the app is running. Instead of having to clear app data and reopen the app, you can change it within the app very easily.

```DatabaseCritter```: Viewing database tables (very basically) for an app without needing to pull the sqlite file off the device or using another machine.

You can create your own custom ```Critter``` fairly easily and it is flexible on what goes into it. It is up to your __imagination__ on what you can configure at runtime for your app.

### Critters

How to register to the ```Debugger```:

```java
// we register our critters here and can use any number of ones but names must remain unique
Debugger.getInstance().use("critterName", new UrlCritter("http://www.google.com/", this))
    .use("anotherName", new PreferenceCritter());

```

How to retrieve from the ```Debugger```:

```java

UrlCritter urlCritter = Debugger.getInstance().getCritter(UrlCritter.class, "critterName");

```

Check out the currently included ```Critter```:

[UrlCritter](https://github.com/Raizlabs/DebugModule/blob/master/usage/UrlCritter.md)

[App Information Critter](https://github.com/Raizlabs/DebugModule/blob/master/usage/AppInformationCritter.md)

[Preference Critter](https://github.com/Raizlabs/DebugModule/blob/master/usage/PrefCritter.md)

[Database Browser](https://github.com/Raizlabs/DebugModule/blob/master/usage/DatabaseCritter.md)

# Maintainers

[agrosner](https://github.com/agrosner)
