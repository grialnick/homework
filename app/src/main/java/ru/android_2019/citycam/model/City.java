package ru.android_2019.citycam.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Город
 */
public class City implements Parcelable {

    /**
     * Название
     */
    public final String name;

    /**
     * Широта
     */
    private final double latitude;

    /**
     * Долгота
     */
    private final double longitude;


    public City(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "City[name=\"" + name + "\" lat=" + latitude + " lon=" + longitude + "]";
    }


    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getName() {
        return name;
    }

    // --------- Методы интерфейса Parcelable ------------
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }

    protected City(Parcel src) {
        name = src.readString();
        latitude = src.readDouble();
        longitude = src.readDouble();
    }

    public static final Creator<City> CREATOR = new Creator<City>() {
        @Override
        public City createFromParcel(Parcel source) {
            return new City(source);
        }

        @Override
        public City[] newArray(int size) {
            return new City[size];
        }
    };

}
