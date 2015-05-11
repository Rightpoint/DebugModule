package com.raizlabs.android.debugmodule.url;

import android.content.Context;

import java.io.FileNotFoundException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Description: Utility methods for getting urls stored in the {@link UrlCritter}
 */
public class UrlFileUtils {

    static final String URL_FILE_NAME = "Urls";

    public static List<String> getUrls(Context context) {
        List<String> urls = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(context.openFileInput(URL_FILE_NAME));
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                urls.add(line);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return urls;
    }

    public static void saveUrls(Context context, List<String> urls) {
        try {
            OutputStreamWriter outputStream = new OutputStreamWriter(context.openFileOutput(URL_FILE_NAME, Context.MODE_PRIVATE));
            for (String url : urls) {
                outputStream.write(url + "\n");
            }
            outputStream.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public static void clearUrls(Context context) {
        context.deleteFile(URL_FILE_NAME);
    }
}
