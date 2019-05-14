package ru.android_2019.citycam.webcams;

import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Константы для работы с Webcams API
 */
public final class Webcams {

    // Зарегистрируйтесь на http://ru.webcams.travel/developers/
    // и вставьте сюда ваш devid

    private static final long PARAM_BASE_RADIUS = 5;
    private static final String HEADER_API_KEY = "X-RapidAPI-Key";
    private static final String HEADER_API_VALUE = "4a28cfb056mshfb74e8ac73d240ap19719fjsn186d275cee30";
    private static final String BASE_URL = "https://webcamstravel.p.rapidapi.com/webcams/list/nearby";
    private static final String LOCAL = "ru";
    private static final String PARAM_LOCAL = "lang";
    private static final String PARAM_SHOW = "show";
    private static final String BASE_SHOW_VALUE = "webcams:image,category,location";

    /**
     * Возвращает URL для выполнения запроса Webcams API для получения
     * информации о веб-камерах рядом с указанными координатами в формате JSON.
     */
    public static URL createNearbyUrl(double latitude, double longitude)
            throws MalformedURLException {
        StringBuilder baseUrl = new StringBuilder(BASE_URL)
                .append("=")
                .append(latitude)
                .append(",")
                .append(longitude)
                .append(",")
                .append(PARAM_BASE_RADIUS);
        Uri uri = Uri.parse(baseUrl.toString()).buildUpon()
                .appendQueryParameter(PARAM_LOCAL, LOCAL)
                .appendQueryParameter(PARAM_SHOW, BASE_SHOW_VALUE)
                .build();
        return new URL(uri.toString());
    }

    public static String getHeaderApiKey() {
        return HEADER_API_KEY;
    }

    public static String getHeaderApiValue() {
        return HEADER_API_VALUE;
    }

    private Webcams() {
    }
}
