package ru.android_2019.citycam;

import android.graphics.Bitmap;

class Webcam {

    private long id;
    private String title;
    private Bitmap bitmap;

    Webcam(long id, String title, Bitmap bitmap){
        this.id = id;
        this.title = title;
        this.bitmap = bitmap;
    }

    public long getId() {
        return id;
    }

    String getTitle() {
        return title;
    }

    Bitmap getBitmap() {
        return bitmap;
    }
}
