
package ru.android_2019.citycam.model.inbound.url;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class Current {

    @SerializedName("desktop")
    @Expose
    private String desktop;
    @SerializedName("mobile")
    @Expose
    private String mobile;

    /**
     * No args constructor for use in serialization
     */
    public Current() {
    }

    /**
     * @param desktop
     * @param mobile
     */
    public Current(String desktop, String mobile) {
        super();
        this.desktop = desktop;
        this.mobile = mobile;
    }

    public String getDesktop() {
        return desktop;
    }

    public void setDesktop(String desktop) {
        this.desktop = desktop;
    }

    public Current withDesktop(String desktop) {
        this.desktop = desktop;
        return this;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Current withMobile(String mobile) {
        this.mobile = mobile;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("desktop", desktop).append("mobile", mobile).toString();
    }

}
