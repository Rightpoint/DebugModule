package com.raizlabs.android.debugmodule.database;

import android.content.ContentValues;
import android.text.TextUtils;

/**
 * Description: Represents a column value of a database row
 */
public class Column<Type> {

    String columnName;

    Type value;

    Class<Type> columnType;

    boolean notNull = false;

    public Column() {
    }

    public Column(Column<Type> column) {
        this.columnName = column.columnName;
        this.value = column.value;
        this.columnType = column.columnType;
        this.notNull = column.notNull;
    }

    public Type toValue(String text) {
        Object value = null;
        if(!TextUtils.isEmpty(text)) {
            if (columnType.equals(Boolean.class)) {
                value = text.equals("1") ? true : false;
            } else if (columnType.equals(Integer.class)) {
                value = Integer.valueOf(text);
            } else if (columnType.equals(Float.class)) {
                value = Float.valueOf(text);
            } else if (columnType.equals(Long.class)) {
                value = Long.valueOf(text);
            } else if (columnType.equals(String.class)) {
                value = text;
            }
        }
        return ((Type) value);
    }

    String getUpdateColumnName() {
        return "`" + columnName + "`";
    }

    public void applytoContentValue(ContentValues contentValues) {
        if(value == null) {
            contentValues.putNull(getUpdateColumnName());
        } else if (columnType.equals(Boolean.class)) {
            contentValues.put(getUpdateColumnName(), (Boolean) value);
        } else if (columnType.equals(Integer.class)) {
            contentValues.put(getUpdateColumnName(), (Integer) value);
        } else if (columnType.equals(Float.class)) {
            contentValues.put(getUpdateColumnName(), ((Float) value));
        } else if (columnType.equals(Long.class)) {
            contentValues.put(getUpdateColumnName(), (Long) value);
        } else if (columnType.equals(String.class)) {
            contentValues.put(getUpdateColumnName(), (String) value);
        }
    }
}
