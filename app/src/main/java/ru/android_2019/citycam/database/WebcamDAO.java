package ru.android_2019.citycam.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import ru.android_2019.citycam.model.Webcam;

@Dao
public interface WebcamDAO {
    @Query("SELECT * FROM webcam where city = :city")
    List<Webcam> getWebcamsByCity(String city);

    @Insert
    void insert(Webcam webcam);
}
