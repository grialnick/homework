
package ru.android_2019.citycam.model.inbound;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

import ru.android_2019.citycam.model.inbound.url.Current;
import ru.android_2019.citycam.model.inbound.url.Daylight;

public class Url {

    @SerializedName("current")
    @Expose
    private Current current;
    @SerializedName("daylight")
    @Expose
    private Daylight daylight;
    @SerializedName("edit")
    @Expose
    private String edit;

    /**
     * No args constructor for use in serialization
     */
    public Url() {
    }

    /**
     * @param edit
     * @param current
     * @param daylight
     */
    public Url(Current current, Daylight daylight, String edit) {
        super();
        this.current = current;
        this.daylight = daylight;
        this.edit = edit;
    }

    public Current getCurrent() {
        return current;
    }

    public void setCurrent(Current current) {
        this.current = current;
    }

    public Url withCurrent(Current current) {
        this.current = current;
        return this;
    }

    public Daylight getDaylight() {
        return daylight;
    }

    public void setDaylight(Daylight daylight) {
        this.daylight = daylight;
    }

    public Url withDaylight(Daylight daylight) {
        this.daylight = daylight;
        return this;
    }

    public String getEdit() {
        return edit;
    }

    public void setEdit(String edit) {
        this.edit = edit;
    }

    public Url withEdit(String edit) {
        this.edit = edit;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("current", current).append("daylight", daylight).append("edit", edit).toString();
    }

}
