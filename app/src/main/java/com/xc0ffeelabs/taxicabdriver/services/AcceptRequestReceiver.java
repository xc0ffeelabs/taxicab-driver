package com.xc0ffeelabs.taxicabdriver.services;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.xc0ffeelabs.taxicabdriver.activities.MapActivity;
import com.xc0ffeelabs.taxicabdriver.models.Driver;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by skammila on 3/14/16.
 */
public class AcceptRequestReceiver extends BroadcastReceiver {
    private static final String ACCEPT_ACTION = "com.xc0ffeelabs.taxicabdriver.ACCEPT_REQUEST";
    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.d("Debug", "In Accept handler");
        String action = intent.getAction();
        Bundle bnd = intent.getExtras();
        String tripId = bnd.getString("tripId");
        String driverId = bnd.getString("driverId");
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(DriverNotificationReceiver.NOTIFICATION_ID);

        if(ACCEPT_ACTION.equals(action)) {
            try {
                Log.d("Debug", "TripId " + tripId);
//                Map parameters = new HashMap();
//                parameters.put("driverId", driverId);
//                parameters.put("tripId", tripId);

//                ParseCloud.callFunctionInBackground("driverAcceptTrip", parameters, new FunctionCallback() {
//                    @Override
//                    public void done(Object object, ParseException e) {
//                        if (e == null) {
//                            Log.i("Success", "Driver assigned to the trip");
//                        } else {
//                            Log.e("Error", "Driver not assigned to the trip");
//                        }
//                    }
//
//                    @Override
//                    public void done(Object o, Throwable throwable) {
//                        if (throwable == null) {
//                            Log.i("Success", "Driver assigned to the trip");
//                        } else {
//                            Log.e("Error", "Driver not assigned to the trip");
//                        }
//                    }
//                });

                ParseObject trip = ParseQuery.getQuery("Trip").get(tripId);
                trip.put("status", "confirmed");
                trip.put("state", TripStates.GOING_TO_PICKUP);
                trip.saveInBackground();

                ParseUser driver = ParseUser.getQuery().get(driverId);
                driver.put(Driver.STATE, DriverStates.IN_TRIP);
                driver.put("driver_currentTripId", tripId);
                Task saveDr = driver.saveInBackground();
                saveDr.onSuccess(new Continuation() {
                    @Override
                    public Object then(Task task) throws Exception {
                        Intent mapInt = new Intent(DriverNotificationReceiver.ACCEPT_REQUEST_LAUNCH_MAP);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(mapInt);
                        Intent intentMap = new Intent(context, MapActivity.class);
                        intentMap.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intentMap);
                        return null;
                    }
                });
//                driver.saveInBackground(new SaveCallback() {
//                    @Override
//                    public void done(ParseException e) {
//                        if (e == null) {
//                            Intent mapInt = new Intent(DriverNotificationReceiver.ACCEPT_REQUEST_LAUNCH_MAP);
//                            LocalBroadcastManager.getInstance(context).sendBroadcast(mapInt);
//                            Intent intentMap = new Intent(context, MapActivity.class);
//                            intentMap.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            context.startActivity(intentMap);
//                        } else {
//                            Log.e("Error", "Error while updating driver with trip info");
//                        }
//                    }
//                });



            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
