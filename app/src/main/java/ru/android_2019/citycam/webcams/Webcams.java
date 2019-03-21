package ru.android_2019.citycam.webcams;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

/**
 * Константы для работы с Webcams API
 */
public final class Webcams {

    // Зарегистрируйтесь на https://rapidapi.com/webcams.travel/api/webcams-travel
    // и вставьте сюда ваш devid
    private static final String DEV_ID_KEY = "X-RapidAPI-Key";
    private static final String DEV_ID = "INSERT YOUR ID HERE";

    private static final String BASE_URL = "https://webcamstravel.p.rapidapi.com";
    private static final String NEARBY_PATH = "webcams/list/nearby=%f,%f,%d/limit=%d";

    private static final int DEFAULT_RADIUS = 250;
    private static final int DEFAULT_LIMIT = 1;

    /**
     * Возвращает URL для выполнения запроса Webcams API для получения
     * информации о веб-камерах рядом с указанными координатами в формате JSON.
     */
    public static HttpURLConnection createNearbyUrlConnection(double latitude, double longitude)
            throws IOException {
        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendEncodedPath(String.format(Locale.ENGLISH, NEARBY_PATH, latitude, longitude, DEFAULT_RADIUS, DEFAULT_LIMIT))
                .appendQueryParameter("lang", "en")
                .appendQueryParameter("show", "webcams:image")
                .build();
        Log.d("Request connection", uri.toString());
        HttpURLConnection connection = (HttpURLConnection) new URL(uri.toString()).openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty(DEV_ID_KEY, DEV_ID);
        return connection;
    }
}
