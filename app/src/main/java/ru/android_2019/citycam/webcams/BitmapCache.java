package ru.android_2019.citycam.webcams;

import android.graphics.Bitmap;
import android.util.LruCache;

public class BitmapCache {
    private static int size = 1024 * 1024 * 8;
    public static LruCache<String, Bitmap> bitmapLruCache = new LruCache<>(size);
}
