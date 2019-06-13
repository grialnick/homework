package ru.android_2019.citycam.webcams;

import java.io.IOException;
import java.net.URL;
import java.net.HttpURLConnection;
import javax.net.ssl.HttpsURLConnection;
import java.io.InputStream;
import android.util.Log;

public class HttpConnect {
    private HttpConnect() {
    }

    public static InputStream httpUrlConnection(URL url) {
        HttpURLConnection Connection = null;
        InputStream stream = null;
        try {
            Connection = (HttpURLConnection) url.openConnection();
            Connection.setReadTimeout(2000);
            Connection.setConnectTimeout(2000);
            Connection.setDoInput(true);
            Connection.setRequestMethod("GET");
            Connection.setRequestProperty("X-RapidAPI-KEY", "8a909f08b6mshb1dc9615319b280p12accdjsn57333583b8fd");
            Connection.connect();
            if (Connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP error");
            }
            Log.w(TAG, "Cannect for http: " + Connection.getURL());
            stream = Connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stream;
    }

    public static InputStream httpsUrlConnection(URL url) {
        HttpsURLConnection Connection = null;
        InputStream stream = null;
        try {
            Connection = (HttpsURLConnection) url.openConnection();
            Connection.setReadTimeout(2000);
            Connection.setConnectTimeout(2000);
            Connection.setDoInput(true);
            Connection.setRequestMethod("GET");
            if (Connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP error");
            }
            Log.w(TAG, "Cannect for http: " + Connection.getURL());
            stream = Connection.getInputStream();
            Log.w(TAG, "Connect for https: " + Connection.getURL());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stream;
    }

    private static final String TAG = "CityCam";
}

