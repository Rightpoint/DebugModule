package com.raizlabs.android.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.raizlabs.android.app.dbflow.ExampleModel;
import com.raizlabs.android.app.dbflow.LargeExampleModel;
import com.raizlabs.android.debugmodule.Debugger;
import com.raizlabs.android.debugmodule.preference.PreferenceBuilder;
import com.raizlabs.android.debugmodule.preference.PreferenceChangeListener;
import com.raizlabs.android.debugmodule.preference.PreferenceCritter;

import java.util.Random;

/**
 * Description:
 */
public class MainFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceCritter preferenceCritter = getPreferenceCritter();
        preferenceCritter
                .addPreference(new PreferenceBuilder<String>(getActivity())
                                       .prefKey("preference_test_name")
                                       .prefType(String.class)
                                       .titleName("String example"))
                .addPreference(new PreferenceBuilder<Boolean>(getActivity())
                                       .prefKey("preference_boolean")
                                       .prefType(Boolean.class)
                                       .titleName("Boolean example"))
                .addPreference(new PreferenceBuilder<Long>(getActivity())
                                       .prefKey("preference_long")
                                       .prefType(Long.class)
                                       .titleName("Long example"))
                .addPreference(new PreferenceBuilder<Float>(getActivity())
                                       .prefKey("preference_float")
                                       .prefType(Float.class)
                                       .titleName("Float example"))
                .addPreference(new PreferenceBuilder<Integer>(getActivity())
                                       .prefKey("preference_int")
                                       .prefType(Integer.class)
                                       .titleName("Integer example"));
        preferenceCritter.registerPreferenceChangeListener(mPreferenceChangeListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.openDrawer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Debugger.getInstance().getDebugDrawer().openDrawer(Gravity.RIGHT);
            }
        });

        view.findViewById(R.id.addItem).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExampleModel model = new ExampleModel();
                model.name = "Test";
                model.duble = 0.5d;
                model.floatie = 0.75f;
                model.isSet = new Random(System.currentTimeMillis()).nextBoolean();
                model.save(false);

                LargeExampleModel largeExampleModel = new LargeExampleModel();
                largeExampleModel.name = "Test";
                largeExampleModel.duble = 0.5d;
                largeExampleModel.floatie = 0.75f;
                largeExampleModel.isSet = new Random(System.currentTimeMillis()).nextBoolean();
                byte[] buffer = new byte[new Random(System.currentTimeMillis()).nextInt(20)];
                new Random(System.currentTimeMillis()).nextBytes(buffer);
                largeExampleModel.anotherName = new String(buffer);
                largeExampleModel.anotherName2 = "wwowowowo";
                largeExampleModel.save(false);
            }
        });

        view.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), DebugMenuActivity.class));
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getPreferenceCritter().unregisterPreferenceChangeListener(mPreferenceChangeListener);
    }

    protected PreferenceCritter getPreferenceCritter() {
        return (PreferenceCritter) Debugger.getInstance().getCritter("PreferenceCritter");
    }

    private final PreferenceChangeListener mPreferenceChangeListener = new PreferenceChangeListener() {
        @Override
        public void onPreferenceChanged(String preferenceKey, Object preferenceValue) {
            Toast.makeText(getActivity(), preferenceKey
                                              + " changed to: " + String.valueOf(preferenceValue), Toast.LENGTH_SHORT).show();
        }
    };
}
