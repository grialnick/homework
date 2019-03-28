package ru.android_2019.citycam.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;


import java.io.IOException;

import ru.android_2019.citycam.model.Webcam;

@Dao
public interface WebcamDAO {
    @Insert
    void insert(Webcam webcam);

    @Query("SELECT * FROM webcam WHERE cityName IS :cityName")
    Webcam selectByName(String cityName) throws IOException;
}
