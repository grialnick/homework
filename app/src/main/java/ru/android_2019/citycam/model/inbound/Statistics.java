
package ru.android_2019.citycam.model.inbound;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class Statistics {

    @SerializedName("views")
    @Expose
    private Long views;

    /**
     * No args constructor for use in serialization
     */
    public Statistics() {
    }

    /**
     * @param views
     */
    public Statistics(Long views) {
        super();
        this.views = views;
    }

    public Long getViews() {
        return views;
    }

    public void setViews(Long views) {
        this.views = views;
    }

    public Statistics withViews(Long views) {
        this.views = views;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("views", views).toString();
    }

}
