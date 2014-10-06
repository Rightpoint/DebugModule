package com.raizlabs.android.debugmodule;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Author: andrewgrosner
 * Contributors: { }
 * Description:
 */
public class DebugCritterFragment extends Fragment {

    static final String ARGUMENT_CRITTER = "debug_critter";

    public static DebugCritterFragment newInstance(Critter critter) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARGUMENT_CRITTER, critter);
        DebugCritterFragment debugCritterFragment = new DebugCritterFragment();
        debugCritterFragment.setArguments(bundle);
        return debugCritterFragment;
    }

    private Critter mCritter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCritter = (Critter) getArguments().getSerializable(ARGUMENT_CRITTER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(mCritter.getLayoutResId(), container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCritter.handleView(view);
    }


}
