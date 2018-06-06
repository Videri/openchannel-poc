package com.videri.openchannelapp;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.Date;
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

    public static final String NBA_DEMO_PK = "com.videri.nba_demoapp";

    Button startService, stopService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // scheduler each 30 seconds
        final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleWithFixedDelay(new Runnable() {

            @Override
            public void run() {
                performTask();
            }
        }, 0, 1, TimeUnit.MINUTES);

    }

    private static final String BASE_URL = "http://market.openchannel.io/v2";
    private static final String M_ID = "5b05c984e7f24542145811be";
    private static final String M_SECRECT = "TZ0OTnlZOt0Dp0NHJ3OZP1sh5VsulNk-pm28xBrqkHI";

    public void performTask() {

        Log.d(TAG, "performTask... at: " + new Date().toLocaleString());

        try {

            // get list of apps
            JSONArray appListJson = OpenChannelClientHttp.getApps();

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
                    if ("active".equalsIgnoreCase(ownershipStatus)) {

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
                    } else if ("uninstalled".equalsIgnoreCase(ownershipStatus)) {

                        // just for debug
                        AndroidCmdUtils.silentUnInstall(NBA_DEMO_PK);
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
