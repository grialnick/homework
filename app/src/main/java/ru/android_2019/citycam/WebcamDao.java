package ru.android_2019.citycam;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;


@Dao
public interface WebcamDao {
    @Query("select * from webcam where cityName = :cityName")
    List<Webcam> getByCityName(String cityName);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Webcam webcam);
}
