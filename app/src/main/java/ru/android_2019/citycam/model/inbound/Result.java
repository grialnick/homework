
package ru.android_2019.citycam.model.inbound;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Result {

    @SerializedName("offset")
    @Expose
    private int offset;
    @SerializedName("limit")
    @Expose
    private int limit;
    @SerializedName("total")
    @Expose
    private int total;
    @SerializedName("webcams")
    @Expose
    private List<Webcam> webcams = null;

    /**
     * No args constructor for use in serialization
     */
    public Result() {
    }

    /**
     * @param total
     * @param limit
     * @param webcams
     * @param offset
     */
    public Result(int offset, int limit, int total, List<Webcam> webcams) {
        super();
        this.offset = offset;
        this.limit = limit;
        this.total = total;
        this.webcams = webcams;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public Result withOffset(int offset) {
        this.offset = offset;
        return this;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public Result withLimit(int limit) {
        this.limit = limit;
        return this;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public Result withTotal(int total) {
        this.total = total;
        return this;
    }

    public List<Webcam> getWebcams() {
        return webcams;
    }

    public void setWebcams(List<Webcam> webcams) {
        this.webcams = webcams;
    }

    public Result withWebcams(List<Webcam> webcams) {
        this.webcams = webcams;
        return this;
    }

}
