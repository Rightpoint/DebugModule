package com.raizlabs.android.debugmodule.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.raizlabs.android.debugmodule.Critter;
import com.raizlabs.android.debugmodule.R;

/**
 * Description:
 */
public class TableCritter implements Critter {

    private ListView listView;

    private SQLiteDatabase database;

    private String tableName;

    @Override
    public int getLayoutResId() {
        return R.layout.view_debug_module_table;
    }

    @Override
    public void handleView(@LayoutRes int layoutResource, View view) {
        listView = (ListView) view.findViewById(R.id.view_debug_module_table_list);

        Cursor cursor = database.query(tableName, null, null, null, null, null, null);
        TableAdapter tableAdapter = new TableAdapter(view.getContext(), cursor);
        listView.setAdapter(tableAdapter);
    }

    public void setDatabase(String tableName, SQLiteDatabase database) {
        this.tableName = tableName;
        this.database = database;
    }

    @Override
    public void cleanup() {
        database = null;
    }

    private class TableAdapter extends BaseAdapter {

        private Cursor cursor;

        public TableAdapter(Context context, Cursor c) {
            cursor = c;
        }

        @Override
        public int getCount() {
            return cursor.getCount();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Object getItem(int position) {
            if (cursor != null) {
                cursor.moveToPosition(position);
                return cursor;
            } else {
                return null;
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }

        @Override
        public int getItemViewType(int position) {
            return position == 0 ? 0 : 1;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }
    }
}
