package com.xc0ffeelabs.taxicabdriver.services;

import com.parse.ParseUser;
import com.xc0ffeelabs.taxicabdriver.models.Driver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by skammila on 3/10/16.
 */
public class DriverStateManager {

    public static void setDriverStateActive() {
        ParseUser driver = ParseUser.getCurrentUser();
        if (driver != null) {
            driver.put(Driver.STATE, DriverStates.ACTIVE);
            driver.saveInBackground();
        }
    }

    public static void setDriverStateInactive() {
        ParseUser driver = ParseUser.getCurrentUser();
        if (driver != null) {
            driver.put(Driver.STATE, DriverStates.INACTIVE);
            driver.saveInBackground();
        }
    }

    public static void setDriverStateGoingPickupCustomer() {
        ParseUser driver = ParseUser.getCurrentUser();
        if (driver != null) {
            driver.put(Driver.STATE, DriverStates.GOING_TO_PICKUP);
            driver.saveInBackground();
        }
    }

    public static void setDriverStatePickedupCustomer() {
        ParseUser driver = ParseUser.getCurrentUser();
        if (driver != null) {
            driver.put(Driver.STATE, DriverStates.PICKEDUP_CUSTOMER);
            driver.saveInBackground();
        }
    }

    public static void setDriverStateGoingDestination() {
        ParseUser driver = ParseUser.getCurrentUser();
        if (driver != null) {
            driver.put(Driver.STATE, DriverStates.GOING_TO_DESTINATION);
            driver.saveInBackground();
        }
    }

    public static void setDriverStateTripDone() {
        ParseUser driver = ParseUser.getCurrentUser();
        if (driver != null) {
            driver.put(Driver.STATE, DriverStates.TRIP_DONE);
            driver.saveInBackground();
        }
    }

    public static void setDriverStateAcceptedRequest() {
        ParseUser driver = ParseUser.getCurrentUser();
        if (driver != null) {
            driver.put(Driver.STATE, DriverStates.ACCEPTED_CUSTOMER_REQUEST);
            driver.saveInBackground();
        }
    }

    public static void setDriverStateReachedCustomer() {
        ParseUser driver = ParseUser.getCurrentUser();
        if (driver != null) {
            driver.put(Driver.STATE, DriverStates.REACHED_CUSTOMER);
            driver.saveInBackground();
        }
    }

    public static List<String> getNextPossibleStates(String currState) {
        List<String> states = new ArrayList<String>();
        switch (currState){
            case DriverStates.INACTIVE:
                states.add(DriverStates.ACTIVE);
                break;
            case DriverStates.ACTIVE:
                states.add(DriverStates.INACTIVE);
                break;
            case DriverStates.ACCEPTED_CUSTOMER_REQUEST:
                states.add(DriverStates.GOING_TO_PICKUP);
                states.add(DriverStates.TRIP_CANCEL);
                break;
            case DriverStates.GOING_TO_PICKUP:
                states.add(DriverStates.REACHED_CUSTOMER);
                states.add(DriverStates.TRIP_CANCEL);
                break;
            case DriverStates.REACHED_CUSTOMER:
                states.add(DriverStates.PICKEDUP_CUSTOMER);
                states.add(DriverStates.TRIP_CANCEL);
                break;
            case DriverStates.PICKEDUP_CUSTOMER:
                states.add(DriverStates.GOING_TO_DESTINATION);
                states.add(DriverStates.TRIP_CANCEL);
                break;
            case DriverStates.GOING_TO_DESTINATION:
                states.add(DriverStates.REACHED_DESTINATION);
                states.add(DriverStates.TRIP_CANCEL);
                break;
            case DriverStates.REACHED_DESTINATION:
                states.add(DriverStates.ACTIVE);
                break;
            default:
                break;
        }

        return states;
    }

    public static List<String> getAllPossibleStates(String currState) {
        List<String> states = new ArrayList<String>();
        switch (currState){
            case DriverStates.INACTIVE:
                states.add(DriverStates.ACTIVE);
                break;
            case DriverStates.ACTIVE:
                states.add(DriverStates.INACTIVE);
                break;
            default:
                break;
        }

        return states;
    }
}
