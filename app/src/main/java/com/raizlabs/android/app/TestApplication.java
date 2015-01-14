package com.raizlabs.android.app;

import android.app.Application;

import com.raizlabs.android.debugmodule.Debugger;
import com.raizlabs.android.debugmodule.info.AppInformationCritter;
import com.raizlabs.android.debugmodule.preference.PreferenceCritter;
import com.raizlabs.android.debugmodule.url.UrlCritter;

/**
 * Description:
 */
public class TestApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Debugger.getInstance().use("UrlManipulator", new UrlCritter("http://www.google.com/", this))
                .use("Test App Details", new AppInformationCritter(BuildConfig.FLAVOR, BuildConfig.BUILD_TYPE))
                .use("PreferenceCritter", new PreferenceCritter());
    }
}
