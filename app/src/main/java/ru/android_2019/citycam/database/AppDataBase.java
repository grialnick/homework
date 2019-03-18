package ru.android_2019.citycam.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import ru.android_2019.citycam.model.Webcam;

@Database(entities = {Webcam.class}, version = 1, exportSchema = false)
public abstract  class AppDataBase extends RoomDatabase {
    public abstract WebcamDAO webcamDAO();
}
