package ru.android_2019.citycam.appconfig;

import android.app.Application;
import android.arch.persistence.room.Room;
import ru.android_2019.citycam.database.WebcamDatabase;

public class App extends Application {
    private static App instance;
    private WebcamDatabase webcamDatabase;

    @Override
    public void onCreate(){
        super.onCreate();
        instance = this;
        webcamDatabase = Room.databaseBuilder(this, WebcamDatabase.class, "database").build();
    }

    public WebcamDatabase getCityDatabase() {
        return webcamDatabase;
    }

    public static App getInstance(){
        return instance;
    }

    public App() {

    }
}
