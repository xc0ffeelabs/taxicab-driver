package com.xc0ffeelabs.taxicabdriver.models;


import com.parse.ParseClassName;
import com.parse.ParseObject;
@ParseClassName("Location")
public class Location extends ParseObject {

    public static final String LATITUDE = "lat";
    public static final String LONGITUDE = "long";

    public Location() {
    }

    public void setLatitude(double latitude) {
        put(LATITUDE, latitude);
    }

    public void setLongitude(double longitude) {
        put(LONGITUDE, longitude);
    }

    public double getLatitude() {
        return getDouble(LATITUDE);
    }

    public double getLongitude() {
        return getDouble(LONGITUDE);
    }
}
