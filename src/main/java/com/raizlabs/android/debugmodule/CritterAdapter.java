package com.raizlabs.android.debugmodule;

import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
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

    @NonNull
    @Override
    public TextView createView(int position, ViewGroup parent) {
        TextView textView = super.createView(position, parent);
        int pad = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10,
                parent.getResources().getDisplayMetrics());
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
