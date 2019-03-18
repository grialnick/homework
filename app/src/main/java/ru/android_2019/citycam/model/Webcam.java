package ru.android_2019.citycam.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.IOException;
import java.net.URL;

public class Webcam {

    private final long id;
    private final String title;
    private final URL imageUrl;
    private final Bitmap bitmap;

    public Webcam(long id, String title, URL imageUrl) throws IOException {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.bitmap = BitmapFactory.decodeStream(this.imageUrl.openStream());
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
