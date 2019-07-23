package ru.android_2019.citycam;

public class Location {

    private String region;
    private String regionCode;
    private String timeZone;

    public Location(String region, String regionCode, String timeZone) {
        this.region = region;
        this.regionCode = regionCode;
        this.timeZone = timeZone;
    }

    public String getRegion() {
        return region;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public String getTimeZone() {
        return timeZone;
    }

    @Override
    public String toString() {
        return "\n\tregion: " + region +  "\n\tregion code : " + regionCode + "\n\ttime zone : " + timeZone;
    }
}
