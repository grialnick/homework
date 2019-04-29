package ru.android_2019.citycam.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.io.IOException;
import java.util.List;

import ru.android_2019.citycam.model.Cam;

@Dao
public interface CamDAO {

    @Insert
    void insert(List <Cam> cams);

    @Query("SELECT * FROM cam WHERE cityName IS :cityName")
    List <Cam> selectByName(String cityName) throws IOException;
}
