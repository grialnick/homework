package ru.android_2019.citycam;

import android.util.LruCache;

import java.util.List;

public class WebcamsCache {
    private static WebcamsCache instance;
    private int cacheSize = 4 * 1024 * 1024;
    private LruCache<String, List<Webcam>> cache;

    private WebcamsCache() {
        cache = new LruCache<>(cacheSize);
    }

    public static WebcamsCache getInstance() {
        if (instance == null) {
            instance = new WebcamsCache();
        }
        return instance;
    }

    public void put(String key, List<Webcam> webcams) {
        cache.put(key, webcams);
    }

    public List<Webcam> get(String key) {
        return cache.get(key);
    }
}
