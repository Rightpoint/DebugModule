package com.raizlabs.android.debugmodule.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Description:
 */
public class DatabaseCritterUtils {

    private static final ArrayList<String> BLACKLIST = new ArrayList<String>() {{
        add("android_metadata");
        add("sqlite_sequence");
    }};

    public static ArrayList<String> getDbTableDetails(SQLiteDatabase db, boolean useBlackList) {
        Cursor c = db.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table'", null);
        ArrayList<String> result = new ArrayList<>();
        int i = 0;
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            String name = c.getString(0);
            if (!useBlackList || (useBlackList && !BLACKLIST.contains(name)))
                result.add(name);
        }

        return result;
    }
}
