package ru.android_2019.citycam.webcams;

import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Константы для работы с Webcams API
 */
public final class Webcams {

    public static final String RAPID_API_KEY = "1b6b2bf409msh301a3b7eec5d75fp14ec10jsne9d8f9ba2511";

    private static final String BASE_URL = "https://webcamstravel.p.rapidapi.com/webcams";

    private static final String PATH_NEARBY = "list/nearby";

    private static final String PARAM_LANGUAGE = "lang";
    private static final String PARAM_SHOW = "show";

    private static final int RADIUS = 100;

    /**
     * Возвращает URL для выполнения запроса Webcams API для получения
     * информации о веб-камерах рядом с указанными координатами в формате JSON.
     */
    public static URL createNearbyUrl(int latitude, int longitude)
            throws MalformedURLException {
        Uri uri = Uri.parse(BASE_URL + "/" + PATH_NEARBY + "=" + latitude + "," + longitude + "," + RADIUS).buildUpon()
                .appendQueryParameter(PARAM_LANGUAGE, "ru")
                .appendQueryParameter(PARAM_SHOW, "webcams:image,location")
                .build();
        return new URL(uri.toString());
    }

    private Webcams() {}
}
