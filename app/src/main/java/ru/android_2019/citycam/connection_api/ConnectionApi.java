package ru.android_2019.citycam.connection_api;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConnectionApi {
    private static final String RAPID_API_KEY = "1034cd9cefmsh9b427c15acd1f86p1bde9bjsn8ff22fa8c7a8";

    private static final String KEY_HEADER_NAME = "X-RapidAPI-Key";

    public static HttpURLConnection getConnection (URL url) {

        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(3000);
            connection.setConnectTimeout(3000);
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.setRequestProperty(KEY_HEADER_NAME, RAPID_API_KEY);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return connection;
    }

    private ConnectionApi() {

    }
}
