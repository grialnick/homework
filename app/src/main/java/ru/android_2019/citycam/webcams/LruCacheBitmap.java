package ru.android_2019.citycam.webcams;

import android.graphics.Bitmap;
import android.util.LruCache;

public final  class LruCacheBitmap {
    private static  LruCache<String, Bitmap> lruCache;
    private static volatile LruCacheBitmap instance;
    private static final byte[]lock = new byte[0];

    public static LruCacheBitmap getInstance(){
        if (instance == null){
            synchronized (lock){
                if (instance == null){
                    instance = new LruCacheBitmap();
                }
            }
        }
        return instance;
    }
    private LruCacheBitmap(){
        int maxMemorySize = 10*1024*1024;
        lruCache = new LruCache<String, Bitmap>(maxMemorySize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes()*value.getHeight();
            }
        };
    }
    public static void putLruCache(String id,Bitmap bitmap) {
        if (bitmap != null){
            lruCache.put(id,bitmap);
        }
    }
    public static Bitmap getLruCache(String id) {
        return lruCache.get(id);
    }
}

