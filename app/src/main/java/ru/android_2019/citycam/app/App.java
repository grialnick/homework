package ru.android_2019.citycam.app;

import android.app.Application;
import android.arch.persistence.room.Room;

import ru.android_2019.citycam.database.CamDatabase;


public class App extends Application {

    private static App instance;
    private CamDatabase camDatabase;
    private static final String DATABASE_INSTANCE = "DATABASE_INSTANCE";

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        camDatabase = Room.databaseBuilder(this, CamDatabase.class, DATABASE_INSTANCE).build();
    }

    public static App getInstance(){
        return instance;
    }

    public CamDatabase getCamDatabase() {
        return camDatabase;
    }

    public App(){}

}
