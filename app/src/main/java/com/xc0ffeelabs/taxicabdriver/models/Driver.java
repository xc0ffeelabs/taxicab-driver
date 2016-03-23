package com.xc0ffeelabs.taxicabdriver.models;

import com.parse.ParseClassName;
import com.parse.ParseException;

@ParseClassName("_User")
    public class Driver extends User {

    public static final String ROLE = "role";
    private static final String NAME = "name";
    private static final String PHONE = "phone";
    private static final String LICENSE = "license";
    private static final String CAR_MODEL = "carModel";
    private static final String CAR_NUMBER = "carNumber";
    public static final String CURRENT_LOCATION = "currentLocation";
    public static final String CURRENT_TRIP_ID = "currentTripId";
    public static final String STATE = "state";


    public Driver() {
    }

    public void setRole() {
        put(ROLE, "driver");
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
        try {
            return fetchIfNeeded().getString(NAME);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getEmail() {
        return getUsername();
    }

    public String getRole() {
        return "driver";
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

}
