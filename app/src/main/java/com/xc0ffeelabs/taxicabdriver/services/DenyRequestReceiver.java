package com.xc0ffeelabs.taxicabdriver.services;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by skammila on 3/14/16.
 */
public class DenyRequestReceiver extends BroadcastReceiver {
    private static final String DENY_ACTION = "com.xc0ffeelabs.taxicabdriver.DENY_REQUEST";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Debug", "In Deny handler");
        String action = intent.getAction();
        Bundle bnd = intent.getExtras();
        String tripId = bnd.getString("tripId");
        String driverId = bnd.getString("driverId");
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(DriverNotificationReceiver.NOTIFICATION_ID);

        if(DENY_ACTION.equals(action)) {
            try {
                Log.d("Debug", "TripId "+ tripId);

                Map parameters = new HashMap();
                parameters.put("driverId", driverId);
                parameters.put("tripId", tripId);

                ParseCloud.callFunctionInBackground("driverDenyTrip", parameters, new FunctionCallback() {
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
//                ParseQuery tripQ = ParseQuery.getQuery("Trip");
//                ParseObject trip = tripQ.get(tripId);
//                trip.put("status", "driver-notfound");
//                trip.put("state", "driver-denied-trip-request");
//                trip.saveInBackground();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
