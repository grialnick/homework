
package ru.android_2019.citycam.model.inbound;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Webcam {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("image")
    @Expose
    private Image image;

    /**
     * No args constructor for use in serialization
     */
    public Webcam() {
    }

    /**
     * @param id
     * @param title
     * @param status
     * @param image
     */
    public Webcam(String id, String status, String title, Image image) {
        super();
        this.id = id;
        this.status = status;
        this.title = title;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Webcam withId(String id) {
        this.id = id;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Webcam withStatus(String status) {
        this.status = status;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Webcam withTitle(String title) {
        this.title = title;
        return this;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Webcam withImage(Image image) {
        this.image = image;
        return this;
    }

}
