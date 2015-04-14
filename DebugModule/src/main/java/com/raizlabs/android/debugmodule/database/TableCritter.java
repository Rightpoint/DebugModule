package com.raizlabs.android.debugmodule.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.LayoutRes;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.raizlabs.android.debugmodule.Critter;
import com.raizlabs.android.debugmodule.DebugCritterFragment;
import com.raizlabs.android.debugmodule.Debugger;
import com.raizlabs.android.debugmodule.R;

import java.util.Map;

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

    private int maxCharacterCount = 50;

    TableAdapter tableAdapter;

    int layoutRes;

    @Override
    public int getLayoutResId() {
        return R.layout.view_debug_module_table;
    }

    @Override
    public void handleView(@LayoutRes int layoutResource, View view) {
        layoutRes = layoutResource;
        recyclerView = (RecyclerView) view.findViewById(R.id.view_debug_module_table_list);

        if(database != null) {
            Cursor cursor = database.query(tableName, null, null, null, null, null, null);
            tableAdapter = new TableAdapter(view.getContext(), cursor);
            FixedGridLayoutManager gridLayoutManager = new FixedGridLayoutManager();
            gridLayoutManager.setTotalColumnCount(cursor.getColumnCount());
            recyclerView.setLayoutManager(gridLayoutManager);
            recyclerView.setAdapter(tableAdapter);
        }
    }

    public void setDatabase(String tableName, SQLiteDatabase database) {
        this.tableName = tableName;
        this.database = database;
    }

    /**
     * Sets how many characters we care about in each row. If its any longer, we truncate the string.
     *
     * @param maxCharacterCount The count of characters per column in row
     */
    public void setMaxCharacterCount(int maxCharacterCount) {
        this.maxCharacterCount = maxCharacterCount;
    }

    @Override
    public void cleanup() {
        Debugger.getInstance().disposeQuietly(tableName + "-Table");
    }

    final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            Cursor currentRow = tableAdapter.getCursor();
            Map<String, Column> columnMap = DatabaseCritterUtils.getDbRowMap(currentRow, position);

            RowCritter rowCritter = new RowCritter();
            rowCritter.setColumnDataMap(tableName, database, columnMap);
            String critterName = tableName + "-Row" + position;

            Debugger.getInstance().use(critterName, rowCritter);

            DebugCritterFragment debugCritterFragment = DebugCritterFragment.newInstance(critterName, getLayoutResId());

            FragmentActivity fragmentActivity = (FragmentActivity) v.getContext();
            fragmentActivity.getSupportFragmentManager()
                    .beginTransaction()
                    .addToBackStack(null)
                    .replace(layoutRes, debugCritterFragment)
                    .commit();
        }
    };

    private class TableAdapter extends RecyclerView.Adapter<TableAdapter.ViewHolder> {

        private final Cursor cursor;

        private final int columnCount;

        private final String[] columnNames;

        public Cursor getCursor() {
            return cursor;
        }

        public TableAdapter(Context context, Cursor c) {
            cursor = c;
            columnCount = c.getColumnCount();
            columnNames = c.getColumnNames();
        }

        @Override
        public int getItemCount() {
            return (cursor.getCount() + 1) * columnCount;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            return new ViewHolder(
                    LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_table_row, viewGroup,
                                                                        false));
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            int columnPosition = position%columnCount;
            if (position < columnCount) {
                viewHolder.contentArea.setText(columnNames[columnPosition]);
            } else {
                viewHolder.contentArea.setOnClickListener(onClickListener);

                int cursorPosition = position/columnCount - 1;
                viewHolder.contentArea.setTag(cursorPosition-1);
                if(cursor.moveToPosition(cursorPosition)) {
                    String currentColumn = columnNames[columnPosition];
                    int index = cursor.getColumnIndex(currentColumn);
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
                    // limit to characters specified.
                    String display = String.valueOf(value);
                    viewHolder.contentArea.setText(
                            display.length() < maxCharacterCount ? display : display.substring(0, maxCharacterCount));
                } else {
                    viewHolder.contentArea.setText("ERROR");
                }
            }
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView contentArea;

            public ViewHolder(View itemView) {
                super(itemView);

                contentArea = (TextView) itemView;
            }
        }
    }
}
