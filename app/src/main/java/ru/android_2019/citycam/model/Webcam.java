package ru.android_2019.citycam.model;

import java.io.IOException;
import java.net.URL;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import ru.android_2019.citycam.dao.BitmapConverter;

@Entity
public class Webcam {

    @PrimaryKey
    private long id;
    private String cityName;
    private String title;
    @TypeConverters(BitmapConverter.class)
    private Bitmap bitmap;

    public Webcam(final long id, final String title, @NonNull final URL imageUrl, @NonNull String cityName) throws IOException {
        this.id = id;
        this.cityName = cityName;
        this.title = title;
        this.bitmap = BitmapFactory.decodeStream(imageUrl.openStream());
    }

    public Webcam() {}

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
