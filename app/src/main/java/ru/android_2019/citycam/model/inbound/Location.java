
package ru.android_2019.citycam.model.inbound;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class Location {

    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("region")
    @Expose
    private String region;
    @SerializedName("region_code")
    @Expose
    private String regionCode;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("country_code")
    @Expose
    private String countryCode;
    @SerializedName("continent")
    @Expose
    private String continent;
    @SerializedName("continent_code")
    @Expose
    private String continentCode;
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("longitude")
    @Expose
    private Double longitude;
    @SerializedName("timezone")
    @Expose
    private String timezone;
    @SerializedName("wikipedia")
    @Expose
    private String wikipedia;

    /**
     * No args constructor for use in serialization
     */
    public Location() {
    }

    /**
     * @param region
     * @param regionCode
     * @param timezone
     * @param continentCode
     * @param wikipedia
     * @param continent
     * @param countryCode
     * @param longitude
     * @param latitude
     * @param country
     * @param city
     */
    public Location(String city, String region, String regionCode, String country, String countryCode, String continent, String continentCode, Double latitude, Double longitude, String timezone, String wikipedia) {
        super();
        this.city = city;
        this.region = region;
        this.regionCode = regionCode;
        this.country = country;
        this.countryCode = countryCode;
        this.continent = continent;
        this.continentCode = continentCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timezone = timezone;
        this.wikipedia = wikipedia;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Location withCity(String city) {
        this.city = city;
        return this;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Location withRegion(String region) {
        this.region = region;
        return this;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public Location withRegionCode(String regionCode) {
        this.regionCode = regionCode;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Location withCountry(String country) {
        this.country = country;
        return this;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Location withCountryCode(String countryCode) {
        this.countryCode = countryCode;
        return this;
    }

    public String getContinent() {
        return continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }

    public Location withContinent(String continent) {
        this.continent = continent;
        return this;
    }

    public String getContinentCode() {
        return continentCode;
    }

    public void setContinentCode(String continentCode) {
        this.continentCode = continentCode;
    }

    public Location withContinentCode(String continentCode) {
        this.continentCode = continentCode;
        return this;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Location withLatitude(Double latitude) {
        this.latitude = latitude;
        return this;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Location withLongitude(Double longitude) {
        this.longitude = longitude;
        return this;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public Location withTimezone(String timezone) {
        this.timezone = timezone;
        return this;
    }

    public String getWikipedia() {
        return wikipedia;
    }

    public void setWikipedia(String wikipedia) {
        this.wikipedia = wikipedia;
    }

    public Location withWikipedia(String wikipedia) {
        this.wikipedia = wikipedia;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("city", city).append("region", region).append("regionCode", regionCode).append("country", country).append("countryCode", countryCode).append("continent", continent).append("continentCode", continentCode).append("latitude", latitude).append("longitude", longitude).append("timezone", timezone).append("wikipedia", wikipedia).toString();
    }

}
