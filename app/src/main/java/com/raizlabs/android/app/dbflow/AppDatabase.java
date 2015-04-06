package com.raizlabs.android.app.dbflow;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Description:
 */
@Database(name = AppDatabase.NAME, version = AppDatabase.VERSION)
public class AppDatabase {

    public static final String NAME = "AppDatabase";

    public static final int VERSION = 1;
}
