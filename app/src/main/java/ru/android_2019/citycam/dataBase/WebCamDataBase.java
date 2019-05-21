package ru.android_2019.citycam.dataBase;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import ru.android_2019.citycam.model.WebCamForm;

@Database(entities = {WebCamForm.class}, version = 1, exportSchema = false)
public abstract class WebCamDataBase extends RoomDatabase {
    public abstract WebCamFormDAO webCamFormDAO();
}
