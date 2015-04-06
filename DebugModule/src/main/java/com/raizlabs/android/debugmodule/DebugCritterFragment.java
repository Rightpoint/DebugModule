package com.raizlabs.android.debugmodule;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Description: The fragment that contains the {@link android.view.View} defined by a {@link Critter#getLayoutResId()}.
 *
 * {@link Critter#handleView(int, View)} will be called in {@link #onViewCreated(View, Bundle)}
 */
public class DebugCritterFragment extends Fragment {

    static final String ARGUMENT_CRITTER = "debug_critter";
    static final String ARGUMENT_LAYOUT_RES = "layout_id";

    public static DebugCritterFragment newInstance(String name, @LayoutRes int container) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARGUMENT_CRITTER, name);
        bundle.putInt(ARGUMENT_LAYOUT_RES, container);
        DebugCritterFragment debugCritterFragment = new DebugCritterFragment();
        debugCritterFragment.setArguments(bundle);
        return debugCritterFragment;
    }

    private Critter mCritter;

    private int layoutRes;

    private String getTitle() {
        return getArguments().getString(ARGUMENT_CRITTER, "");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String critterName = getTitle();
        mCritter = Debugger.getInstance().getCritter(critterName);
        if(mCritter == null) {
            throw new IllegalStateException("Critter passed no longer exists. Please reload the screen");
        }

        layoutRes = getArguments().getInt(ARGUMENT_LAYOUT_RES, -1);

        getActivity().setTitle(critterName);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getTitle());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(mCritter.getLayoutResId(), container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCritter.handleView(layoutRes, view);

        Debugger.getInstance().registerCritterRemoveListener(mRemoveListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mCritter.cleanup();
        Debugger.getInstance().unregisterCritterRemoveListener(mRemoveListener);
    }

    private final Debugger.CritterRemoveListener mRemoveListener = new Debugger.CritterRemoveListener() {
        @Override
        public void onCritterRemoved(Critter critter) {
            if(mCritter.equals(critter) && getActivity() != null && !getActivity().isFinishing()) {
                // no longer valid we exit this screen
                mCritter.cleanup();
                getActivity().getSupportFragmentManager().popBackStack();
            }
        }
    };


}
