package ru.android_2019.citycam.dataBase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import ru.android_2019.citycam.model.WebCamForm;

import java.util.List;

@Dao
public interface WebCamFormDAO {

    @Query("SELECT * FROM `WebCamForm` WHERE city = :city")
    List<WebCamForm> getByCity(String city);

    @Insert
    void insert(WebCamForm webCam);

    @Query("DELETE FROM `WebCamForm` WHERE city = :city")
    void deleteByCity(String city);
}
