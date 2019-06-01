package ru.android_2019.citycam.asynctask.camjson;

public class Location {
    private String region;
    private String regionCode;
    private String timeZone;

    Location(String region, String regionCode, String timeZone) {
        this.region = region;
        this.regionCode = regionCode;
        this.timeZone = timeZone;
    }

    @Override
    public String toString() {
        return "\nРегион: '" + region + '\'' +
                "\nКод региона: '" + regionCode + '\'' +
                "\nВременная зона: '" + timeZone + '\'';
    }
}
