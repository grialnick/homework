package ru.android_2019.citycam.model;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


import java.io.IOException;
import java.net.URL;

import ru.android_2019.citycam.dao.BitmapConverter;

@Entity
public class Webcam {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private String cityName;
    private String title;
    @TypeConverters(BitmapConverter.class)
    private Bitmap bitmap;

    public Webcam() {

    }


    public Webcam(String title, URL imageUrl) throws IOException {
        this.title = title;
        this.bitmap = BitmapFactory.decodeStream(imageUrl.openStream());
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void setId (long id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }
    public Bitmap getBitmap(){
        return bitmap;
    }

    public String getTitle() {
        return title;
    }

    public long getId () {
        return id;
    }

    public Bitmap getImage() {
        return bitmap;
    }
}
