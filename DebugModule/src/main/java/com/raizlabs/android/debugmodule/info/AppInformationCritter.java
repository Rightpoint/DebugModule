package com.raizlabs.android.debugmodule.info;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.TextView;

import com.raizlabs.android.debugmodule.Critter;
import com.raizlabs.android.debugmodule.R;

/**
 * Description:
 */
public class AppInformationCritter implements Critter {

    private final String flavorName;

    private final String buildType;

    /**
     * Constructs instance with flavor and build type. These are not accessible from within the library
     * so they are necessary to be passed in
     *
     * @param flavorName The name of the flavor from BuildConfig
     * @param buildType  The type of the build from BuildConfig
     */
    public AppInformationCritter(String flavorName, String buildType) {
        this.flavorName = flavorName;
        this.buildType = buildType;
    }


    @Override
    public int getLayoutResId() {
        return R.layout.fragment_app_information_view;
    }

    @Override
    public void handleView(View view) {
        TextView appinformation = (TextView) view.findViewById(R.id.view_debug_module_app_information);

        Context context = view.getContext();

        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            StringBuilder builder = new StringBuilder()
                    .append("App Name: ").append(context.getPackageManager().getApplicationLabel(context.getApplicationInfo())).append("\n");
            builder.append("App Version: ").append(info.versionName).append(":").append(info.versionCode).append("\n");
            builder.append("Application ID: ").append(info.packageName).append("\n");
            builder.append("Flavor: ").append(flavorName).append("\n");
            builder.append("Build Type: ").append(buildType).append("\n");
            appinformation.setText(builder.toString());
        } catch (PackageManager.NameNotFoundException e) {
            //ignored
        }

    }

}
