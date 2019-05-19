package ru.android_2019.citycam;

import android.app.Application;
import android.arch.persistence.room.Room;

public class WebcamApp extends Application {
    private static WebcamApp instance;
    private WebcamDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        database = Room.databaseBuilder(this, WebcamDatabase.class, "webcamDatabase").build();
    }

    public static WebcamApp getInstance() {
        return instance;
    }

    public WebcamDatabase getDatabase() {
        return database;
    }
}
