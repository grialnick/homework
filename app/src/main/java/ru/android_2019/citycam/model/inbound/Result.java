
package ru.android_2019.citycam.model.inbound;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class Result {

    @SerializedName("offset")
    @Expose
    private Long offset;
    @SerializedName("limit")
    @Expose
    private Long limit;
    @SerializedName("total")
    @Expose
    private Long total;
    @SerializedName("webcams")
    @Expose
    private List<Webcam> webcams = null;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Result() {
    }

    /**
     * 
     * @param total
     * @param limit
     * @param webcams
     * @param offset
     */
    public Result(Long offset, Long limit, Long total, List<Webcam> webcams) {
        super();
        this.offset = offset;
        this.limit = limit;
        this.total = total;
        this.webcams = webcams;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public Result withOffset(Long offset) {
        this.offset = offset;
        return this;
    }

    public Long getLimit() {
        return limit;
    }

    public void setLimit(Long limit) {
        this.limit = limit;
    }

    public Result withLimit(Long limit) {
        this.limit = limit;
        return this;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Result withTotal(Long total) {
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

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("offset", offset).append("limit", limit).append("total", total).append("webcams", webcams).toString();
    }

}
