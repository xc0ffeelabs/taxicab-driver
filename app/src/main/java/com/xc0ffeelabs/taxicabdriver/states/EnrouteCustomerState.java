package com.xc0ffeelabs.taxicabdriver.states;

import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.xc0ffeelabs.taxicabdriver.R;
import com.xc0ffeelabs.taxicabdriver.activities.MapActivity;
import com.xc0ffeelabs.taxicabdriver.fragments.EnrouteCustomerControlsFragment;
import com.xc0ffeelabs.taxicabdriver.models.Driver;
import com.xc0ffeelabs.taxicabdriver.network.GMapV2Direction;
import com.xc0ffeelabs.taxicabdriver.stubs.NavigateDriverToUserStub;

import org.w3c.dom.Document;

import java.util.ArrayList;

public class EnrouteCustomerState implements State {

    private MapActivity mActivity;
    private GoogleMap mMap;
    private GoogleApiClient mApiClient;
    private Driver mDriver;
    private Marker mMarker;
    private Marker mUserMarker;
    private ParseObject mTrip;
    private ParseUser mUser;
    private LatLng mUserLocation;
    private LatLng mDriverLocation;
    private Polyline mLine;
    private boolean mRefreshRequested;
    private Handler mHandler = new MyHandler(Looper.getMainLooper());
    private static final int MSG_REFRESH_LOCATION = 1;
    private static final int MSG_STOP_REFRESH_LOCATION = 2;
    private static final int REFRESH_INTERVAL = 7;  // in sec

    private static EnrouteCustomerState mEnrouteCustomerState;

    public static EnrouteCustomerState getInstance() {
        if (mEnrouteCustomerState == null) {
            mEnrouteCustomerState = new EnrouteCustomerState();
        }
        return mEnrouteCustomerState;
    }

    private EnrouteCustomerState() {
    }

    @Override
    public void enterState(MapActivity activity, Bundle data) {
        mActivity = activity;
        mMap = mActivity.getMap();
        mApiClient = mActivity.getApiClient();
        mDriver = mActivity.getmDriver();
        mUser = mActivity.getmTripUser();
        mTrip = mActivity.getmTrip();
        initialize();
    }

    private void initialize() {

        //update driver state
        mDriver.put(Driver.STATE, StateManager.States.EnrouteCustomer.toString());
        mDriver.saveInBackground();

        if (mUser == null) {
            Log.e("EnrouteCustomerState", "To initiate this state, user object is needed");
            StateManager.getInstance().startState(StateManager.States.Active, null);
            return;
        }

        //set controls
        FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fmControls, EnrouteCustomerControlsFragment.newInstance(), "enroutecustomercontrols");
        ft.commit();

        addDriverMarker();
        addUserMarker();

    }

    private void addDriverMarker() {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mApiClient);
        if (location != null) {
            mDriverLocation = new LatLng(location.getLatitude(), location.getLongitude());
            mMarker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(android.R.drawable.ic_delete)).position(mDriverLocation));

        } else {
//            throw new IllegalStateException("Driver location not found while preparing directions map");
        }

    }

    private void addUserMarker() {
        if (mUser != null) {
            try {
                ParseGeoPoint userLocation = mUser.getParseGeoPoint("currentLocation");
                mUserLocation = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(mUserLocation)
                        .icon(BitmapDescriptorFactory.fromResource(android.R.drawable.ic_btn_speak_now));
                mUserMarker = mMap.addMarker(markerOptions);
                zoomCamera();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
//            throw new IllegalStateException("Cant find the User location to prepare directions map");
        }
    }

    private void zoomCamera() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(mUserLocation);
        builder.include(mDriverLocation);
        LatLngBounds bounds = builder.build();
        int padding  = (int) mActivity.getResources().getDimension(R.dimen.map_offset);
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.animateCamera(cu);
        showRoute();
        updateDriverLocation();

//        /* TODO: Remove this for final app */
        new NavigateDriverToUserStub(mDriverLocation, mUserLocation, mDriver, new NavigateDriverToUserStub.DriverArrived() {
            @Override
            public void onDriverArrived() {
                moveToDestState();
            }
        }).run();
    }

    private void moveToDestState() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showDriverArrived();
            }
        });
        mRefreshRequested = false;
    }

    private void showDriverArrived() {
        new AlertDialog.Builder(mActivity)
                .setTitle(R.string.driver_arrived)
                .setMessage(R.string.driver_arrived_text)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        changeState();
                    }
                }).create().show();
    }

    private void updateDriverLocation() {
        mRefreshRequested = true;
        mHandler.sendEmptyMessage(MSG_REFRESH_LOCATION);
    }

    private void showRoute() {
        new GMapV2Direction(mDriverLocation, mUserLocation, new GMapV2Direction.RouteReady() {
            @Override
            public void onRouteReady(Document document) {
                drawRoute(document);
            }
        }).execute();
    }

    private void drawRoute(Document doc) {
        if (doc != null) {
            ArrayList<LatLng> directionPoint = GMapV2Direction.getDirection(doc);
            PolylineOptions rectLine = new PolylineOptions().width(10).color(
                    Color.BLUE);

            for (int i = 0; i < directionPoint.size(); i++) {
                rectLine.add(directionPoint.get(i));
            }
            mLine = mMap.addPolyline(rectLine);
        }
    }

    private class MyHandler extends Handler {

        MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REFRESH_LOCATION:
                    if (mRefreshRequested) {
                        fetchDriverPosition();
                        sendEmptyMessageDelayed(MSG_REFRESH_LOCATION, REFRESH_INTERVAL * 1000);
                    } else {
                        sendEmptyMessage(MSG_STOP_REFRESH_LOCATION);
                    }
                    break;
                case MSG_STOP_REFRESH_LOCATION:
                    break;
                default :
                    throw new UnsupportedOperationException("Can't handle");
            }
        }
    }

    private void fetchDriverPosition() {
        mDriver.fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    updateDriverLocation();
                } else {
                    Log.e("EnrouteCustomerState", "Unable to update driver location");
                }
            }
        });
    }


    @Override
    public void exitState() {
        if (mMarker != null) {
            mMarker.remove();
        }

        if (mUserMarker != null) {
            mUserMarker.remove();
        }
        mRefreshRequested = false;
    }
}
