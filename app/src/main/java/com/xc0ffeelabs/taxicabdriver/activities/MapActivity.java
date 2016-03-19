package com.xc0ffeelabs.taxicabdriver.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.xc0ffeelabs.taxicabdriver.R;
import com.xc0ffeelabs.taxicabdriver.models.Driver;
import com.xc0ffeelabs.taxicabdriver.services.DriverNotificationReceiver;
import com.xc0ffeelabs.taxicabdriver.services.DriverStateManager;
import com.xc0ffeelabs.taxicabdriver.services.DriverStates;
import com.xc0ffeelabs.taxicabdriver.services.GPSTracker;
import com.xc0ffeelabs.taxicabdriver.services.TripStates;
import com.xc0ffeelabs.taxicabdriver.templates.ILocationListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MapActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, ILocationListener {

    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 60000;  /* 60 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */

    private GPSTracker gpst;
    private Driver driver;
    private ParseObject trip;

    /*
     * Define a request code to send to Google Play services This code is
     * returned in Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            driver = (Driver)ParseUser.getCurrentUser();
            ParseQuery tripQ = ParseQuery.getQuery("Trip");
            try {
                driver = (Driver)driver.fetch();
                trip = tripQ.get(driver.getString("driver_currentTripId"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String tripState = trip != null? trip.getString(Driver.STATE) : null;
            manageDriverActionButtons(driver.getString(Driver.STATE), tripState);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    loadMap(map);
                }
            });
        } else {
            Toast.makeText(this, "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
        }

        gpst = new GPSTracker(this, driver);

        driver = (Driver)ParseUser.getCurrentUser();
        if (driver != null && driver.getString(Driver.STATE) == null) {
            driver.put(Driver.STATE, DriverStates.INACTIVE);
            driver.saveInBackground();
        } else if (driver == null) {
//            Toast.makeText(this, "Error - You didn't login. Please signup with GoTaxi.", Toast.LENGTH_SHORT).show();
            return;
        }

        scheduleLocationUpdates();

        setupPushnotifications();

        manageDriverActionButtons(driver.getString(Driver.STATE), null);

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
                new IntentFilter());

    }

    protected void loadMap(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            // Map is ready
//            Toast.makeText(this, "Map Fragment was loaded properly!", Toast.LENGTH_SHORT).show();
            MapActivityPermissionsDispatcher.getMyLocationWithCheck(this);
        } else {
            Toast.makeText(this, "Error - Map was null!!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MapActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void getMyLocation() {
        if (map != null) {
            // Now that map has loaded, let's get our location!
            map.setMyLocationEnabled(true);
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            connectClient();
        }
    }

    protected void connectClient() {
        // Connect the client.
        if (isGooglePlayServicesAvailable() && mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    /*
     * Called when the Activity becomes visible.
    */
    @Override
    protected void onStart() {
        super.onStart();
        connectClient();
    }

    /*
	 * Called when the Activity is no longer visible.
	 */
    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    protected void onResume() {
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(DriverNotificationReceiver.ACCEPT_REQUEST_LAUNCH_MAP);
//        registerReceiver(receiver, filter);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
                new IntentFilter(DriverNotificationReceiver.ACCEPT_REQUEST_LAUNCH_MAP));
        super.onResume();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
//        unregisterReceiver(receiver);
        super.onPause();
    }

    /*
     * Handle results returned to the FragmentActivity by Google Play services
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Decide what to do based on the original request code
        switch (requestCode) {

            case CONNECTION_FAILURE_RESOLUTION_REQUEST:
			/*
			 * If the result code is Activity.RESULT_OK, try to connect again
			 */
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        mGoogleApiClient.connect();
                        break;
                }

        }
    }

    private boolean isGooglePlayServicesAvailable() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates", "Google Play services is available.");
            return true;
        } else {
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);

            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(errorDialog);
                errorFragment.show(getSupportFragmentManager(), "Location Updates");
            }

            return false;
        }
    }

    /*
     * Called by Location Services when the request to connect the client
     * finishes successfully. At this point, you can request the current
     * location or start periodic updates
     */
    @Override
    public void onConnected(Bundle dataBundle) {
        // Display the connection status
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location != null) {
//            Toast.makeText(this, "GPS location was found!", Toast.LENGTH_SHORT).show();
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
            map.animateCamera(cameraUpdate);
        } else {
            Toast.makeText(this, "Current location was null, enable GPS on emulator!", Toast.LENGTH_SHORT).show();
        }
//        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
    }

    public void onLocationChanged(Location location) {
        // Report to the UI that the location was updated
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

    }

    /*
     * Called by Location Services if the connection to the location client
     * drops because of an error.
     */
    @Override
    public void onConnectionSuspended(int i) {
        if (i == CAUSE_SERVICE_DISCONNECTED) {
            Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
        } else if (i == CAUSE_NETWORK_LOST) {
            Toast.makeText(this, "Network lost. Please re-connect.", Toast.LENGTH_SHORT).show();
        }
    }

    /*
     * Called by Location Services if the attempt to Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(),
                    "Sorry. Location services not available to you", Toast.LENGTH_SHORT).show();
        }
    }


    // Define a DialogFragment that displays the error dialog
    public static class ErrorDialogFragment extends DialogFragment {

        // Global field to contain the error dialog
        private Dialog mDialog;

        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        // Return a Dialog to the DialogFragment.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }


    public void scheduleLocationUpdates() {
        if (driver == null)
            driver = (Driver)ParseUser.getCurrentUser();
        if (driver != null && driver.getString(Driver.STATE) != null && !(driver.getString(Driver.STATE)).equals(DriverStates.INACTIVE)) {
            //dont track the location in INACTIVE state
            gpst.startLocationTracking();
        } else {
            gpst.stopLocationTracking();
        }

    }

    @Override
    public void updateLocation(Location location) {
        Log.i("Location", "Longitude: " + location.getLongitude() + " Latitude: " + location.getLatitude());
        uploadDriverLocation(location.getLatitude(), location.getLongitude());
    }

//    @Override
//    public void updateLongitude(Double lang) {
//        Log.i("Location", "Longitude: " + lang);
//    }
//
//    @Override
//    public void updateLatitude(Double lat) {
//        Log.i("Location", " Latitude: "+ lat);
//    }

    private void uploadDriverLocation(Double latitude, Double longitude) {
        if (driver == null)
             driver = (Driver)ParseUser.getCurrentUser();
        if (driver != null) {
            driver.put(Driver.CURRENT_LOCATION, new ParseGeoPoint(latitude, longitude));
            driver.saveInBackground();
        }
    }

    private void manageDriverActionButtons(String currentState, String tripState) {
//        String tripState = "";
        ParseQuery tripQ = ParseQuery.getQuery("Trip");
        if (currentState.equals(DriverStates.IN_TRIP) && tripState == null) {
            try {
                trip = tripQ.get(driver.getString("driver_currentTripId"));
                tripState = trip.getString("state");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        String toastMsg = "";
//        List<String> nextStates = DriverStateManager.getNextPossibleStates(currentState, tripState);
//        for (int i = 0; i < nextStates.size(); i++) {
//            int id = -1;
//            switch (nextStates.get(i)){
//                case DriverStates.INACTIVE:
//                    id = R.id.inactiveBtn;
////                    toastMsg = "You can Go Active";
//                    break;
//                case DriverStates.ACTIVE:
//                    id = R.id.activeBtn;
//                    toastMsg = "You are in queue for next trip.";
//                    break;
//                case TripStates.GOING_TO_PICKUP:
//                    id = R.id.goingPickupBtn;
//                    toastMsg = "Go, pick the customer";
//                    break;
//                case TripStates.REACHED_CUSTOMER:
//                    id = R.id.reachedCustomerBtn;
//                    break;
//                case TripStates.PICKEDUP_CUSTOMER:
//                    id = R.id.pickedupCustomerBtn;
//                    toastMsg = "Go, pick the customer";
//                    break;
//                case TripStates.GOING_TO_DESTINATION:
//                    id = R.id.goignDestinationBtn;
//                    break;
//                case TripStates.REACHED_DESTINATION:
//                    id = R.id.reachedDestinationBtn;
//                    toastMsg = "Start Driving to destination";
//                    break;
//                default:
//                    break;
//
//            }
//            if (id != -1) {
////                Button btn = (Button)findViewById(id);
////                btn.setVisibility(View.VISIBLE);
//                toggleOtherButtons(id);
//            }
//
//            if (toastMsg.length() > 0) {
//                Toast.makeText(this, toastMsg, Toast.LENGTH_SHORT).show();
//            }
//
//        }
        if (currentState.equals(DriverStates.ACTIVE) || currentState.equals(DriverStates.INACTIVE)) {
            //hide cancel button
            findViewById(R.id.cancelTripBtn).setVisibility(View.GONE);
        } else {
            //show cancel button
            findViewById(R.id.cancelTripBtn).setVisibility(View.VISIBLE);
        }
    }




    public void goActive(View view) {
        driver.put(Driver.STATE, DriverStates.ACTIVE);
        driver.saveInBackground();
        manageDriverActionButtons(DriverStates.ACTIVE, null);
        scheduleLocationUpdates();
    }

    public void goInactive(View view) {
        driver.put(Driver.STATE, DriverStates.INACTIVE);
        driver.saveInBackground();
        manageDriverActionButtons(DriverStates.INACTIVE, null);
        scheduleLocationUpdates();
    }

    public void goPickupCustomer(View view) {
        trip.put(Driver.STATE, TripStates.GOING_TO_PICKUP);
        trip.saveInBackground();
        manageDriverActionButtons(DriverStates.IN_TRIP, TripStates.GOING_TO_PICKUP);

    }

    public void reachedCustomer(View view) {
        trip.put(Driver.STATE, TripStates.REACHED_CUSTOMER);
        trip.saveInBackground();
        manageDriverActionButtons(DriverStates.IN_TRIP, TripStates.REACHED_CUSTOMER);


        Map parameters = new HashMap();
        parameters.put("driverId", driver.getObjectId());
        parameters.put("tripId", trip.getObjectId());


        ParseCloud.callFunctionInBackground("driverReachedUser", parameters, new FunctionCallback() {
            @Override
            public void done(Object object, ParseException e) {
                if (e == null) {
                    Log.i("Success", "Driver assigned to the trip");
                } else {
                    Log.e("Error", "Driver not assigned to the trip");
                }
            }

            @Override
            public void done(Object o, Throwable throwable) {
                if (throwable == null) {
                    Log.i("Success", "Driver assigned to the trip");
                } else {
                    Log.e("Error", "Driver not assigned to the trip");
                }
            }
        });
    }

    public void pickedupCustomer(View view) {
        trip.put(Driver.STATE, TripStates.PICKEDUP_CUSTOMER);
        trip.saveInBackground();
        manageDriverActionButtons(DriverStates.IN_TRIP, TripStates.PICKEDUP_CUSTOMER);
    }

    public void goingDestination(View view) {
        trip.put(Driver.STATE, TripStates.GOING_TO_DESTINATION);
        trip.saveInBackground();
        manageDriverActionButtons(DriverStates.IN_TRIP, TripStates.GOING_TO_DESTINATION);
    }

    public void reachedDestination(View view) {
        trip.put(Driver.STATE, TripStates.REACHED_DESTINATION);
        trip.saveInBackground();
        driver.put(Driver.STATE, DriverStates.ACTIVE);
        driver.put("driver_currentTripId", "");
        driver.saveInBackground();
        manageDriverActionButtons(DriverStates.ACTIVE, TripStates.REACHED_DESTINATION);
    }

    public void cancelTrip(View view) {
        trip.put(Driver.STATE, TripStates.TRIP_CANCEL);
        trip.saveInBackground();
        driver.put(Driver.STATE, DriverStates.ACTIVE);
        driver.put("driver_currentTripId", "");
        driver.saveInBackground();
        manageDriverActionButtons(DriverStates.ACTIVE, TripStates.TRIP_CANCEL);
    }

    private void toggleOtherButtons(int id) {
        int btns[] = {R.id.activeBtn, R.id.inactiveBtn, R.id.goingPickupBtn, R.id.reachedCustomerBtn, R.id.pickedupCustomerBtn, R.id.goignDestinationBtn, R.id.reachedDestinationBtn, R.id.cancelTripBtn};
        for (int i = 0; i < btns.length; i++) {
            if (btns[i] != id) {
                findViewById(btns[i]).setVisibility(View.GONE);
            }
        }
        findViewById(id).setVisibility(View.VISIBLE);
    }

    private void setupPushnotifications() {
        if (driver != null) {
            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
            installation.put("ownerId", driver.getObjectId());
            installation.saveInBackground();
        }
    }


}
