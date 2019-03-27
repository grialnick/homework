package ru.android_2019.citycam.webcams;

import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URL;

public final class Webcams {

    private static final String BASE_URL = "https://webcamstravel.p.rapidapi.com/webcams/";

    private static final String PARAM_LANG = "lang";
    private static final String LANG = "en";
    private static final String PARAM_SHOW = "show";
    private static final String SHOW = "webcams:image,location,country,status,statistics";

    private static final String METHOD_NEARBY = "list/nearby";
    private static final String RADIUS = "50";

    public static URL createNearbyUrl(double latitude, double longitude)
            throws MalformedURLException {
        Uri uri = Uri.parse(BASE_URL+METHOD_NEARBY+"="+Double.toString(latitude)+","+Double.toString(longitude)+","+RADIUS).buildUpon()
                .appendQueryParameter(PARAM_LANG, LANG)
                .appendQueryParameter(PARAM_SHOW, SHOW)
                .build();
        return new URL(uri.toString());
    }
    private Webcams() {}
}