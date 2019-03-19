package ru.android_2019.citycam.webcams;

import android.graphics.Bitmap;

public class Webcam {
    private final int id;
    private final String status;
    private final String title;
    private final Bitmap preview;

    public Webcam(int id, String status, String title, Bitmap preview) {
        this.id = id;
        this.status = status;
        this.title = title;
        this.preview = preview;
    }

    public String getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }

    public Bitmap getPreview() {
        return preview;
    }
}
