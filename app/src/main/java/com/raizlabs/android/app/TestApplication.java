package com.raizlabs.android.app;

import android.app.Application;

import com.raizlabs.android.app.dbflow.AppDatabase;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.debugmodule.Debugger;
import com.raizlabs.android.debugmodule.database.DatabaseCritter;
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

        DatabaseCritter databaseCritter = new DatabaseCritter();

        Debugger.getInstance().use("UrlManipulator", new UrlCritter("http://www.google.com/", this))
                .use("Test App Details", new AppInformationCritter(BuildConfig.FLAVOR, BuildConfig.BUILD_TYPE))
                .use("PreferenceCritter", new PreferenceCritter())
                .use("Database Browser", databaseCritter);

        FlowManager.init(this);
        databaseCritter.setDatabase(FlowManager.getDatabase(AppDatabase.NAME).getWritableDatabase(), true);
    }
}
