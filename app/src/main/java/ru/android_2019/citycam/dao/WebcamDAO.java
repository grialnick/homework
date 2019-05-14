package ru.android_2019.citycam.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;


import java.io.IOException;
import java.util.List;

import ru.android_2019.citycam.model.Webcam;

@Dao
public interface WebcamDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertWebcams(List<Webcam> webcamList);

    @Query("SELECT * FROM webcam WHERE cityName IS :cityName")
    List <Webcam> selectListWebcamsByName(String cityName) throws IOException;
}
