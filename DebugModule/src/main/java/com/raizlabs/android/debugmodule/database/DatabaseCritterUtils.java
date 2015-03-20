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

import static android.database.Cursor.FIELD_TYPE_BLOB;
import static android.database.Cursor.FIELD_TYPE_FLOAT;
import static android.database.Cursor.FIELD_TYPE_INTEGER;
import static android.database.Cursor.FIELD_TYPE_NULL;
import static android.database.Cursor.FIELD_TYPE_STRING;

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

    public static Map<String, Column> getDbRowMap(Cursor cursor, int position) {
        Map<String, Column> data = new HashMap<>();
        if (cursor.moveToPosition(position + 1)) {
            String[] columns = cursor.getColumnNames();
            for (String column : columns) {
                int index = cursor.getColumnIndex(column);
                int type = cursor.getType(index);
                Object value = null;
                Class clazz = null;
                switch (type) {
                    case FIELD_TYPE_BLOB:
                        value = cursor.getBlob(index);
                        clazz = byte[].class;
                        break;
                    case FIELD_TYPE_FLOAT:
                        value = cursor.getFloat(index);
                        clazz = Float.class;
                        break;
                    case FIELD_TYPE_INTEGER:
                        value = cursor.getInt(index);
                        clazz = Integer.class;
                        break;
                    case FIELD_TYPE_STRING:
                        value = cursor.getString(index);
                        clazz = String.class;
                        break;
                    case FIELD_TYPE_NULL:
                    default:
                        break;
                }
                Column columnObject = new Column();
                columnObject.columnType = clazz;
                columnObject.columnName = column;
                columnObject.value = value;
                data.put(column, columnObject);
            }
        }
        return data;
    }

    public static ContentValues toContentValues(Map<String, Column> columnMap) {
        ContentValues contentValues = new ContentValues();
        Set<String> columnNames = columnMap.keySet();
        for(String columnName: columnNames) {
            Column column = columnMap.get(columnName);
            column.applytoContentValue(contentValues);
        }
        return contentValues;
    }

    public static String toWhere(Map<String, Column> columnMap) {
        StringBuilder where = new StringBuilder();
        List<String> columnNames = new ArrayList<>(columnMap.keySet());
        for(int i = 0; i < columnNames.size(); i++) {
            if(i > 0) {
                where.append(" AND ");
            }
            String columnName = columnNames.get(i);
            Column column = columnMap.get(columnName);
            where.append(column.getUpdateColumnName()).append(" = ");
            if(column.value instanceof String) {
                where.append(DatabaseUtils.sqlEscapeString(column.value.toString()));
            } else {
                where.append(column.value);
            }
        }
        return where.toString();
    }
}
