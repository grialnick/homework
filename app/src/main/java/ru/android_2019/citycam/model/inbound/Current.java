
package ru.android_2019.citycam.model.inbound;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Current {

    @SerializedName("icon")
    @Expose
    private String icon;
    @SerializedName("thumbnail")
    @Expose
    private String thumbnail;
    @SerializedName("preview")
    @Expose
    private String preview;
    @SerializedName("toenail")
    @Expose
    private String toenail;

    /**
     * No args constructor for use in serialization
     */
    public Current() {
    }

    /**
     * @param icon
     * @param thumbnail
     * @param preview
     * @param toenail
     */
    public Current(String icon, String thumbnail, String preview, String toenail) {
        super();
        this.icon = icon;
        this.thumbnail = thumbnail;
        this.preview = preview;
        this.toenail = toenail;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Current withIcon(String icon) {
        this.icon = icon;
        return this;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Current withThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
        return this;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public Current withPreview(String preview) {
        this.preview = preview;
        return this;
    }

    public String getToenail() {
        return toenail;
    }

    public void setToenail(String toenail) {
        this.toenail = toenail;
    }

    public Current withToenail(String toenail) {
        this.toenail = toenail;
        return this;
    }

}
