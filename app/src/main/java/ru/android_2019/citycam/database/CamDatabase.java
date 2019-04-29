package ru.android_2019.citycam.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import ru.android_2019.citycam.dao.CamDAO;
import ru.android_2019.citycam.model.Cam;

@Database(entities = {Cam.class}, version = 2, exportSchema = false)
public abstract class CamDatabase extends RoomDatabase {
    public abstract CamDAO getCamDAO();
}
