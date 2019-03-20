package ru.android_2019.citycam.webcams.cache;

import android.util.Log;
import android.util.LruCache;

import ru.android_2019.citycam.webcams.model.Webcam;

public class WebcamCache extends LruCache<String, Webcam> {

    private static final int MAX_SIZE = (int) Runtime.getRuntime().maxMemory() / 1024 / 8;
    private static final WebcamCache INSTANCE = new WebcamCache(MAX_SIZE);

    private WebcamCache(int maxSize) {
        super(maxSize);
    }

    public static WebcamCache getInstance() {
        return INSTANCE;
    }

    public Webcam getWebcamFromMemory(String key) {
        return this.get(key);
    }

    public void setWebcamToMemory(String key, Webcam webcam) {
        if (getWebcamFromMemory(key) == null) {
            this.put(key, webcam);
            Log.i(TAG,"Image of " + key + " was added to cache");
        }
    }

    private static final String TAG = "WebcamCache";
}
