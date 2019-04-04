package ru.android_2019.citycam;

import android.util.LruCache;

public class WebcamCache extends LruCache<String, Webcam> {

    private static final int MAX_SIZE = 4 * 1024 * 1024;
    private static WebcamCache instance;

    private WebcamCache(int maxSize) {
        super(maxSize);
    }

    public static WebcamCache getInstance() {
        if (instance == null) {
            instance = new WebcamCache(MAX_SIZE);
        }
        return instance;
    }
}
