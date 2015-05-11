package com.raizlabs.android.debugmodule.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: Provides utility methods for databases in this module.
 */
public class DatabaseCritterUtils {

    private static final ArrayList<String> BLACKLIST = new ArrayList<String>() {{
        add("android_metadata");
        add("sqlite_sequence");
    }};

    /**
     * @param db           The database to load and get table names from.
     * @param useBlackList If we use the blacklist, we ignore internal system tables.
     * @return list of table names.
     */
    public static ArrayList<String> getTableNames(SQLiteDatabase db, boolean useBlackList) {
        Cursor c = db.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table'", null);
        ArrayList<String> result = new ArrayList<>();
        int i = 0;
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            String name = c.getString(0);
            if (!useBlackList || (useBlackList && !BLACKLIST.contains(name))) {
                result.add(name);
            }
        }

        return result;
    }

    /**
     * @param database  The database to use.
     * @param tableName The name of table.
     * @param cursor    The cursor we're retrieving data from.
     * @param position  The position of the cursor.
     * @return the map of values between column names.
     */
    public static Map<String, Column> getDbRowMap(SQLiteDatabase database, String tableName, Cursor cursor,
                                                  int position) {
        Map<String, Column> data = new HashMap<>();

        // get table info for real column values
        Cursor tableInfo = database.rawQuery(String.format("PRAGMA table_info(%1s)", tableName), null);
        if (tableInfo.moveToFirst()) {
            do {
                String type = tableInfo.getString(tableInfo.getColumnIndex("type"));
                String columnName = tableInfo.getString(tableInfo.getColumnIndex("name"));
                boolean notNull = (tableInfo.getInt(tableInfo.getColumnIndex("notnull")) == 1);

                Column column = new Column();
                column.columnName = columnName;
                column.notNull = notNull;
                switch (type) {
                    case "INTEGER":
                        column.columnType = Integer.class;
                        break;
                    case "BLOB":
                        column.columnType = byte[].class;
                        break;
                    case "TEXT":
                        column.columnType = String.class;
                        break;
                    case "REAL":
                        column.columnType = Float.class;
                        break;
                }
                data.put(columnName, column);
            } while (tableInfo.moveToNext());
        }
        tableInfo.close();

        if (cursor.moveToPosition(position + 1)) {
            String[] columns = cursor.getColumnNames();
            for (String column : columns) {
                int index = cursor.getColumnIndex(column);
                Column columnObject = data.get(column);
                Object value = null;
                if (!cursor.isNull(index)) {
                    if (columnObject.columnType == byte[].class) {
                        value = cursor.getBlob(index);
                    } else if (columnObject.columnType == Float.class) {
                        value = cursor.getFloat(index);
                    } else if (columnObject.columnType == Integer.class) {
                        value = cursor.getInt(index);
                    } else if (columnObject.columnType == String.class) {
                        value = cursor.getString(index);
                    }
                }
                columnObject.value = value;
            }
        }
        return data;
    }

    /**
     * @param columnMap The map of column to content values.
     * @return A {@link ContentValues} from map of data.
     */
    public static ContentValues toContentValues(Map<String, Column> columnMap) {
        ContentValues contentValues = new ContentValues();
        Set<String> columnNames = columnMap.keySet();
        for (String columnName : columnNames) {
            Column column = columnMap.get(columnName);
            column.applytoContentValue(contentValues);
        }
        return contentValues;
    }

    /**
     * @param columnMap The map of column names to values.
     * @return A WHERE query for specified data.
     */
    public static String toWhere(Map<String, Column> columnMap) {
        StringBuilder where = new StringBuilder();
        List<String> columnNames = new ArrayList<>(columnMap.keySet());
        for (int i = 0; i < columnNames.size(); i++) {
            if (i > 0) {
                where.append(" AND ");
            }
            String columnName = columnNames.get(i);
            Column column = columnMap.get(columnName);
            where.append(column.getUpdateColumnName()).append(" = ");
            if (column.value instanceof String) {
                where.append(DatabaseUtils.sqlEscapeString(column.value.toString()));
            } else {
                where.append(column.value);
            }
        }
        return where.toString();
    }
}
