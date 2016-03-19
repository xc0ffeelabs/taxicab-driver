package com.xc0ffeelabs.taxicabdriver.services;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.xc0ffeelabs.taxicabdriver.activities.MapActivity;
import com.xc0ffeelabs.taxicabdriver.models.Driver;
import com.xc0ffeelabs.taxicabdriver.states.StateManager;

/**
 * Created by skammila on 3/14/16.
 */
public class AcceptRequestReceiver extends BroadcastReceiver {
    private static final String ACCEPT_ACTION = "com.xc0ffeelabs.taxicabdriver.ACCEPT_REQUEST";
    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.d("AcceptRequestReceiver", "In Accept handler");
        String action = intent.getAction();
        Bundle bnd = intent.getExtras();
        final String tripId = bnd.getString("tripId");
        String driverId = bnd.getString("driverId");
        final String userId = bnd.getString("userId");
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(DriverNotificationReceiver.NOTIFICATION_ID);

        if(ACCEPT_ACTION.equals(action)) {
            try {
                Log.d("AcceptRequestReceiver", "TripId " + tripId);

                ParseObject trip = ParseQuery.getQuery("Trip").get(tripId);
                trip.put("status", "confirmed");
                trip.put("state", TripStates.GOING_TO_PICKUP);
                trip.saveInBackground();

                ParseUser driver = ParseUser.getQuery().get(driverId);
                driver.put(Driver.STATE, StateManager.States.EnrouteCustomer.toString());
                driver.put("driver_currentTripId", tripId);
                driver.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Intent intentMap = new Intent(context, MapActivity.class);
                            intentMap.putExtra("tripId", tripId);
                            intentMap.putExtra("tripUserId", userId);
                            intentMap.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intentMap);
                        } else {
                            Log.d("AcceptRequestReceiver", "e = " + e.getMessage());
                        }
                    }
                });


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
