package ru.android_2019.citycam.asynctask.camjson;

public class Category {
    private String id;
    private String name;

    Category(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "\'" + name + "\'";
    }
}