package com.xc0ffeelabs.taxicabdriver.services;

/**
 * Created by skammila on 3/16/16.
 */
public interface TripStates {
    public String ACCEPTED_CUSTOMER_REQUEST = "accepted_customer_request";
    public String TRIP_CANCEL = "trip_cancel";
    public String GOING_TO_PICKUP = "going_to_pickup";
    public String REACHED_CUSTOMER = "reached_customer";
    public String PICKEDUP_CUSTOMER = "pickedup_customer";
    public String GOING_TO_DESTINATION = "going_to_destination";
    public String REACHED_DESTINATION = "reached_destination";
    public String TRIP_DONE = "trip_done";
}
