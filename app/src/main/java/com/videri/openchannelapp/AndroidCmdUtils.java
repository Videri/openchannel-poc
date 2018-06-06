package com.videri.openchannelapp;

import android.os.Environment;
import android.util.Log;

import java.io.File;

public class AndroidCmdUtils {

    private static String TAG = "AndroidCmdUtils";

    public static void silentInstall(String filename) {

        Log.i(TAG, "silentInstall...");

        File file = new File(filename);
        if(file.exists()){
            try {
                final String command = "pm install -r " + file.getAbsolutePath();
                Process proc = Runtime.getRuntime().exec(new String[] { "su", "-c", command });
                proc.waitFor();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Log.i(TAG, "silentInstall done.");
    }

    public static void silentUnInstall(String packageName) {

        Log.i(TAG, "silentUnInstall...");
        try {

            final String command = "pm uninstall " + packageName;
            Process proc = Runtime.getRuntime().exec(new String[] { "su", "-c", command });
            proc.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.i(TAG, "silentUnInstall done.");
    }


}
