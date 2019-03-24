package ru.android_2019.citycam.webcams;

import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Константы для работы с Webcams API
 */
public final class Webcams {

    private static final String BASE_URL = "https://webcamstravel.p.rapidapi.com/webcams/";

    private static final String METHOD_NEARBY = "list/nearby=";
    private static final String REQUEST_PARAMS = "?lang=ru&show=webcams:category,image,location,statistics,url";
    private static final String RADIUS = "25";

    /**
     * Возвращает URL для выполнения запроса Webcams API для получения
     * информации о веб-камерах рядом с указанными координатами в формате JSON.
     */
    public static URL createNearbyUrl(double latitude, double longitude)
            throws MalformedURLException {
        Uri uri = Uri.parse(BASE_URL + METHOD_NEARBY + Double.toString(latitude) +
                "," + Double.toString(longitude) +
                "," + RADIUS + REQUEST_PARAMS)
                .buildUpon()
                .build();
        return new URL(uri.toString());
    }

    private Webcams() {}
}
