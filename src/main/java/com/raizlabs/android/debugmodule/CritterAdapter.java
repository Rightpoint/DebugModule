package com.raizlabs.android.debugmodule;

import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import com.raizlabs.android.connector.list.baseadapter.ListItemViewAdapter;
import com.raizlabs.android.core.DeviceUtils;

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

    @NonNull
    @Override
    public TextView createView(int position, ViewGroup parent) {
        TextView textView = super.createView(position, parent);
        int pad = (int) DeviceUtils.dp(10);
        textView.setPadding(pad, pad, pad, pad);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(20);
        return textView;
    }

    @Override
    public void setViewData(@NonNull TextView view, int position) {
        view.setText(getItem(position).getName());
    }
}
