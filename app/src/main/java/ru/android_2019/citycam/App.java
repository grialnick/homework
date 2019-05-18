package ru.android_2019.citycam;

import android.app.Application;
import android.arch.persistence.room.Room;

import ru.android_2019.citycam.database.AppDataBase;

public class App extends Application {
    private static App instance;
    private AppDataBase dataBase;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        dataBase = Room.databaseBuilder(this, AppDataBase.class, "database")
                .build();
    }

    public static App getInstance() {
        return instance;
    }

    public AppDataBase getDataBase() {
        return dataBase;
    }
}
