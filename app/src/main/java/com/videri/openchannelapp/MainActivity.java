package com.videri.openchannelapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cz.msebera.android.httpclient.Header;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "AppDebug";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // scheduler each 30 seconds
//        final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
//        service.scheduleWithFixedDelay(new Runnable() {
//
//            @Override
//            public void run() {
//                performTask();
//            }
//        }, 0, 30, TimeUnit.MINUTES);

        performTask();
    }

    public void performTask() {

        Log.d(TAG, "performTask...");

        RequestParams requestParams = new RequestParams();
        requestParams.add("query", "{'status.value':'approved'}");
        requestParams.add("userId", "1");

        OpenChannelClient.get("/apps", requestParams, new JsonHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d(TAG, errorResponse.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Log.d(TAG, response.toString());

                try {

                    // get list of apps
                    JSONArray appListJson = response.getJSONArray("list");

                    // for each app
                    for (int i = 0; i < appListJson.length(); i++) {

                        JSONObject appJson = appListJson.getJSONObject(i);

                        // get the ownership
                        JSONObject ownershipJson = appJson.getJSONObject("ownership");

                        if (ownershipJson != null) {

                            // get ownershipStatus
                            String ownershipStatus = ownershipJson.getString("ownershipStatus");

                            Log.d(TAG, "ownershipStatus: " + ownershipStatus);

                            // if status is installed
                            if ("uninstalled".equalsIgnoreCase(ownershipStatus)) {

                                // get custom data
                                JSONObject customData = appJson.getJSONObject("customData");

                                if (customData != null) {
                                    JSONArray filesJson = customData.getJSONArray("files");

                                    if (filesJson.length() > 0) {

                                        // get the apk file
                                        String apkFile = filesJson.getString(0);

                                        if (!apkFile.startsWith("http:")) {
                                            apkFile = "http:".concat(apkFile);
                                        }
                                        Log.d(TAG, "apkFile: " + apkFile);

                                        new DownloadAndInstall(getApplicationContext()).execute(apkFile);
                                    }
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, e.getMessage());

                }


            }
        });
    }
}
