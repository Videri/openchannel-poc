package com.videri.openchannelapp;

import android.util.JsonReader;

import com.loopj.android.http.HttpGet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.auth.UsernamePasswordCredentials;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.utils.URIBuilder;
import cz.msebera.android.httpclient.impl.auth.BasicScheme;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;

public class OpenChannelClientHttp {
    private static final String BASE_URL = "http://market.openchannel.io/v2";
    private static final String M_ID = "5b05c984e7f24542145811be";
    private static final String M_SECRECT = "TZ0OTnlZOt0Dp0NHJ3OZP1sh5VsulNk-pm28xBrqkHI";
    private static final String USER_ID = "1";

    private static HttpResponse get(HttpGet httpRequest) throws IOException {

        HttpResponse response = null;
        HttpClient httpclient = new DefaultHttpClient();
        try {
            //HttpGet httpRequest = new HttpGet(getAbsoluteUrl(api));
            httpRequest.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials(M_ID, M_SECRECT), "UTF-8", false));

            response = (HttpResponse) httpclient.execute(httpRequest);
        } finally {

        }

        return response;

    }

    public static JSONArray getApps() {

        JSONArray jsonArray = null;
        try {

            URIBuilder builder = new URIBuilder(getAbsoluteUrl("/apps"));
            builder.setParameter("query", "{'status.value':'approved'}");
            builder.setParameter("userId", USER_ID);

            HttpGet httpRequest = new HttpGet(builder.build());
            HttpResponse response = get(httpRequest);

            String responseStr = EntityUtils.toString(response.getEntity(), "UTF-8");

            if (responseStr != null) {
                JSONObject json = new JSONObject(responseStr);

                jsonArray = json.getJSONArray("list");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonArray;
    }

    public static JSONObject getApp(String appId) {

        JSONObject jsonObject = null;
        try {

            URIBuilder builder = new URIBuilder(getAbsoluteUrl("/apps/".concat(appId)));
            builder.setParameter("userId", USER_ID);

            HttpGet httpRequest = new HttpGet(builder.build());
            HttpResponse response = get(httpRequest);

            String responseStr = EntityUtils.toString(response.getEntity(), "UTF-8");

            if (responseStr != null) {
                jsonObject = new JSONObject(responseStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    private static String getAbsoluteUrl(String api) {

        return BASE_URL + api;
    }

}
