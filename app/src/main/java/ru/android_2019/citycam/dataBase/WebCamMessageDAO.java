package ru.android_2019.citycam.dataBase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import ru.android_2019.citycam.model.WebCamMessage;

@Dao
public interface WebCamMessageDAO {

    @Query("SELECT * FROM webcammessage WHERE city = :city")
    List<WebCamMessage> getByCity(String city);

    @Insert
    void insert(WebCamMessage webCam);

    @Query("DELETE FROM webcammessage WHERE city = :city")
    void deleteByCity(String city);

}
