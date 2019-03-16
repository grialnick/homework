package ru.android_2019.citycam.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.IOException;
import java.net.URL;

public class Webcam {

    private final long id;
    private final String status;
    private final URL imageUrl;
    private final Bitmap bitmap;

    public Webcam(long id, String status, URL imageUrl) throws IOException {
        this.id = id;
        this.status = status;
        this.imageUrl = imageUrl;
        this.bitmap = BitmapFactory.decodeStream(this.imageUrl.openStream());
    }

    public Bitmap getImage() {
        return bitmap;
    }
}
