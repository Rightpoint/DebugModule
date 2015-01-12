package com.raizlabs.android.debugmodule;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Author: andrewgrosner
 * Description: The adapter of critters to display on screen
 */
public class CritterAdapter extends BaseAdapter {

    private ArrayList<String> mCritterList;

    public CritterAdapter(Map<String, Critter> data) {
        mCritterList = new ArrayList<>(data.keySet());
    }

    @Override
    public int getCount() {
        return mCritterList != null ? mCritterList.size() : 0;
    }

    @Override
    public String getItem(int position) {
        return mCritterList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;
        if (convertView == null) {
            textView = new TextView(parent.getContext());
            int pad = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10,
                    parent.getResources().getDisplayMetrics());
            textView.setPadding(pad, pad, pad, pad);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(20);
        } else {
            textView = (TextView) convertView;
        }
        textView.setText(getItem(position));
        return textView;
    }
}
