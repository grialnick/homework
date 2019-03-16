package ru.android_2019.citycam;


class WebCamMessage {
    private long id;
    private String title;
    private WebCamLocation location;
    private String image;
    private String status;
    private String views;

    WebCamMessage(long id, String title, WebCamLocation location, String image, String status, String views) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.image = image;
        this.status = status;
        this.views = views;
    }

    public long getId() {
        return id;
    }

    String getTitle() {
        return title;
    }

    WebCamLocation getLocation() {
        return location;
    }

    String getImage() {
        return image;
    }

    String getStatus() {
        return status;
    }

    String getViews() {
        return views;
    }
}
