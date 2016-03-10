package com.xc0ffeelabs.taxicabdriver.services;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.xc0ffeelabs.taxicabdriver.templates.ILocationListener;

/**
 * Created by skammila on 3/9/16.
 */
public class LocationService extends IntentService implements ILocationListener{
    public LocationService() {
        super("LocationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Do the task here
        Log.i("LocationService", "Update Driver location");
        new GPSTracker(getApplicationContext(), this);
    }

    @Override
    public void updateLatitude(Double lat) {

    }

    @Override
    public void updateLongitude(Double lang) {

    }

    @Override
    public void updateLocation(Location location) {
        Log.i("LocationService", "Latitude: " + location.getLatitude() + " Longitude" + location.getLongitude());
    }
}