package ru.android_2019.citycam.cache;

import android.util.LruCache;
import ru.android_2019.citycam.CityCamActivity;

public class MyCache {

    private static MyCache instance;
    private LruCache<String, CityCamActivity.DownloadImageTask.WebcamsMessage> lru;
    private int cacheSize = 8 * 1024 * 1024; //8 MiB

    private MyCache() {
        lru = new LruCache<>(cacheSize);
    }

    public static MyCache getInstance() {
        if (instance == null) {
            instance = new MyCache();
        }
        return instance;
    }

    public LruCache<String, CityCamActivity.DownloadImageTask.WebcamsMessage> getLru() {
        return lru;
    }
}