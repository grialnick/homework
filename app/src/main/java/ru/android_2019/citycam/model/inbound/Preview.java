
package ru.android_2019.citycam.model.inbound;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Preview {

    @SerializedName("width")
    @Expose
    private int width;
    @SerializedName("height")
    @Expose
    private int height;

    /**
     * No args constructor for use in serialization
     */
    public Preview() {
    }

    /**
     * @param height
     * @param width
     */
    public Preview(int width, int height) {
        super();
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public Preview withWidth(int width) {
        this.width = width;
        return this;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Preview withHeight(int height) {
        this.height = height;
        return this;
    }

}
