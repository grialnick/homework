package ru.android_2019.citycam.serializer;

import com.google.gson.Gson;

public class Serializer {
    private static Gson ourInstance = new Gson();

    private Serializer() {
    }

    public static Gson getInstance() {
        return ourInstance;
    }
}