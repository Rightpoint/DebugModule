package com.raizlabs.android.debugmodule.url;

import android.content.Context;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.raizlabs.android.debugmodule.Critter;
import com.raizlabs.android.debugmodule.R;

import java.util.List;

/**
 * Author: andrewgrosner
 * Contributors: { }
 * Description: Stores a list of URLS in internal app storage. It allows you to add and test custom endpoints in an app, and select one for the app to use.
 * In your implementing application, call {@link #getBaseUrl()} for the beginning of the URL for any request and this module will (if enabled) allow you to swap the standard one
 * with one the user can type in.
 */
public class UrlCritter implements Critter {

    /**
     * Is called when a URL has changed
     */
    public interface UrlChangeListener {

        /**
         * Called when the user chooses a different url endpoint.
         *
         * @param url The new URL to use
         */
        void onUrlChanged(String url);
    }


    private ViewHolder mViewHolder;

    private String mBaseUrl;

    private UrlChangeListener mUrlChangeListener;

    private UrlManager mPrefs;

    /**
     * Constructs this class with the specified URL as a base url. We will allow the user to change URLS in the app.
     *
     * @param baseUrlString
     */
    public UrlCritter(String baseUrlString, Context context) {
        mBaseUrl = baseUrlString;
        mPrefs = new UrlManager(context);
    }

    /**
     * Adds a URL to the singleton list of URL data.
     * @param url
     * @return
     */
    @SuppressWarnings("unchecked")
    public UrlCritter addUrl(String url, Context context) {
        List<String> urls = mPrefs.getUrls(context);
        if(!urls.contains(url)) {
            urls.add(url);
            mPrefs.saveUrls(context, urls);
        }
        return this;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.critter_url;
    }

    @Override
    public void handleView(View view) {
        mViewHolder = new ViewHolder(view);
        mViewHolder.setupHolder(view.getContext());
    }

    /**
     * Clears all URL data from disk
     */
    public void clearData() {
        mPrefs.clear();
    }

    @Override
    public String getName() {
        return "Select URL Endpoint";
    }

    public void setUrlChangeListener(UrlChangeListener mUrlChangeListener) {
        this.mUrlChangeListener = mUrlChangeListener;
    }

    /**
     * @return the base url to use for this application.
     */
    public String getBaseUrl() {
        return mPrefs.getCurrentUrl(mBaseUrl);
    }

    public class ViewHolder {
        TextView urlTitleText;
        TextView urlSpinnerText;
        Spinner storedUrlSpinner;
        EditText addUrlOption;
        Button saveUrlOption;

        public ViewHolder(View view) {
            urlTitleText = (TextView) view.findViewWithTag(R.id.urlTitleText);
            urlSpinnerText = (TextView) view.findViewById(R.id.urlSpinnerText);
            storedUrlSpinner = (Spinner) view.findViewById(R.id.storedUrlSpinner);
            addUrlOption = (EditText) view.findViewById(R.id.addUrlOption);
            saveUrlOption = (Button) view.findViewById(R.id.saveUrlOption);
        }

        void setupHolder(Context context) {
            final UrlAdapter urlAdapter = new UrlAdapter(context);
            storedUrlSpinner.setAdapter(urlAdapter);
            storedUrlSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String url = urlAdapter.getItem(position);
                    mPrefs.setCurrentUrl(url);
                    Toast.makeText(view.getContext(), "Now using " + url, Toast.LENGTH_SHORT).show();

                    if (mUrlChangeListener != null) {
                        mUrlChangeListener.onUrlChanged(url);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            saveUrlOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = addUrlOption.getText().toString();
                    if (!Patterns.DOMAIN_NAME.matcher(url).matches()) {
                        Toast.makeText(v.getContext(), "Please enter a valid base url", Toast.LENGTH_SHORT).show();
                    } else if (!url.isEmpty()) {
                        addUrl(url, v.getContext());
                        Toast.makeText(v.getContext(), "Added " + url + " to list", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    /**
     * Retrieves the list of Urls from the corresponding list of urls.
     */
    class UrlAdapter extends BaseAdapter {

        private List<String> mUrls;

        @SuppressWarnings("unchecked")
        public UrlAdapter(Context context) {
            addUrl(mBaseUrl, context);
            mUrls = mPrefs.getUrls(context);
        }

        @Override
        public int getCount() {
            return mUrls != null ? mUrls.size() : 0;
        }

        @Override
        public String getItem(int position) {
            return mUrls.get(position);
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
            } else {
                textView = (TextView) convertView;
            }
            textView.setText(getItem(position));
            return textView;
        }
    }
}
