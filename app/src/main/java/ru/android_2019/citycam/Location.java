package ru.android_2019.citycam;

class Location {
    private String city;
    private String region;
    private String country;
    private String continent;
    private String wikiURL;

    public void setCity(String city) {
        this.city = city;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setWikiURL(String wikiURL) {
        this.wikiURL = wikiURL;
    }

    public String getCity() {
        return city;
    }

    public String getContinent() {
        return continent;
    }

    public String getCountry() {
        return country;
    }

    public String getRegion() {
        return region;
    }

    public String getWikiURL() {
        return wikiURL;
    }
}
