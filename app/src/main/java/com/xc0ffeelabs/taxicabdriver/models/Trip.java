package com.xc0ffeelabs.taxicabdriver.models;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Created by skammila on 3/12/16.
 */
@ParseClassName("Trip")
public class Trip extends ParseObject{
    private ParseUser user;
    private Driver driver;
    private String tripStatus;
    public static final String PICKUP_LOCATION = "pickUpLocation";
    public static final String DEST_LOCATION = "destLocation";

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

    public void setPickupLocation(LatLng location, final SaveCallback callback) {
        Location pnt = new Location();
        pnt.setLatitude(location.latitude);
        pnt.setLongitude(location.longitude);
        put(PICKUP_LOCATION, pnt);
        saveInBackground(callback);
    }

    public void setDestLocation(LatLng location) {
        Location pnt = new Location();
        pnt.setLatitude(location.latitude);
        pnt.setLongitude(location.longitude);
        put(DEST_LOCATION, pnt);
        saveInBackground();
    }

    public Location getDestLocation() {
        return (Location) get(DEST_LOCATION);
    }

    public Location getPickUpLocation() {
        return  (Location) get(PICKUP_LOCATION);
    }
}
