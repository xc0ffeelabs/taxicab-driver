package com.xc0ffeelabs.taxicabdriver.states;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.xc0ffeelabs.taxicabdriver.R;
import com.xc0ffeelabs.taxicabdriver.activities.MapActivity;
import com.xc0ffeelabs.taxicabdriver.fragments.EnrouteDestinationControlsFragment;
import com.xc0ffeelabs.taxicabdriver.models.Driver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnrouteDestinationState implements State {

    private final static int REFRESH_INTERVAL = 10;

    private Map<String, Marker> mMarkerMap = new HashMap<>();
    private Marker mUserMarker;
    private LatLng mUserLocation;
//    private NearbyDrivers mNearbyDrivers;
    private volatile boolean mSortRequested = true;
    protected List<Driver> mSortedUsers = new ArrayList<>();
    private MapActivity mActivity;
    private GoogleMap mMap;
    private GoogleApiClient mApiClient;
    private Driver mDriver;


    private static EnrouteDestinationState mEnrouteDestinationState;

    public static EnrouteDestinationState getInstance() {
        if (mEnrouteDestinationState == null) {
            mEnrouteDestinationState = new EnrouteDestinationState();
        }
        return mEnrouteDestinationState;
    }

    private EnrouteDestinationState() {
    }

    @Override
    public void enterState(MapActivity activity, Bundle data) {
        mActivity = activity;
        mMap = mActivity.getMap();
        mApiClient = mActivity.getApiClient();
        mDriver = mActivity.getmDriver();
        initialize();
    }

    private void initialize() {

        //update driver state
//        Driver  driver = (Driver) ParseUser.getCurrentUser();
        mDriver.put(Driver.STATE, StateManager.States.EnrouteDestination.toString());
        mDriver.saveInBackground();

        //set controls
        FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fmControls, EnrouteDestinationControlsFragment.newInstance(), "enroutedestinationcontrols");
        ft.commit();
//        ControlsFragment.newInstance().setControlsInteraction(new ControlsFragment.ControlsInteraction() {
//            @Override
//            public void onPickupButtonClicked() {
//                requestToPickup();
//            }
//        });
//
//        try {
//            Location location = LocationServices.FusedLocationApi.getLastLocation(mApiClient);
//            if (location != null) {
//                mUserLocation = new LatLng(location.getLatitude(), location.getLongitude());
//                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mUserLocation, 10);
//                setUserMarker();
//                mMap.moveCamera(cameraUpdate);
//                startLocationUpdates();
//                fetchNearByDrivers();
//                mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
//                    @Override
//                    public void onCameraChange(CameraPosition cameraPosition) {
//                        repositionCamera(cameraPosition);
//                    }
//                });
//            } else {
//                Toast.makeText(mActivity, "Cound't retrieve current location. Please enable GPS location",
//                        Toast.LENGTH_SHORT).show();
//            }
//        } catch (SecurityException e) {
//            throw e;
//        }

    }

//    private void setUserMarker() {
//        if (mUserMarker == null) {
//            MarkerOptions markerOptions = new MarkerOptions()
//                    .position(mUserLocation)
//                    .title("Me")
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_black_48dp));
//            mUserMarker = mMap.addMarker(markerOptions);
//        } else {
//            mUserMarker.setPosition(mUserLocation);
//        }
//    }
//
//    private void requestToPickup() {
//        Bundle b = new Bundle();
//        List<String> drivers = new ArrayList<>();
//        for (User driver : mSortedUsers) {
//            drivers.add(driver.getObjectId());
//        }
//        PickupRequestedState.PickupRequestData data =
//                new PickupRequestedState.PickupRequestData(
//                        ParseUser.getCurrentUser().getObjectId(),
//                        mUserLocation,
//                        drivers);
//        b.putParcelable(PickupRequestedState.PickupRequestData.PICKUP_DATA, Parcels.wrap(data));
//        TaxiCabApplication.getStateManager().startState(StateManager.States.TripRequested, b);
//    }
//
//    private void repositionCamera(CameraPosition cameraPosition) {
//        displayAproximateTime("--");
//        mNearbyDrivers.setLocation(cameraPosition.target);
//        mNearbyDrivers.getNow();
//        mUserLocation = cameraPosition.target;
//        mSortRequested = true;
//        setUserMarker();
//    }

//    private void fetchNearByDrivers() {
//        mNearbyDrivers = TaxiCabApplication.getNearbyDrivers();
//        mNearbyDrivers.setLocation(mUserLocation);
//        mNearbyDrivers.setRadius(20);
//        mNearbyDrivers.setRefreshInterval(REFRESH_INTERVAL);
//        mNearbyDrivers.setQueryDriversCallback(new NearbyDrivers.QueryDriversCallback() {
//            @Override
//            public void onDriverLocationUpdate(final List<User> users) {
//                if (false) {
//                    /* TODO: Fix this */
//                    //Toast.makeText(mActivity, "No nearby drivers found", Toast.LENGTH_SHORT).show();
//                } else {
//                    addDriverMarkers(users);
//                    if (mSortRequested) {
//                        TravelTime.compute(mUserLocation, users, new TravelTime.TravelTimeComputed() {
//                            @Override
//                            public void onTravelTimeComputed(List<User> drivers) {
//                                mSortedUsers.clear();
//                                mSortedUsers.addAll(users);
//                                if (drivers != null && !drivers.isEmpty()) {
//                                    displayAproximateTime(drivers.get(0).getTravelTimeText());
//                                }
//                                mSortRequested = false;
//                            }
//                        });
//                    }
//                }
//            }
//
//            @Override
//            public void onFailed() {
//                Toast.makeText(mActivity, "Failed to get nearby drivers", Toast.LENGTH_SHORT).show();
//            }
//        });
//        mNearbyDrivers.startQueryDriverLocationUpdates();
//    }
//
//    private void displayAproximateTime(String text) {
//        if (mSortedUsers != null && !mSortedUsers.isEmpty()) {
//            ControlsFragment.newInstance().setApprTime(text);
//        }
//    }
//
//    private void addDriverMarkers(List<User> drivers) {
//        Set<String> mCurrentDrivers = new HashSet<>(mMarkerMap.keySet());
//        for (User driver : drivers) {
//            LatLng position = new LatLng(driver.getLocation().getLatitude(), driver.getLocation().getLongitude());
//            if (!mMarkerMap.containsKey(driver.getObjectId())) {
//                MarkerOptions markerOptions = new MarkerOptions().position(position).title(driver.getName());
//                Marker marker = mMap.addMarker(markerOptions);
//                mMarkerMap.put(driver.getObjectId(), marker);
//            } else {
//                Marker marker = mMarkerMap.get(driver.getObjectId());
//                marker.setPosition(position);
//            }
//            mCurrentDrivers.remove(driver.getObjectId());
//        }
//        for (String id: mCurrentDrivers) {
//            Marker marker = mMarkerMap.get(id);
//            marker.remove();
//            mMarkerMap.remove(id);
//        }
//    }

    @Override
    public void exitState() {
    }
}
