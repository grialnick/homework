
package ru.android_2019.citycam.model.inbound;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Image {

    @SerializedName("current")
    @Expose
    private Current current;
    @SerializedName("daylight")
    @Expose
    private Daylight daylight;
    @SerializedName("sizes")
    @Expose
    private Sizes sizes;
    @SerializedName("update")
    @Expose
    private int update;
    @SerializedName("interval")
    @Expose
    private int interval;

    /**
     * No args constructor for use in serialization
     */
    public Image() {
    }

    /**
     * @param update
     * @param sizes
     * @param interval
     * @param current
     * @param daylight
     */
    public Image(Current current, Daylight daylight, Sizes sizes, int update, int interval) {
        super();
        this.current = current;
        this.daylight = daylight;
        this.sizes = sizes;
        this.update = update;
        this.interval = interval;
    }

    public Current getCurrent() {
        return current;
    }

    public void setCurrent(Current current) {
        this.current = current;
    }

    public Image withCurrent(Current current) {
        this.current = current;
        return this;
    }

    public Daylight getDaylight() {
        return daylight;
    }

    public void setDaylight(Daylight daylight) {
        this.daylight = daylight;
    }

    public Image withDaylight(Daylight daylight) {
        this.daylight = daylight;
        return this;
    }

    public Sizes getSizes() {
        return sizes;
    }

    public void setSizes(Sizes sizes) {
        this.sizes = sizes;
    }

    public Image withSizes(Sizes sizes) {
        this.sizes = sizes;
        return this;
    }

    public int getUpdate() {
        return update;
    }

    public void setUpdate(int update) {
        this.update = update;
    }

    public Image withUpdate(int update) {
        this.update = update;
        return this;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public Image withInterval(int interval) {
        this.interval = interval;
        return this;
    }

}
