package com.raizlabs.android.debugmodule.url;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.support.annotation.ArrayRes;
import android.support.annotation.LayoutRes;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Description: Stores a list of URLS in internal preferences. It allows you to add and test custom endpoints in an app,
 * and select one for the app to use. In your implementing application, call {@link #getCurrentUrl()}
 * for the beginning of the URL for any request and this module will (if enabled) allow you to swap the standard one
 * with one a tester can type in.
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


    private ViewHolder viewHolder;

    private String baseUrl;

    private List<UrlChangeListener> urlChangeListeners;

    private transient UrlManager urlManager;

    UrlAdapter adapter;

    /**
     * Constructs this class with the specified URL as a base url. We will allow the user to change URLS in the app.
     *
     * @param name          The name of this critter that will show up in the {@link com.raizlabs.android.debugmodule.Debugger} menu.
     * @param baseUrlString
     * @param context
     */
    public UrlCritter(String baseUrlString, Context context) {
        baseUrl = baseUrlString;
        urlManager = new UrlManager(context);
        urlChangeListeners = new ArrayList<>();
    }

    /**
     * Adds a URL to the singleton list of URL data.
     *
     * @param url     The url to add to list
     * @param context The context of application
     * @return This instance for chaining
     */
    @SuppressWarnings("unchecked")
    public UrlCritter addUrl(String url, Context context) {
        List<String> urls = urlManager.getUrls(context);
        if (!urls.contains(url)) {
            urls.add(url);
            urlManager.saveUrls(context, urls);
        }
        return this;
    }

    /**
     * Adds a list of urls to the saved list
     *
     * @param urls    The list of urls to add
     * @param context The context of application
     * @return This instance for chaining
     */
    public UrlCritter addUrlList(List<String> urls, Context context) {
        List<String> savedUrls = urlManager.getUrls(context);
        for (String url : urls) {
            if (!savedUrls.contains(url)) {
                savedUrls.add(url);
            }
        }
        urlManager.saveUrls(context, savedUrls);
        return this;
    }

    /**
     * Adds a varg of urls to the saved list
     *
     * @param context The context of application
     * @param urls    The varg of urls to add
     * @return This instance for chaining
     */
    public UrlCritter addUrls(Context context, String... urls) {
        List<String> savedUrls = urlManager.getUrls(context);
        for (String url : urls) {
            if (!savedUrls.contains(url)) {
                savedUrls.add(url);
            }
        }
        urlManager.saveUrls(context, savedUrls);
        return this;
    }

    /**
     * Adds a typed array of urls
     *
     * @param typedArray The typed array to use
     * @param context    The context of application
     * @return This instance for chaining
     */
    public UrlCritter addUrlTypedArray(TypedArray typedArray, Context context) {
        List<String> savedUrls = urlManager.getUrls(context);

        int length = typedArray.length();
        for (int i = 0; i < length; i++) {
            String url = typedArray.getString(i);
            if (!savedUrls.contains(url)) {
                savedUrls.add(url);
            }
        }
        urlManager.saveUrls(context, savedUrls);
        return this;
    }

    /**
     * Adds a typed array of urls from the specified resource
     *
     * @param typedArrayRes The typed array resource to use
     * @param context       The context of application
     * @return This instance for chaining
     */
    public UrlCritter addUrlTypedArray(@ArrayRes int typedArrayRes, Context context) {
        return addUrlTypedArray(context.getResources().obtainTypedArray(typedArrayRes), context);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.view_debug_module_url_critter;
    }

    @Override
    public void handleView(@LayoutRes int layoutResource, View view) {
        viewHolder = new ViewHolder(view);
        viewHolder.setupHolder(view.getContext());
    }

    @Override
    public void cleanup() {

    }

    /**
     * Clears all URL data from disk
     */
    public void clearData(Context context) {
        urlManager.clear(context);
    }

    /**
     * Registers the listener for when URL changes.
     *
     * @param urlChangeListener The listener that gets called back
     */
    public void registerUrlChangeListener(UrlChangeListener urlChangeListener) {
        if (!urlChangeListeners.contains(urlChangeListener)) {
            urlChangeListeners.add(urlChangeListener);
        }
    }

    /**
     * Un registers the listener
     *
     * @param urlChangeListener The listener to remove
     */
    public void removeUrlChangeListener(UrlChangeListener urlChangeListener) {
        urlChangeListeners.remove(urlChangeListener);
    }

    /**
     * @return the current url to use for this application.
     */
    public String getCurrentUrl() {
        return urlManager.getCurrentUrl(baseUrl);
    }

    public class ViewHolder {
        TextView urlTitleText;
        TextView urlSpinnerText;
        Spinner storedUrlSpinner;
        EditText addUrlOption;
        Button saveUrlOption;
        Button clearUrlsOption;

        public ViewHolder(View view) {
            urlTitleText = (TextView) view.findViewWithTag(R.id.urlTitleText);
            urlSpinnerText = (TextView) view.findViewById(R.id.urlSpinnerText);
            storedUrlSpinner = (Spinner) view.findViewById(R.id.storedUrlSpinner);
            addUrlOption = (EditText) view.findViewById(R.id.addUrlOption);
            saveUrlOption = (Button) view.findViewById(R.id.saveUrlOption);
            clearUrlsOption = (Button) view.findViewById(R.id.clearData);
        }

        void setupHolder(Context context) {
            adapter = new UrlAdapter(context);

            // prefill with current base url option
            addUrlOption.setText(adapter.getItem(adapter.mUrls.indexOf(getCurrentUrl())));
            storedUrlSpinner.setAdapter(adapter);
            storedUrlSpinner.setSelection(adapter.mUrls.indexOf(getCurrentUrl()));
            storedUrlSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                boolean isFirst = true;
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(!isFirst) {
                        String url = adapter.getItem(position);
                        urlManager.setCurrentUrl(url);
                        Toast.makeText(view.getContext(), "Now using " + url, Toast.LENGTH_SHORT).show();

                        if (urlChangeListeners != null) {
                            for (UrlChangeListener listener : urlChangeListeners) {
                                listener.onUrlChanged(url);
                            }
                        }
                    } else {
                        isFirst = false;
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
                    if (!Patterns.WEB_URL.matcher(url).matches()) {
                        Toast.makeText(v.getContext(), "Please enter a valid base url", Toast.LENGTH_SHORT).show();
                    } else if (!url.isEmpty()) {
                        if(!urlManager.getUrls(v.getContext()).contains(url)) {
                            Toast.makeText(v.getContext(), "Added " + url + " to list", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(v.getContext(), "Duplicate URL entered", Toast.LENGTH_SHORT).show();
                        }
                        addUrl(url, v.getContext());
                        adapter.refresh(v.getContext());
                    }
                }
            });

            clearUrlsOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext())
                            .setTitle("Are you sure you want to clear all custom urls?")
                            .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    urlManager.clear(v.getContext());
                                    adapter.refresh(v.getContext());
                                }
                            })
                            .setNegativeButton("no", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    builder.show();
                }
            });
        }
    }

    /**
     * Retrieves the list of Urls from the corresponding list of urls.
     */
    class UrlAdapter extends BaseAdapter {

        List<String> mUrls;

        @SuppressWarnings("unchecked")
        public UrlAdapter(Context context) {
            addUrl(baseUrl, context);
            mUrls = urlManager.getUrls(context);
        }

        public void refresh(Context context) {
            mUrls = urlManager.getUrls(context);
            notifyDataSetChanged();
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
