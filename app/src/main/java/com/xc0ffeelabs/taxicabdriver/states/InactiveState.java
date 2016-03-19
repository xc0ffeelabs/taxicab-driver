package com.xc0ffeelabs.taxicabdriver.states;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.xc0ffeelabs.taxicabdriver.R;
import com.xc0ffeelabs.taxicabdriver.activities.MapActivityNew;
import com.xc0ffeelabs.taxicabdriver.fragments.InactiveControlsFragment;
import com.xc0ffeelabs.taxicabdriver.models.Driver;
import com.xc0ffeelabs.taxicabdriver.services.GPSTracker;
import com.xc0ffeelabs.taxicabdriver.services.LocationService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InactiveState implements State {

    private MapActivityNew mActivity;
    private GoogleMap mMap;
    private GoogleApiClient mApiClient;
    private Driver mDriver;
    private Marker mMarker;


    private static InactiveState mActiveState;

    public static InactiveState getInstance() {
        if (mActiveState == null) {
            mActiveState = new InactiveState();
        }
        return mActiveState;
    }

    private InactiveState() {
    }

    @Override
    public void enterState(MapActivityNew activity, Bundle data) {
        mActivity = activity;
        mMap = mActivity.getMap();
        mApiClient = mActivity.getApiClient();
        mDriver = mActivity.getmDriver();
        initialize();
    }

    private void initialize() {

        Intent intent = new Intent(mActivity, LocationService.class);
        mActivity.stopService(intent);


        //update driver state
//        Driver driver = (Driver) ParseUser.getCurrentUser();
        mDriver.put(Driver.STATE, StateManager.States.Inactive.toString());
        mDriver.saveInBackground();

        //set controls
        InactiveControlsFragment fm = InactiveControlsFragment.newInstance();
        FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fmControls, fm, "inactivecontrols");
        ft.commit();


        Location location = LocationServices.FusedLocationApi.getLastLocation(mApiClient);
        if (location != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
            mMap.moveCamera(cameraUpdate);
            mMarker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(android.R.drawable.ic_input_add)).position(latLng));
        }
    }

    @Override
    public void exitState() {
        mMarker.remove();
    }
}
