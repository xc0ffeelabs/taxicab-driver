package com.xc0ffeelabs.taxicabdriver.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.xc0ffeelabs.taxicabdriver.R;
import com.xc0ffeelabs.taxicabdriver.models.Driver;
import com.xc0ffeelabs.taxicabdriver.services.DriverStateManager;
import com.xc0ffeelabs.taxicabdriver.services.DriverStates;
import com.xc0ffeelabs.taxicabdriver.services.GPSTracker;
import com.xc0ffeelabs.taxicabdriver.templates.ILocationListener;

import java.util.List;

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
    private ParseUser user;

    /*
     * Define a request code to send to Google Play services This code is
     * returned in Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

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

        gpst = new GPSTracker(this, this);

        user = ParseUser.getCurrentUser();
        if (user != null && user.getString(Driver.STATE) == null) {
            user.put(Driver.STATE, DriverStates.INACTIVE);
            user.saveInBackground();
        } else if (user == null) {
            Toast.makeText(this, "Error - You didn't login. Please signup with GoTaxi.", Toast.LENGTH_SHORT).show();
            return;
        }

        scheduleLocationUpdates();

        setupPushnotifications();

        manageDriverActionButtons(user.getString(Driver.STATE));



    }

    protected void loadMap(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            // Map is ready
            Toast.makeText(this, "Map Fragment was loaded properly!", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "GPS location was found!", Toast.LENGTH_SHORT).show();
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
                    "Sorry. Location services not available to you", Toast.LENGTH_LONG).show();
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

    // Setup a recurring alarm every half hour
    public void scheduleLocationUpdates() {
//        // Construct an intent that will execute the AlarmReceiver
//        Intent intent = new Intent(getApplicationContext(), LocationBoradcastReceiver.class);
//        // Create a PendingIntent to be triggered when the alarm goes off
//        final PendingIntent pIntent = PendingIntent.getBroadcast(this, LocationBoradcastReceiver.REQUEST_CODE,
//                intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        // Setup periodic alarm every 5 seconds
//        long firstMillis = System.currentTimeMillis(); // alarm is set right away
//        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
//        // First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
//        // Interval can be INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY
//        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
//                1000 * 30, pIntent);
//
//
//
        if (user == null)
            user  = ParseUser.getCurrentUser();
        if (user != null && user.getString(Driver.STATE) != null && !(user.getString(Driver.STATE)).equals(DriverStates.INACTIVE)) {
            //dont track the location in INACTIVE state
            gpst.startLocationTracking();
        } else {
            gpst.stopLocationTracking();
        }

    }

//    public void cancelAlarm() {
//        Intent intent = new Intent(getApplicationContext(), LocationBoradcastReceiver.class);
//        final PendingIntent pIntent = PendingIntent.getBroadcast(this, LocationBoradcastReceiver.REQUEST_CODE,
//                intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
//        alarm.cancel(pIntent);
//    }

    @Override
    public void updateLocation(Location location) {
        Log.i("Location", "Longitude: " + location.getLongitude() + " Latitude: " + location.getLatitude());
        uploadDriverLocation(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void updateLongitude(Double lang) {
        Log.i("Location", "Longitude: " + lang);
    }

    @Override
    public void updateLatitude(Double lat) {
        Log.i("Location", " Latitude: "+ lat);
    }

    private void uploadDriverLocation(Double latitude, Double longitude) {
        if (user == null)
             user  = ParseUser.getCurrentUser();
        if (user != null) {
            user.put(Driver.CURRENT_LOCATION, new ParseGeoPoint(latitude, longitude));
            user.saveInBackground();
        }
    }

    private void manageDriverActionButtons(String currentState) {
        List<String> nextStates = DriverStateManager.getNextPossibleStates(currentState);
        for (int i = 0; i < nextStates.size(); i++) {
            int id = -1;
            switch (nextStates.get(i)){
                case DriverStates.INACTIVE:
                    id = R.id.inactiveBtn;
                    break;
                case DriverStates.ACTIVE:
                    id = R.id.activeBtn;
                    break;
                case DriverStates.GOING_TO_PICKUP:
                    id = R.id.goingPickupBtn;
                    break;
                case DriverStates.REACHED_CUSTOMER:
                    id = R.id.reachedCustomerBtn;
                    break;
                case DriverStates.PICKEDUP_CUSTOMER:
                    id = R.id.pickedupCustomerBtn;
                    break;
                case DriverStates.GOING_TO_DESTINATION:
                    id = R.id.goignDestinationBtn;
                case DriverStates.REACHED_DESTINATION:
                    id = R.id.reachedDestinationBtn;
                    break;
                default:
                    break;

            }
            if (id != -1) {
//                Button btn = (Button)findViewById(id);
//                btn.setVisibility(View.VISIBLE);
                toggleOtherButtons(id);
            }
        }
        if (currentState.equals(DriverStates.ACTIVE) || currentState.equals(DriverStates.INACTIVE)) {
            //hide cancel button
            findViewById(R.id.cancelTripBtn).setVisibility(View.GONE);
        } else {
            //show cancel button
            findViewById(R.id.cancelTripBtn).setVisibility(View.VISIBLE);
        }
    }


    public void goActive(View view) {
        user.put(Driver.STATE, DriverStates.ACTIVE);
        user.saveInBackground();
        manageDriverActionButtons(DriverStates.ACTIVE);
    }

    public void goInactive(View view) {
        user.put(Driver.STATE, DriverStates.INACTIVE);
        user.saveInBackground();
        manageDriverActionButtons(DriverStates.INACTIVE);
    }

    public void goPickupCustomer(View view) {
        user.put(Driver.STATE, DriverStates.GOING_TO_PICKUP);
        user.saveInBackground();
        manageDriverActionButtons(DriverStates.GOING_TO_PICKUP);
    }

    public void reachedCustomer(View view) {
        user.put(Driver.STATE, DriverStates.REACHED_CUSTOMER);
        user.saveInBackground();
        manageDriverActionButtons(DriverStates.REACHED_CUSTOMER);
    }

    public void pickedupCustomer(View view) {
        user.put(Driver.STATE, DriverStates.PICKEDUP_CUSTOMER);
        user.saveInBackground();
        manageDriverActionButtons(DriverStates.PICKEDUP_CUSTOMER);
    }

    public void goingDestination(View view) {
        user.put(Driver.STATE, DriverStates.GOING_TO_DESTINATION);
        user.saveInBackground();
        manageDriverActionButtons(DriverStates.GOING_TO_DESTINATION);
    }

    public void reachedDestination(View view) {
        user.put(Driver.STATE, DriverStates.REACHED_DESTINATION);
        user.saveInBackground();
        manageDriverActionButtons(DriverStates.REACHED_DESTINATION);
    }

    private void toggleOtherButtons(int id) {
        int btns[] = {R.id.activeBtn, R.id.inactiveBtn, R.id.goingPickupBtn, R.id.reachedCustomerBtn, R.id.pickedupCustomerBtn, R.id.goignDestinationBtn, R.id.reachedDestinationBtn};
        for (int i = 0; i < btns.length; i++) {
            if (btns[i] != id) {
                findViewById(btns[i]).setVisibility(View.GONE);
            }
        }
        findViewById(id).setVisibility(View.VISIBLE);
    }

    private void setupPushnotifications() {
        if (user != null) {
            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
            installation.put("ownerId", user.getObjectId());
            installation.saveInBackground();
        }
    }
}
