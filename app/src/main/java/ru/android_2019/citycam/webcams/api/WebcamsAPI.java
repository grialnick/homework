package ru.android_2019.citycam.webcams.api;

import android.net.Uri;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Константы для работы с WebcamsAPI API
 */
public final class WebcamsAPI {

    private static final String KEY_HEADER_NAME = "X-RapidAPI-Key";
    private static final String RAPID_API_KEY = "9874e23cf2msh03710ad1d33c9bbp15fca7jsnee66756f171d";
    private static final String BASE_URL = "https://webcamstravel.p.rapidapi.com";
    private static final String PARAM_LAN = "lan";
    private static final String PARAM_SHOW = "show";
    private static final String LAN = "en";
    private static final String SHOW = "webcams:base,image,location";

    /**
     * Возвращает HttpURLConnection для выполнения запроса WebcamsAPI API для получения
     * информации о веб-камерах рядом с указанными координатами в формате JSON.
     */
    public static HttpURLConnection createNearbyUrl(double latitude,
                                                    double longitude,
                                                    double radius) throws IOException {

        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendPath("webcams")
                .appendPath("list")
                .appendEncodedPath("nearby=" + latitude + "," + longitude + "," + radius)
                .appendQueryParameter(PARAM_LAN, LAN)
                .appendQueryParameter(PARAM_SHOW, SHOW)
                .build();
        HttpURLConnection connection = (HttpURLConnection) new URL(uri.toString()).openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty(KEY_HEADER_NAME, RAPID_API_KEY);
        return connection;
    }

    private WebcamsAPI() {}
}
