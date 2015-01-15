package com.raizlabs.android.debugmodule;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Description: The fragment that contains the {@link android.view.View} defined by a {@link Critter#getLayoutResId()}.
 *
 * {@link com.raizlabs.android.debugmodule.Critter#handleView(android.view.View)} will be called in {@link #onViewCreated(android.view.View, android.os.Bundle)}
 */
public class DebugCritterFragment extends Fragment {

    static final String ARGUMENT_CRITTER = "debug_critter";

    public static DebugCritterFragment newInstance(String name) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARGUMENT_CRITTER, name);
        DebugCritterFragment debugCritterFragment = new DebugCritterFragment();
        debugCritterFragment.setArguments(bundle);
        return debugCritterFragment;
    }

    private Critter mCritter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCritter = Debugger.getInstance().getCritter(getArguments().getString(ARGUMENT_CRITTER, ""));
        if(mCritter == null) {
            throw new IllegalStateException("Critter passed no longer exists. Please reload the screen");
        }
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
