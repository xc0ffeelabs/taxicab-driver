package com.xc0ffeelabs.taxicabdriver.services;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.xc0ffeelabs.taxicabdriver.activities.MapActivity;
import com.xc0ffeelabs.taxicabdriver.models.Driver;
import com.xc0ffeelabs.taxicabdriver.states.StateManager;

import java.util.List;

/**
 * Created by skammila on 3/14/16.
 */
public class RideRequestActionReceiver extends BroadcastReceiver {
    public static final String ACCEPT_ACTION = "com.xc0ffeelabs.taxicabdriver.ACCEPT_REQUEST";
    public static final String DENY_ACTION = "com.xc0ffeelabs.taxicabdriver.DENY_REQUEST";
    public static final String AUTO_REMOVE_NOTIFCATION_ACTION = "com.xc0ffeelabs.taxicabdriver.AUTO_REMOVE_REQUEST_NOTIFICATION";

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.d("AcceptRequestReceiver", "In Accept handler");
        String action = intent.getAction();
        Bundle bnd = intent.getExtras();
        final String tripId = bnd.getString("tripId");
        String driverId = bnd.getString("driverId");
        final String userId = bnd.getString("userId");


        if(ACCEPT_ACTION.equals(action)) {
            NotificationManager mNotificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(DriverNotificationReceiver.NOTIFICATION_ID);

            //accept request
            try {
                Log.d("AcceptRequestReceiver", "TripId " + tripId + "driver id = " + driverId);

                final ParseObject trip = ParseQuery.getQuery("Trip").get(tripId);
                trip.put("status", "confirmed");
                trip.put("state", TripStates.GOING_TO_PICKUP);
                trip.saveInBackground();

                ParseUser driver = ParseUser.getCurrentUser();
                driver.put(Driver.STATE, StateManager.States.EnrouteCustomer.toString());
                driver.put("driver_currentTripId", tripId);
                driver.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            startActivity(context, userId, tripId);
                        } else {
                            Log.d("AcceptRequestReceiver", "e = " + e.getMessage());
                        }
                    }
                });


            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (DENY_ACTION.equals(action)) {
            NotificationManager mNotificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(DriverNotificationReceiver.NOTIFICATION_ID);

            //perform deny
            try {
                Log.d("Debug", "Deny TripId "+ tripId);
                ParseQuery tripQ = ParseQuery.getQuery("Trip");
                ParseObject trip = tripQ.get(tripId);
                trip.put("status", "driver-denied");
                trip.put("state", "driver-denied-trip-request");
                trip.saveInBackground();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (AUTO_REMOVE_NOTIFCATION_ACTION.equals(action)) {
            //cancel ride request
            NotificationManager mNotificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(DriverNotificationReceiver.NOTIFICATION_ID);
        }
    }

    private void startActivity(Context context, String userId, String tripId) {

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(Integer.MAX_VALUE);
        for (ActivityManager.RunningTaskInfo info : taskInfo) {
            ComponentName componentInfo = info.topActivity;
            if (componentInfo.getPackageName().equalsIgnoreCase("com.xc0ffeelabs.taxicabdriver")) {
                sendBroadcast(context, tripId, userId);
                return;
            }
        }

        Intent intentMap = new Intent(context, MapActivity.class);
        intentMap.putExtra("tripId", tripId);
        intentMap.putExtra("tripUserId", userId);
        intentMap.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intentMap);
    }

    private void sendBroadcast(Context context, String tripId, String userId) {
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(context);
        Intent intent = new Intent(DriverNotificationReceiver.ACCEPT_REQUEST_LAUNCH_MAP);
        intent.putExtra("tripId", tripId);
        intent.putExtra("tripUserId", userId);
        broadcastManager.sendBroadcast(intent);
    }

    //Function to set alarm for auto cancel
    public static void setAutocancelAlarm(Context context) {
        AlarmManager alarmMgr =
                (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(AUTO_REMOVE_NOTIFCATION_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmMgr.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() +
                        30 * 1000,
                pendingIntent);
    }
}
