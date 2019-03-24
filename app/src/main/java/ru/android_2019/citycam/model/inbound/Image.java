
package ru.android_2019.citycam.model.inbound;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

import ru.android_2019.citycam.model.inbound.image.Current;
import ru.android_2019.citycam.model.inbound.image.Daylight;
import ru.android_2019.citycam.model.inbound.image.Sizes;

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
    private Long update;
    @SerializedName("interval")
    @Expose
    private Long interval;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Image() {
    }

    /**
     * 
     * @param update
     * @param sizes
     * @param interval
     * @param current
     * @param daylight
     */
    public Image(Current current, Daylight daylight, Sizes sizes, Long update, Long interval) {
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

    public Long getUpdate() {
        return update;
    }

    public void setUpdate(Long update) {
        this.update = update;
    }

    public Image withUpdate(Long update) {
        this.update = update;
        return this;
    }

    public Long getInterval() {
        return interval;
    }

    public void setInterval(Long interval) {
        this.interval = interval;
    }

    public Image withInterval(Long interval) {
        this.interval = interval;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("current", current).append("daylight", daylight).append("sizes", sizes).append("update", update).append("interval", interval).toString();
    }

}
