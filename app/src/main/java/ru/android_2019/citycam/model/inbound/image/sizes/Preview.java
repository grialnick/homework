
package ru.android_2019.citycam.model.inbound.image.sizes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class Preview {

    @SerializedName("width")
    @Expose
    private Long width;
    @SerializedName("height")
    @Expose
    private Long height;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Preview() {
    }

    /**
     * 
     * @param height
     * @param width
     */
    public Preview(Long width, Long height) {
        super();
        this.width = width;
        this.height = height;
    }

    public Long getWidth() {
        return width;
    }

    public void setWidth(Long width) {
        this.width = width;
    }

    public Preview withWidth(Long width) {
        this.width = width;
        return this;
    }

    public Long getHeight() {
        return height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }

    public Preview withHeight(Long height) {
        this.height = height;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("width", width).append("height", height).toString();
    }

}
