package com.videri.openchannelapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.webkit.URLUtil;

import com.loopj.android.http.HttpGet;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.entity.BufferedHttpEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

public class DownloadAndInstall extends AsyncTask<String, Integer, Boolean> {

    private Context context;
    public DownloadAndInstall(Context context){
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(String... strings) {

        Boolean flag = false;

        try {

            URL url = new URL(strings[0]);
            String fileName = URLUtil.guessFileName(url.toString(), null, null);

            HttpGet httpRequest = new HttpGet(url.toURI());
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = (HttpResponse) httpclient.execute(httpRequest);

            HttpEntity entity = response.getEntity();
            BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
            InputStream is = bufHttpEntity.getContent();


//            HttpURLConnection c = (HttpURLConnection) url.openConnection();
//            c.setRequestMethod("GET");
//            c.setDoOutput(true);
//            c.connect();

            String PATH = Environment.getExternalStorageDirectory() + "/Download/";
            File folder = new File(PATH);

            if (!folder.exists()) {
                folder.mkdirs();
            }

            File outputFile = new File(folder, fileName);

            if(outputFile.exists()){
                outputFile.delete();
            }

            FileOutputStream fos = new FileOutputStream(outputFile);
            //InputStream is = c.getInputStream();

            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len1);
            }

            fos.flush();
            fos.close();
            is.close();

            normalInstall(outputFile.getAbsolutePath());

            flag = true;
        } catch (Exception e) {
            Log.e("DownloadAndInstall", "Update Error: " + e.getMessage());
            flag = false;
        }

        return flag;

    }


    public void normalInstall(String location) {

        Log.i("DownloadAndInstall", "install...");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(location)),"application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        Log.i("DownloadAndInstall", "install done.");
    }

    private void silientInstall(String filename) {

        Log.i("DownloadAndInstall", "install...");
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

        Log.i("DownloadAndInstall", "install done.");
    }

    public static String insertEscape(String text) {
        return text.replace(" ", "\\ ");
    }

}
