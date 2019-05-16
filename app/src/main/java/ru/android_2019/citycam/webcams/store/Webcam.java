package ru.android_2019.citycam.webcams.store;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

@Entity
public class Webcam {

    @PrimaryKey
    @NonNull
    private String cityName;
    private String title;
    private String imageUrl;
    @TypeConverters({BitmapConverter.class})
    private Bitmap bitmap;

    public Webcam(@NonNull final String cityName) {
        this.cityName = cityName;
    }

    @NonNull
    public String getCityName() {
        return cityName;
    }

    public void setCityName(@NonNull String cityName) {
        this.cityName = cityName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public enum TypeImage {
        ICON("icon"),
        THUMBNAIL("thumbnail"),
        PREVIEW("preview"),
        TOENAIL("toenail");

        String name;

        TypeImage(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
