package com.raizlabs.android.debugmodule.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.LayoutRes;
import android.support.v4.app.FragmentActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.raizlabs.android.debugmodule.Critter;
import com.raizlabs.android.debugmodule.DebugCritterFragment;
import com.raizlabs.android.debugmodule.Debugger;
import com.raizlabs.android.debugmodule.R;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Description: Provides a view of the current app's database tables and rows
 */
public class DatabaseCritter implements Critter {

    ListView listView;

    private DatabaseAdapter adapter;

    private SQLiteDatabase database;

    private boolean useBlackList;

    private Context mContext;

    private int layoutRes;

    @Override
    public int getLayoutResId() {
        return R.layout.view_debug_module_database;
    }

    @Override
    public void handleView(@LayoutRes int layoutResource, View view) {
        layoutRes = layoutResource;
        mContext = view.getContext();
        listView = (ListView) view.findViewById(R.id.view_debug_module_database_list);
        listView.setAdapter(adapter = new DatabaseAdapter());
        listView.setOnItemClickListener(onItemClickListener);

        // if DB set before displayed.
        if (database != null) {
            adapter.setDatabase(database, useBlackList);
        }
    }

    @Override
    public void cleanup() {
        mContext = null;
    }

    public void setUseBlackList(boolean useBlackList) {
        this.useBlackList = useBlackList;
    }

    /**
     * Sets and displays the database defined here.
     *
     * @param database     The database to load.
     * @param useBlackList The black list that is used for system level tables such as android_metadata, which
     *                     we may want to ignore.
     */
    public void setDatabase(SQLiteDatabase database, boolean useBlackList) {
        this.database = database;
        this.useBlackList = useBlackList;
        if (adapter != null) {
            adapter.setDatabase(database, useBlackList);
        }
    }

    private final AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String tableName = adapter.getItem(position);
            TableCritter tableCritter = new TableCritter();
            tableCritter.setDatabase(tableName, database);
            Debugger.getInstance().use(tableName + "-Table", tableCritter);

            DebugCritterFragment debugCritterFragment = DebugCritterFragment.newInstance(tableName + "-Table", layoutRes);
            if(mContext instanceof FragmentActivity) {
                ((FragmentActivity) mContext).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(layoutRes, debugCritterFragment)
                        .addToBackStack(null)
                        .commit();
            } else {
                Toast.makeText(mContext, "Cannot go to table: Context is not of type FragmentActivity", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private class DatabaseAdapter extends BaseAdapter {

        private ArrayList<String> tables = new ArrayList<>();

        public void setDatabase(SQLiteDatabase database, boolean useBlackList) {
            tables = DatabaseCritterUtils.getDbTableDetails(database, useBlackList);

            // sort
            Collections.sort(tables);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return tables.size();
        }

        @Override
        public String getItem(int position) {
            return tables.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView name;
            if (convertView == null) {
                name = new TextView(parent.getContext());
                name.setTextSize(TypedValue.COMPLEX_UNIT_PX, parent.getResources().getDimension(R.dimen.DebugModule_TextSize_Label));
            } else {
                name = (TextView) convertView;
            }
            name.setText(getItem(position));
            return name;
        }
    }
}
