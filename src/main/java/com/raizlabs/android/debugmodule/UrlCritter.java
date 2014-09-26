package com.raizlabs.android.debugmodule;

import android.support.annotation.NonNull;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.raizlabs.android.connector.list.baseadapter.ListItemViewAdapter;
import com.raizlabs.android.core.DeviceUtils;
import com.raizlabs.android.core.StaticPrefs;
import com.raizlabs.android.singleton.Singleton;

import java.util.ArrayList;

/**
 * Author: andrewgrosner
 * Contributors: { }
 * Description:
 */
public class UrlCritter implements Critter {

    static final String PREF_CURRENT_URL = "debugger_pref_current_url";

    static final String KEY_URL_SINGLETON_LIST = "debugger_url_singleton_list";

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

    /**
     * Constructs this class with the specified URL as a base url. We will allow the user to change URLS in the app.
     *
     * @param baseUrlString
     */
    public UrlCritter(String baseUrlString) {
        mBaseUrl = baseUrlString;
    }

    /**
     * Adds a URL to the singleton list of URL data.
     * @param url
     * @return
     */
    @SuppressWarnings("unchecked")
    public UrlCritter addUrl(String url) {
        Singleton<ArrayList> urlSingleton = new Singleton<ArrayList>(KEY_URL_SINGLETON_LIST, ArrayList.class, true);
        ArrayList<String> urls = urlSingleton.getInstance();
        if (!urls.contains(url)) {
            urls.add(url);
            urlSingleton.save();
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
        mViewHolder.setupHolder();
    }

    @Override
    public String getName() {
        return "Select URL Endpoint";
    }

    public void setUrlChangeListener(UrlChangeListener mUrlChangeListener) {
        this.mUrlChangeListener = mUrlChangeListener;
    }

    /**
     * Returns the base url to use for this application.
     *
     * @return
     */
    public String getBaseUrl() {
        return StaticPrefs.getInstance().getString(PREF_CURRENT_URL, mBaseUrl);
    }

    @SuppressWarnings("unchecked")
    ArrayList<String> getSingletonList() {
        Singleton<ArrayList> urlSingleton = new Singleton<ArrayList>(KEY_URL_SINGLETON_LIST, ArrayList.class, true);
        ArrayList<String> urls = urlSingleton.getInstance();
        return urls;
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

        void setupHolder() {
            final UrlAdapter urlAdapter = new UrlAdapter();
            storedUrlSpinner.setAdapter(urlAdapter);
            storedUrlSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String url = urlAdapter.getItem(position);
                    StaticPrefs.getInstance().putString(PREF_CURRENT_URL, url);
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
                        addUrl(url);
                        Toast.makeText(v.getContext(), "Added " + url + " to list", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    /**
     * Retrieves the list of Urls from the corresponding {@link com.raizlabs.android.singleton.Singleton}
     */
    class UrlAdapter extends ListItemViewAdapter<String, TextView> {

        @SuppressWarnings("unchecked")
        public UrlAdapter() {
            super(TextView.class);
            addUrl(mBaseUrl);
            setData(getSingletonList());
        }

        @NonNull
        @Override
        public TextView createView(int position, ViewGroup parent) {
            TextView textView = super.createView(position, parent);
            int pad = (int) DeviceUtils.dp(10);
            textView.setPadding(pad, pad, pad, pad);
            return textView;
        }

        @Override
        public void setViewData(@NonNull TextView view, int position) {
            view.setText(getItem(position));
        }
    }
}
