package ru.android_2019.citycam.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;

import java.util.List;

import ru.android_2019.citycam.model.Webcam;

@Dao
public abstract class WebcamDAO {
    @Query("SELECT * FROM webcam where city = :city")
    public abstract List<Webcam> getWebcamsByCity(String city);

    @Insert
    public abstract void insert(Webcam webcam);

    @Update
    public abstract int update(Webcam webcam);

    @Transaction
    public void insertOrUpdateList(List<Webcam> webcamList) {
        for (Webcam webcam : webcamList) {
            int isUpdated = update(webcam);
            if (isUpdated == 0) {
                insert(webcam);
            }
        }
    }
}
