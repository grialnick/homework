package ru.android_2019.citycam.reader;

import android.graphics.Bitmap;

/**
 * ["result"]["webcams"][0]["id"]    ("1350096618")
 * ["result"]["webcams"][0]["status"] if "active"
 * ["result"]["webcams"][0]["title"]   ("St Petersburg: Saint Petersburg − Intermodal Terminal")
 * ["result"]["webcams"][0]["image"]["current"]["preview"] ("https://images.webcams.travel/preview/1240664025.jpg")
 * ["result"]["webcams"][0]["location"]["timezone"]  ("Europe/Moscow")
 * ["result"]["webcams"][0]["statistics"]["views"]   (28718)
 */

public class WebcamsMessage {
    private String id;
    private String status;
    private String title;
    private String preview;
    private String timezone;
    private Integer views;
    private Bitmap bitmap = null;

    public WebcamsMessage(String id, String status, String title, String preview, String timezone, Integer views) {
        this.id = id;
        this.status = status;
        this.title = title;
        this.preview = preview;
        this.timezone = timezone;
        this.views = views;
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }

    public String getPreview() {
        return preview;
    }

    public String getTimezone() {
        return timezone;
    }

    public Integer getViews() {
        return views;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}