package com.raizlabs.android.debugmodule;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.raizlabs.android.connector.list.baseadapter.ListItemViewAdapter;
import com.raizlabs.android.singleton.Singleton;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: andrewgrosner
 * Contributors: { }
 * Description: Stores a list of URLS in internal app storage. It allows you to add and test custom endpoints in an app, and select one for the app to use.
 * In your implementing application, call {@link #getBaseUrl()} for the beginning of the URL for any request and this module will (if enabled) allow you to swap the standard one
 * with one the user can type in.
 */
public class UrlCritter implements Critter {

    static List<String> URL_LIST;

    public static List<String> getURL_LIST() {
        if(URL_LIST == null) {

        }

        return URL_LIST;
    }

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

    private SharedPreferences mPrefs;

    /**
     * Constructs this class with the specified URL as a base url. We will allow the user to change URLS in the app.
     *
     * @param baseUrlString
     */
    public UrlCritter(String baseUrlString, Context context) {
        mBaseUrl = baseUrlString;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
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

    /**
     * Clears all URL data from disk
     */
    public void clearData() {
        Singleton<ArrayList> urlSingleton = new Singleton<ArrayList>(KEY_URL_SINGLETON_LIST, ArrayList.class, true);
        urlSingleton.delete();
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
        return mPrefs.getString(PREF_CURRENT_URL, mBaseUrl);
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
                    mPrefs.edit().putString(PREF_CURRENT_URL, url).apply();
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
     * Retrieves the list of Urls from the corresponding list of urls.
     */
    class UrlAdapter extends BaseAdapter {

        private List<String> mUrls;

        @SuppressWarnings("unchecked")
        public UrlAdapter() {
            addUrl(mBaseUrl);
            mUrls = getSingletonList();
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
