package com.raizlabs.android.debugmodule.database;

import android.content.ContentValues;

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
    }

    public Type toValue(String text) {
        Object preference = null;
        if(text != null) {
            if (columnType.equals(Boolean.class)) {
                preference = text.equals("1") ? true : false;
            } else if (columnType.equals(Integer.class)) {
                preference = Integer.valueOf(text);
            } else if (columnType.equals(Float.class)) {
                preference = Float.valueOf(text);
            } else if (columnType.equals(Long.class)) {
                preference = Long.valueOf(text);
            } else if (columnType.equals(String.class)) {
                preference = text;
            }
        }
        return ((Type) preference);
    }

    String getUpdateColumnName() {
        return "`" + columnName + "`";
    }

    public void applytoContentValue(ContentValues contentValues) {
        if (columnType.equals(Boolean.class)) {
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
