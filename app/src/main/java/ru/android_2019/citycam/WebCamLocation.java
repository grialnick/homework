package ru.android_2019.citycam;

class WebCamLocation {
    private String timezone;
    private String city;

    WebCamLocation(String timezone, String city) {
        this.timezone = timezone;
        this.city = city;
    }

    String getTimezone() {
        return timezone;
    }

    public String getCity() {
        return city;
    }


}
