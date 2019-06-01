package ru.android_2019.citycam.asynctask.camjson;

import java.util.ArrayList;

public class CamInfo {

    private String id;
    private String title;
    private ArrayList<Category> categories;
    private Location location;
    private String URLPreviewImage;

    CamInfo(String id, String title, ArrayList<Category> categories, Location location, String URLPreviewImage) {
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

    public String getURLPreviewImage() {
        return URLPreviewImage;
    }

    public String getLocationAsString(){
        return location.toString();
    }

    public String getCategoriesAsString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Category category : categories) {
            stringBuilder.append(category.toString())
                    .append(", ");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }
}
