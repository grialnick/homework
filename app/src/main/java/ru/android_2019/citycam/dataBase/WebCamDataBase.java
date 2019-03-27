package ru.android_2019.citycam.dataBase;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import ru.android_2019.citycam.model.WebCamMessage;

@Database(entities = {WebCamMessage.class}, version = 1, exportSchema = false)
public abstract class WebCamDataBase extends RoomDatabase {
    public abstract WebCamMessageDAO webCamMessageDAO();
}
