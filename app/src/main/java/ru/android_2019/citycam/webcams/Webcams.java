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
    private static final String BASE_URL = "https://webcamstravel.p.rapidapi.com/webcams/";
    private static final String PARAM_LANG = "lang";
    private static final String PARAM_LANG_OP = "en";
    private static final String PARAM_SHOW = "show";
    private static final String PARAM_SHOW_OP = "webcams:image,location,title";
    private static final String PARAM_RADIUS = Integer.toString(150);
    private static final String PARAM_METHOD = "list/nearby";

    public static URL createNearbyUrl(double latitude, double longitude) throws MalformedURLException {
        Uri uri = Uri.parse(BASE_URL+PARAM_METHOD+"="+Double.toString(latitude)+","+Double.toString(longitude)+","+PARAM_RADIUS).buildUpon()
                .appendQueryParameter(PARAM_LANG,PARAM_LANG_OP).appendQueryParameter(PARAM_SHOW, PARAM_SHOW_OP).build();
        return new URL(uri.toString());
    }
    private Webcams() {}
}
