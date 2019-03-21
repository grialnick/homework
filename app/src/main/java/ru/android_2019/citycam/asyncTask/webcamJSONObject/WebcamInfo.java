package ru.android_2019.citycam.asyncTask.webcamJSONObject;

import java.util.ArrayList;

public class WebcamInfo {

    private String id;
    private String title;
    private ArrayList<Category> categories;
    private Location location;
    private String URLPreviewImage;

    public WebcamInfo(String id, String title, ArrayList<Category> categories, Location location, String URLPreviewImage) {
        this.id = id;
        this.title = title;
        this.categories = categories;
        this.location = location;
        this.URLPreviewImage = URLPreviewImage;
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public Location getLocation() {
        return location;
    }

    public String getURLPreviewImage() {
        return URLPreviewImage;
    }

    public String getGategoriesAsString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n");
        int count = 1;
        for (Category category : categories) {
            stringBuilder.append(count++)
                    .append(") ")
                    .append("id : '")
                    .append(category.getId())
                    .append("' , name : '")
                    .append(category.getName())
                    .append("'\n");
        }
        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        return "WebcamInfo{" +
                "id=" + id +
                ", title='" + title + '\'' +
                "\ncategories: " + getGategoriesAsString() +
                "location: " + location.toString() +
                ", URLPreviewImage='" + URLPreviewImage + '\'' +
                '}';
    }
}
