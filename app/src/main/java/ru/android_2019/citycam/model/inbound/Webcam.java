
package ru.android_2019.citycam.model.inbound;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

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
    @SerializedName("category")
    @Expose
    private List<Category> category = null;
    @SerializedName("image")
    @Expose
    private Image image;
    @SerializedName("location")
    @Expose
    private Location location;
    @SerializedName("statistics")
    @Expose
    private Statistics statistics;
    @SerializedName("url")
    @Expose
    private Url url;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Webcam() {
    }

    /**
     * 
     * @param id
     * @param category
     * @param title
     * @param location
     * @param status
     * @param image
     * @param url
     * @param statistics
     */
    public Webcam(String id, String status, String title, List<Category> category, Image image, Location location, Statistics statistics, Url url) {
        super();
        this.id = id;
        this.status = status;
        this.title = title;
        this.category = category;
        this.image = image;
        this.location = location;
        this.statistics = statistics;
        this.url = url;
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

    public List<Category> getCategory() {
        return category;
    }

    public void setCategory(List<Category> category) {
        this.category = category;
    }

    public Webcam withCategory(List<Category> category) {
        this.category = category;
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Webcam withLocation(Location location) {
        this.location = location;
        return this;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public void setStatistics(Statistics statistics) {
        this.statistics = statistics;
    }

    public Webcam withStatistics(Statistics statistics) {
        this.statistics = statistics;
        return this;
    }

    public Url getUrl() {
        return url;
    }

    public void setUrl(Url url) {
        this.url = url;
    }

    public Webcam withUrl(Url url) {
        this.url = url;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("status", status).append("title", title).append("category", category).append("image", image).append("location", location).append("statistics", statistics).append("url", url).toString();
    }

}
