package com.xc0ffeelabs.taxicabdriver.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by skammila on 3/12/16.
 */
@ParseClassName("Trip")
public class Trip extends ParseObject{
    private ParseUser user;
    private Driver driver;
    private String tripStatus;

    public Trip() {

    }

    public ParseUser getUser() {
        return user;
    }

    public void setUser(ParseUser user) {
        this.user = user;
    }

    public String getTripStatus() {
        return tripStatus;
    }

    public void setTripStatus(String tripStatus) {
        this.tripStatus = tripStatus;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }
}
