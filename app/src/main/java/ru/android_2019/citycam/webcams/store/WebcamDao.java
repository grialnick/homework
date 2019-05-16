package ru.android_2019.citycam.webcams.store;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface WebcamDao {

    @Query("SELECT * FROM webcam")
    List<Webcam> getAll();

    @Query("SELECT * FROM webcam WHERE cityName = :cityName")
    Webcam getByCityName(String cityName);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Webcam webcam);

    @Delete
    void delete(Webcam webcam);
}
