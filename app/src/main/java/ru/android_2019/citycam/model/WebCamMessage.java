package ru.android_2019.citycam.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.graphics.Bitmap;

import ru.android_2019.citycam.dataBase.BitmapConverter;

@Entity
public class WebCamMessage {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private long camId;
    private String title;
    private String timeZone;
    @TypeConverters(BitmapConverter.class)
    private Bitmap image;

    private String status;
    private String views;
    private String city;
    private String time;

    public WebCamMessage() {
    }

    public WebCamMessage(long id, String title, String timeZone, Bitmap image, String status, String views, String time) {
        this.camId = id;
        this.title = title;
        this.timeZone = timeZone;
        this.image = image;
        this.status = status;
        this.views = views;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getCamId() {
        return camId;
    }

    public void setCamId(long camId) {
        this.camId = camId;
    }

    public String getTitle() {
        return title;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public Bitmap getImage() {
        return image;
    }

    public String getStatus() {
        return status;
    }

    public String getViews() {
        return views;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCity() {
        return city;
    }

    public String getTime() {
        return time;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
