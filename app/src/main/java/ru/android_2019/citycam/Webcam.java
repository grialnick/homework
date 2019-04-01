package ru.android_2019.citycam;

import android.graphics.Bitmap;

public class Webcam {

    public final String title;
    public final String city;
    public final String previewUrl;
    private Bitmap image;

    public Webcam(String title, String city, String previewUrl) {
        this.title = title;
        this.city = city;
        this.previewUrl = previewUrl;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
