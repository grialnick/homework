package ru.android_2019.citycam;

public class WebCamInfo {

    private String id;
    private String title;
    private Location location;
    private String imageURL;
    private String status;

    public WebCamInfo(String id, String title, Location location, String imageURL, String status) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.imageURL = imageURL;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Location getLocation() {
        return location;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "id: " + id + "\ntitle: " + title + "\nlocation: " + location.toString();
    }
}
