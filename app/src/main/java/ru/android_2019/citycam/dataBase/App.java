package ru.android_2019.citycam.dataBase;

import android.app.Application;
import android.arch.persistence.room.Room;

public class App extends Application {

    public static App instance;
    private WebCamDataBase database;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        database = Room.databaseBuilder(this, WebCamDataBase.class, "database")
                .build();
    }

    public static App getInstance() {
        return instance;
    }

    public WebCamDataBase getDatabase() {
        return database;
    }
}
