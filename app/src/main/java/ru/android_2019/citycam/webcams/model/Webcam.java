package ru.android_2019.citycam.webcams.model;

import android.graphics.Bitmap;

public class Webcam {

    private final String id;
    private final String title;
    private final String imageUrl;
    private Bitmap bitmap;

    public Webcam(String id,
                  String title,
                  String imageUrl) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
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
