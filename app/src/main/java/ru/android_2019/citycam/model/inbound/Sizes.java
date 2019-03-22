
package ru.android_2019.citycam.model.inbound;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Sizes {

    @SerializedName("icon")
    @Expose
    private Icon icon;
    @SerializedName("thumbnail")
    @Expose
    private Thumbnail thumbnail;
    @SerializedName("preview")
    @Expose
    private Preview preview;
    @SerializedName("toenail")
    @Expose
    private Toenail toenail;

    /**
     * No args constructor for use in serialization
     */
    public Sizes() {
    }

    /**
     * @param icon
     * @param thumbnail
     * @param preview
     * @param toenail
     */
    public Sizes(Icon icon, Thumbnail thumbnail, Preview preview, Toenail toenail) {
        super();
        this.icon = icon;
        this.thumbnail = thumbnail;
        this.preview = preview;
        this.toenail = toenail;
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public Sizes withIcon(Icon icon) {
        this.icon = icon;
        return this;
    }

    public Thumbnail getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Thumbnail thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Sizes withThumbnail(Thumbnail thumbnail) {
        this.thumbnail = thumbnail;
        return this;
    }

    public Preview getPreview() {
        return preview;
    }

    public void setPreview(Preview preview) {
        this.preview = preview;
    }

    public Sizes withPreview(Preview preview) {
        this.preview = preview;
        return this;
    }

    public Toenail getToenail() {
        return toenail;
    }

    public void setToenail(Toenail toenail) {
        this.toenail = toenail;
    }

    public Sizes withToenail(Toenail toenail) {
        this.toenail = toenail;
        return this;
    }

}
