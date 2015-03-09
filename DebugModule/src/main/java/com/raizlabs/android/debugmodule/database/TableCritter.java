package com.raizlabs.android.debugmodule.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.raizlabs.android.debugmodule.Critter;
import com.raizlabs.android.debugmodule.R;

import static android.database.Cursor.FIELD_TYPE_BLOB;
import static android.database.Cursor.FIELD_TYPE_FLOAT;
import static android.database.Cursor.FIELD_TYPE_INTEGER;
import static android.database.Cursor.FIELD_TYPE_NULL;
import static android.database.Cursor.FIELD_TYPE_STRING;

/**
 * Description:
 */
public class TableCritter implements Critter {

    private RecyclerView recyclerView;

    private SQLiteDatabase database;

    private String tableName;

    @Override
    public int getLayoutResId() {
        return R.layout.view_debug_module_table;
    }

    @Override
    public void handleView(@LayoutRes int layoutResource, View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.view_debug_module_table_list);

        Cursor cursor = database.query(tableName, null, null, null, null, null, null);
        TableAdapter tableAdapter = new TableAdapter(view.getContext(), cursor);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(tableAdapter);
    }

    public void setDatabase(String tableName, SQLiteDatabase database) {
        this.tableName = tableName;
        this.database = database;
    }

    @Override
    public void cleanup() {
        database = null;
    }

    private class TableAdapter extends RecyclerView.Adapter<TableAdapter.ViewHolder> {

        private Cursor cursor;

        public TableAdapter(Context context, Cursor c) {
            cursor = c;
        }

        @Override
        public int getItemCount() {
            return cursor.getCount()+1;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_table_row, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {

            viewHolder.contentArea.removeAllViews();
            if(position == 0) {
                String[] columns = cursor.getColumnNames();
                for (String column : columns) {
                    TextView valueDisplay = new TextView(viewHolder.itemView.getContext());
                    valueDisplay.setText(column);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.weight = 1;
                    valueDisplay.setLayoutParams(params);
                    viewHolder.contentArea.addView(valueDisplay);
                }
            } else if (cursor.moveToPosition(position+1)) {
                String[] columns = cursor.getColumnNames();
                for (String column : columns) {
                    TextView valueDisplay = new TextView(viewHolder.itemView.getContext());
                    int index = cursor.getColumnIndex(column);
                    int type = cursor.getType(index);
                    Object value = null;
                    switch (type) {
                        case FIELD_TYPE_BLOB:
                            value = cursor.getBlob(index);
                            break;
                        case FIELD_TYPE_FLOAT:
                            value = cursor.getFloat(index);
                            break;
                        case FIELD_TYPE_INTEGER:
                            value = cursor.getInt(index);
                            break;
                        case FIELD_TYPE_STRING:
                            value = cursor.getString(index);
                            break;
                        case FIELD_TYPE_NULL:
                        default:
                            break;
                    }

                    valueDisplay.setText(String.valueOf(value));
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.weight = 1;
                    valueDisplay.setLayoutParams(params);
                    viewHolder.contentArea.addView(valueDisplay);
                }
            }
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            LinearLayout contentArea;

            public ViewHolder(View itemView) {
                super(itemView);

                contentArea = (LinearLayout) itemView;
            }
        }
    }
}
