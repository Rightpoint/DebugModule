package com.raizlabs.android.debugmodule;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Description: The main fragment screen that displays a list of critters that a tester can click on
 * and interact with.
 */
public class DebugMenuFragment extends Fragment {

    private static final String ARG_CONTAINER_RESOURCE = "DebugMenuFragment:Container";

    public static DebugMenuFragment newInstance(@IdRes int layoutResId) {
        DebugMenuFragment debugMenuFragment = new DebugMenuFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CONTAINER_RESOURCE, layoutResId);
        debugMenuFragment.setArguments(args);
        return debugMenuFragment;
    }

    private CritterAdapter mAdapter;

    private int getContainerResourceId() {
        return getArguments().getInt(ARG_CONTAINER_RESOURCE, R.id.view_debug_module_menu_drawer);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new CritterAdapter(Debugger.getInstance().getCritterMap());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_debug_module_menu, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView listView = (ListView) view.findViewById(R.id.listView);

        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String key = mAdapter.getItem(position);
                DebugCritterFragment critterFragment = DebugCritterFragment.newInstance(key, getContainerResourceId());
                getFragmentManager().beginTransaction()
                        .replace(getContainerResourceId(), critterFragment)
                        .addToBackStack(null).commit();
            }
        });

        Debugger.getInstance().registerCritterRemoveListener(mRemoveListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Debugger.getInstance().unregisterCritterRemoveListener(mRemoveListener);
    }

    private final Debugger.CritterRemoveListener mRemoveListener = new Debugger.CritterRemoveListener() {
        @Override
        public void onCritterRemoved(Critter critter) {
            mAdapter.remove(Debugger.getInstance().getCritterName(critter));
            mAdapter.notifyDataSetChanged();
        }
    };
}
