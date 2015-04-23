package com.raizlabs.android.debugmodule.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.raizlabs.android.debugmodule.Critter;
import com.raizlabs.android.debugmodule.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description:
 */
public class RowCritter implements Critter {

    public interface ColumnChangeListener {

        /**
         * Called when a column's value changes for a specific row
         *
         * @param column The column changed.
         */
        public void onColumnChanged(Column column);
    }

    LinearLayout rowLayout;
    Button saveButton;
    private Map<String, Column> columnDataMap;
    private Map<String, Column> originalColumnMap;
    private List<String> columnNameSet;
    private String tableName;
    private SQLiteDatabase database;

    private RowEditAdapter adapter;

    @Override
    public int getLayoutResId() {
        return R.layout.view_debug_module_row;
    }

    @Override
    public void handleView(@LayoutRes int layoutResource, View view) {
        rowLayout = ((LinearLayout) view.findViewById(R.id.rowLayout));
        saveButton = (Button) view.findViewById(R.id.view_debug_module_row_save);
        saveButton.setOnClickListener(clickListener);

        adapter = new RowEditAdapter();

        rowLayout.removeAllViews();
        for (int i = 0; i < adapter.getCount(); i++) {
            rowLayout.addView(adapter.getView(i, null, rowLayout));
        }
    }

    public void setColumnDataMap(String tableName, SQLiteDatabase database, Map<String, Column> map) {
        this.tableName = tableName;
        this.database = database;
        Set<String> keys = map.keySet();
        columnDataMap = new HashMap<>();
        for(String key: keys) {
            Column column = new Column(map.get(key));
            columnDataMap.put(key, column);
        }
        originalColumnMap = map;
        columnNameSet = new ArrayList<>(columnDataMap.keySet());
    }

    @Override
    public void cleanup() {
        columnDataMap = null;
    }

    ColumnChangeListener changeListener = new ColumnChangeListener() {
        @Override
        public void onColumnChanged(Column column) {
            columnDataMap.put(column.columnName, column);
        }
    };

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ContentValues contentValues = DatabaseCritterUtils.toContentValues(columnDataMap);
            int count = database.updateWithOnConflict(tableName, contentValues, DatabaseCritterUtils.toWhere(originalColumnMap), null, SQLiteDatabase.CONFLICT_FAIL);
            Toast.makeText(v.getContext(), count == 1 ? "Row updated successfully" :
                    (count > 1 ? "Update touched more than one row" : "Row update failed"), Toast.LENGTH_SHORT).show();
        }
    };

    private class RowEditAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return columnDataMap.size();
        }

        @Override
        public String getItem(int position) {
            return columnNameSet.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            RowEditView rowEditView;
            if (convertView == null) {
                rowEditView = new RowEditView(parent.getContext());
            } else {
                rowEditView = (RowEditView) convertView;
            }
            String columnName = getItem(position);
            rowEditView.populate(columnDataMap.get(columnName), changeListener);
            return rowEditView;
        }
    }
}
