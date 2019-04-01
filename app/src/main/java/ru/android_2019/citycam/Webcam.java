package ru.android_2019.citycam;

import android.graphics.Bitmap;

public class Webcam {

    private final String title;
    private final String city;
    private final String previewUrl;
    private Bitmap image;

    public Webcam(String title, String city, String previewUrl) {
        this.title = title;
        this.city = city;
        this.previewUrl = previewUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getCity() {
        return city;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
