package ru.android_2019.citycam.webcams.store;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Webcam.class}, version = 1, exportSchema = false)
public abstract class WebcamDatabase extends RoomDatabase {
    public abstract WebcamDao webcamDao();
}