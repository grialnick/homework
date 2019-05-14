package ru.android_2019.citycam.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;


import ru.android_2019.citycam.dao.WebcamDAO;
import ru.android_2019.citycam.model.Webcam;

@Database(entities = {Webcam.class}, version = 3, exportSchema = false)
public abstract class WebcamDatabase  extends RoomDatabase {
    public abstract WebcamDAO webcamDao();
}
