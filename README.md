# DebugModule

A powerful debug module that is fully pluggable, extendable, and very useful. It enables you to create your own ```Critter``` that contain 
UI elements which enable you configure your application on the fly. It injects a right-facing ```DrawerLayout``` into your activity on top of
all other content, making it very unobtrusive and accessible from __everywhere within your application__.

## Getting Started

### Inclusion

Since this is a **Debug Module**, we should __explicitly__ only include it in our debug app builds, or a specific ```buildFlavor```,
```buildType```, or ```buildVariant```.

For now, you need to clone the project into your project and include it as a dependency:

```groovy

dependencies {
  debugCompile project(':Libraries:DebugModule')
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

public abstract class DebugOptionsProvider {

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

