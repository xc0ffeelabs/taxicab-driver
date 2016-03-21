package com.xc0ffeelabs.taxicabdriver.models;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

@ParseClassName("_User")
public class User extends ParseUser {

    public enum UserStates {
        Online,
        WaitingDriver,
        DriverArrived,
        EnrouteDest
    }

    public static final String ROLE = "role";
    public static final String NAME = "name";
    public static final String PHONE = "phone";
    public static final String LICENSE = "license";
    public static final String CAR_MODEL = "carModel";
    public static final String CAR_NUMBER = "carNumber";
    public static final String CURRENT_LOCATION = "currentLocation";
    private static final String DEST_LOCATION = "destLocation";
    public static final String STATE = "state";
    public static final String PICKUP_LOCATION = "pickUpLocation";

    public static final String USER_ROLE = "user";
    public static final String DRIVER_ROLE = "driver";

    private String travelTimeText;
    private long travelTime;

    public User() {
    }

    public void setRole(String role) {
        put(ROLE, role);
    }

    public void setName(String name) {
        put(NAME, name);
    }

    public void setPhone(String phone) {
        put(PHONE, phone);
    }

    public void setLicense(String license) {
        put(LICENSE, license);
    }

    public void setCarModel(String carModel) {
        put(CAR_MODEL, carModel);
    }

    public void setCarNumber(String carNumber) {
        put(CAR_NUMBER, carNumber);
    }

    public String getName() {
        return getString(NAME);
    }

    public String getEmail() {
        return getUsername();
    }

    public String getRole() {
        return getString(ROLE);
    }

    public String getPhone() {
        return getString(PHONE);
    }

    public String getLicense() {
        return getString(LICENSE);
    }

    public String getCarModel() {
        return getString(CAR_MODEL);
    }

    public String getCarNumber() {
        return getString(CAR_NUMBER);
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint(CURRENT_LOCATION);
    }

    public LatLng getPosition() {
        return new LatLng(getLocation().getLatitude(), getLocation().getLongitude());
    }

    public String getTravelTimeText() {
        return travelTimeText;
    }

    public void setTravelTimeText(String travelTimeText) {
        this.travelTimeText = travelTimeText;
    }

    public long getTravelTime() {
        return travelTime;
    }

    public void setTravelTime(long travelTime) {
        this.travelTime = travelTime;
    }

    public String getUserState() {
        return getString("state");
    }

    public void setUserState(UserStates state) {
        put("state", state);
    }

    public void setPickupLocation(LatLng location, final SaveCallback callback) {
        Location pnt = new Location();
        pnt.setLatitude(location.latitude);
        pnt.setLongitude(location.longitude);
        put(PICKUP_LOCATION, pnt);
        saveInBackground(callback);
    }

    public Location getDestLocation() {
        return (Location) get(DEST_LOCATION);
    }
    public Location getPickupLocation() {
        return (Location)get(PICKUP_LOCATION);
    }
}
