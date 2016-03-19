package com.xc0ffeelabs.taxicabdriver.states;

import android.app.Service;
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
import com.parse.ParseGeoPoint;
import com.xc0ffeelabs.taxicabdriver.R;
import com.xc0ffeelabs.taxicabdriver.activities.MapActivityNew;
import com.xc0ffeelabs.taxicabdriver.fragments.ActiveControlsFragment;
import com.xc0ffeelabs.taxicabdriver.models.Driver;
import com.xc0ffeelabs.taxicabdriver.services.LocationService;

public class ActiveState implements State {

    private MapActivityNew mActivity;
    private GoogleMap mMap;
    private GoogleApiClient mApiClient;
    private Driver mDriver;
    private Marker mMarker;


    private static ActiveState mActiveState;

    public static ActiveState getInstance() {
        if (mActiveState == null) {
            mActiveState = new ActiveState();
        }
        return mActiveState;
    }

    private ActiveState() {
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

        //update driver state
        mDriver.put(Driver.STATE, StateManager.States.Active.toString());


        //set controls
        FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fmControls, ActiveControlsFragment.newInstance(), "activecontrols");
        ft.commit();

        Location location = LocationServices.FusedLocationApi.getLastLocation(mApiClient);
        if (location != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
            mMap.moveCamera(cameraUpdate);
            mMarker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(android.R.drawable.ic_delete)).position(latLng));
        }

        mDriver.put(Driver.CURRENT_LOCATION, new ParseGeoPoint(location.getLatitude(), location.getLongitude()));
        mDriver.saveInBackground();

        Intent intent = new Intent(mActivity, LocationService.class);
        mActivity.startService(intent);

    }


    @Override
    public void exitState() {
        mMarker.remove();
    }
}
