package com.xc0ffeelabs.taxicabdriver.states;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.xc0ffeelabs.taxicabdriver.R;
import com.xc0ffeelabs.taxicabdriver.activities.MapActivity;
import com.xc0ffeelabs.taxicabdriver.fragments.EnrouteDestinationControlsFragment;
import com.xc0ffeelabs.taxicabdriver.models.Driver;
import com.xc0ffeelabs.taxicabdriver.network.GMapV2Direction;
import com.xc0ffeelabs.taxicabdriver.services.LocationService;
import com.xc0ffeelabs.taxicabdriver.stubs.NavigateDriverToUserStub;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnrouteDestinationState implements State {

    private Marker mDstMarker;
    private LatLng mDstLocation;
//    private NearbyDrivers mNearbyDrivers;
    private volatile boolean mSortRequested = true;
    protected List<Driver> mSortedUsers = new ArrayList<>();
    private MapActivity mActivity;
    private GoogleMap mMap;
    private GoogleApiClient mApiClient;
    private Driver mDriver;
    private ParseObject mTrip;


    private static EnrouteDestinationState mEnrouteDestinationState;
    private LatLng mDriverLocation;
    private Marker mMarker;
    private boolean mRefreshRequested = false;
    private static final int MSG_REFRESH_LOCATION = 1;
    private static final int REFRESH_INTERVAL = 7;  // in sec
    private MyHandler mHandler = new MyHandler(Looper.getMainLooper());



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
        mTrip = mActivity.getmTrip();
        initialize();
    }

    private void initialize() {

        /* TODO: Remove this in final app */
        mActivity.stopService(new Intent(mActivity, LocationService.class));

        //update driver state
//        Driver  driver = (Driver) ParseUser.getCurrentUser();
        mDriver.put(Driver.STATE, StateManager.States.EnrouteDestination.toString());
        mDriver.saveInBackground();


        if (mDriver == null || mTrip == null) {
            Log.e("EnrouteDestination", "To initiate this state, driver object and trip are needed");
            StateManager.getInstance().startState(StateManager.States.Active, null);
            return;
        }

        //send notification to the user that driver arriverd for pickup
        Map parameters = new HashMap();
        parameters.put("driverId", mDriver.getObjectId());
        parameters.put("tripId", mTrip.getObjectId());


        ParseCloud.callFunctionInBackground("driverReachedUser", parameters, new FunctionCallback() {
            @Override
            public void done(Object object, ParseException e) {
                if (e == null) {
                    Log.i("EnrouteDestination", "Driver assigned to the trip");
                } else {
                    Log.e("EnrouteDestination", "Driver not assigned to the trip");
                }
            }

            @Override
            public void done(Object o, Throwable throwable) {
                if (throwable == null) {
                    Log.i("EnrouteDestination", "Driver assigned to the trip");
                } else {
                    Log.e("EnrouteDestination", "Driver not assigned to the trip");
                }
            }
        });

        //set controls
        FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fmControls, EnrouteDestinationControlsFragment.newInstance(), "enroutedestinationcontrols");
        ft.commit();

        addDriverMarker();
        addDestinationMarker();
    }


    private void addDriverMarker() {
        //TODO Uncomment in final app

//        Location location = LocationServices.FusedLocationApi.getLastLocation(mApiClient);
//        if (location != null) {
//            mDriverLocation = new LatLng(location.getLatitude(), location.getLongitude());
//            mMarker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(android.R.drawable.ic_delete)).position(mDriverLocation));
//
//        } else {
//            throw new IllegalStateException("Driver location not found while preparing directions map");
//        }

        /*TODO Remove for final APP    */
        ParseGeoPoint pnt = mDriver.getParseGeoPoint("currentLocation");
        mDriverLocation = new LatLng(pnt.getLatitude(), pnt.getLongitude());
        mMarker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(android.R.drawable.ic_delete)).position(mDriverLocation));

    }

    private void addDestinationMarker() {
        fetchTripObject();
    }

    private void fetchTripObject() {
        mTrip.fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    if (mTrip != null) {
                        try {
//                            ParseGeoPoint dstLocation = mTrip.getParseGeoPoint("destinationLocation");
                            ParseGeoPoint dstLocation = new ParseGeoPoint(37.4064495, -121.9439531);
                            mDstLocation = new LatLng(dstLocation.getLatitude(), dstLocation.getLongitude());
                            MarkerOptions markerOptions = new MarkerOptions()
                                    .position(mDstLocation)
                                    .icon(BitmapDescriptorFactory.fromResource(android.R.drawable.ic_input_add));
                            mDstMarker = mMap.addMarker(markerOptions);
                            zoomCamera();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    } else {
                        throw new IllegalStateException("Cant find the User location to prepare directions map");
                    }
                } else {
                    Log.d("NAYAN", "SOmething wrong while fething. e = " + e.getMessage());
                }
            }
        });
    }

    private void zoomCamera() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(mDstLocation);
        builder.include(mDriverLocation);
        LatLngBounds bounds = builder.build();
        int padding  = (int) mActivity.getResources().getDimension(R.dimen.map_offset);
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.animateCamera(cu);
        showRoute();

        /* TODO: Remove this for final app */
        mRefreshRequested = true;
        mHandler.sendEmptyMessage(MSG_REFRESH_LOCATION);
        new NavigateDriverToUserStub(mDriverLocation, mDstLocation, mDriver, new NavigateDriverToUserStub.ToReached() {
            @Override
            public void onDestinationReached() {
                moveToDestState();
            }
        }).run();


    }

    private void showRoute() {
        new GMapV2Direction(mDriverLocation, mDstLocation, new GMapV2Direction.RouteReady() {
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
            mMap.addPolyline(rectLine);
        }
    }

    private void moveToDestState() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showDestReached();
                mRefreshRequested = false;
            }
        });
    }

    private void showDestReached() {
        new AlertDialog.Builder(mActivity)
                .setTitle(R.string.destination_reached_title)
                .setMessage(R.string.destination_reached_text)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        changeState();
                        notifyUser();
                    }
                }).create().show();
    }

    public  void notifyUser () {
        //send notification to the user that driver arriverd for pickup
        Map parameters = new HashMap();
        parameters.put("driverId", mDriver.getObjectId());
        parameters.put("tripId", mTrip.getObjectId());
        ParseCloud.callFunctionInBackground("reachedDestination", parameters, new FunctionCallback() {
            @Override
            public void done(Object object, ParseException e) {
                if (e == null) {
                    Log.i("EnrouteDestination", "Reached destination");
                } else {
                    Log.e("EnrouteDestination", "Error: "+ e.getMessage());
                }
            }

            @Override
            public void done(Object o, Throwable throwable) {
                if (throwable == null) {
                    Log.i("EnrouteDestination", "Reached destination");
                } else {
                    Log.e("EnrouteDestination", "Error: "+ throwable.getMessage());
                }
            }
        });
    }


    @Override
    public void exitState() {
        mMap.clear();
        if (mMarker != null) {
            mMarker.remove();
        }

        if (mDstMarker != null) {
            mDstMarker.remove();
        }
    }


    //TODO Remove this in final app

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
                    }
                    break;
                default:
                    throw new UnsupportedOperationException("Can't handle");
            }
        }
    }

    private void fetchDriverPosition() {
        mDriver.fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    updateDriverMarker();
                } else {
                    Log.e("Enroute", "Unable to update driver location");
                }
            }
        });
    }

    private void updateDriverMarker() {
        ParseGeoPoint pnt = mDriver.getParseGeoPoint("currentLocation");
        mDriverLocation = new LatLng(pnt.getLatitude(), pnt.getLongitude());
        mMarker.setPosition(mDriverLocation);
    }
}
