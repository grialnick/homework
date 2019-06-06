package ru.android_2019.citycam;

public class WebCam {

    private String id;
    private String status;
    private String title;
    private String imageURL;

    public WebCam(String id, String status, String title, String imageURL) {
        this.id = id;
        this.status = status;
        this.title = title;
        this.imageURL = imageURL;
    }

    public String getImageURL() {
        return imageURL;
    }

    @Override
    public String toString() {
        return "id = " + id +
                "\n status = " + status +
                "\n title = " + title;
    }
}
