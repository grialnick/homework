package ru.android_2019.citycam.cache;

import android.util.LruCache;
import ru.android_2019.citycam.model.City;

public class CityCache {

    private LruCache <String, City> cache;
    private static CityCache instance;

    private CityCache() {
        int cacheSize = 8 * 1024 * 1024;
        cache = new LruCache<>(cacheSize);
    }

    public static CityCache getInstance() {
        if(instance == null) {
            instance = new CityCache();
        }
        return instance;
    }

    public void putDataInCache(City city) {
        cache.put(city.getName(), city);
    }

    public City getDataFromCache(String name) {
        return cache.get(name);
    }
}
