package ru.android_2019.citycam.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.graphics.Bitmap;

import ru.android_2019.citycam.dataBase.BitmapConverter;

@Entity
public class WebCamForm {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    @TypeConverters(BitmapConverter.class)
    private Bitmap image;

    private String city;
    private String time;

    public WebCamForm() {
    }

    public WebCamForm(int id, String title, Bitmap image, String time) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
