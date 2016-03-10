package com.xc0ffeelabs.taxicabdriver.services;

import com.parse.ParseUser;
import com.xc0ffeelabs.taxicabdriver.models.Driver;

/**
 * Created by skammila on 3/10/16.
 */
public class DriverStateManager {
    public enum DRIVER_STATES{
        ACTIVE, INACTIVE, GOING_TO_PICKUP, PICKEDUP_CUSTOMER, GOING_TO_DESTINATION, TRIP_DONE
    }

    public static void setDriverStateActive() {
        ParseUser driver = ParseUser.getCurrentUser();
        if (driver != null) {
            driver.put(Driver.STATE, DRIVER_STATES.ACTIVE);
            driver.saveInBackground();
        }
    }

    public static void setDriverStateInactive() {
        ParseUser driver = ParseUser.getCurrentUser();
        if (driver != null) {
            driver.put(Driver.STATE, DRIVER_STATES.INACTIVE);
            driver.saveInBackground();
        }
    }

    public static void setDriverStateGoingPickupCustomer() {
        ParseUser driver = ParseUser.getCurrentUser();
        if (driver != null) {
            driver.put(Driver.STATE, DRIVER_STATES.GOING_TO_PICKUP);
            driver.saveInBackground();
        }
    }

    public static void setDriverStatePickedupCustomer() {
        ParseUser driver = ParseUser.getCurrentUser();
        if (driver != null) {
            driver.put(Driver.STATE, DRIVER_STATES.PICKEDUP_CUSTOMER);
            driver.saveInBackground();
        }
    }

    public static void setDriverStateGoingDestination() {
        ParseUser driver = ParseUser.getCurrentUser();
        if (driver != null) {
            driver.put(Driver.STATE, DRIVER_STATES.GOING_TO_DESTINATION);
            driver.saveInBackground();
        }
    }

    public static void setDriverStateTripDone() {
        ParseUser driver = ParseUser.getCurrentUser();
        if (driver != null) {
            driver.put(Driver.STATE, DRIVER_STATES.TRIP_DONE);
            driver.saveInBackground();
        }
    }
}
