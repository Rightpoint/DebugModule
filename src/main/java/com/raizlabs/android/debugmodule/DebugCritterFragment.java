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

    private Critter mCritter;

    public void setCritter(Critter critter) {
        mCritter = critter;
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
