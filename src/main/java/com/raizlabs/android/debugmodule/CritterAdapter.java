package com.raizlabs.android.debugmodule;

import android.support.annotation.NonNull;
import android.widget.TextView;

import com.raizlabs.android.connector.list.baseadapter.ListItemViewAdapter;

import java.util.List;

/**
 * Author: andrewgrosner
 * Contributors: { }
 * Description:
 */
public class CritterAdapter extends ListItemViewAdapter<Critter, TextView> {

    public CritterAdapter(List<Critter> data) {
        super(TextView.class, data);
    }

    @Override
    public void setViewData(@NonNull TextView view, int position) {
        view.setText(getItem(position).getName());
    }
}
