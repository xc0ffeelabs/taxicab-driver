package com.xc0ffeelabs.taxicabdriver.services;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

/**
 * Created by skammila on 3/13/16.
 */
public class DriverNotificationReceiver  extends BroadcastReceiver {
    private static final String TAG = "NotificationReceiver";
    public static final String intentAction = "com.parse.push.intent.RECEIVE";

//    public static final String ACCEPT_ACTION = "com.xc0ffeelabs.taxicabdriver.ACCEPT_REQUEST";
//    public static final String DENY_ACTION = "com.xc0ffeelabs.taxicabdriver.DENY_REQUEST";
    public static final String ACCEPT_REQUEST_LAUNCH_MAP = "com.xc0ffeelabs.taxicabdriver.ACCEPT_REQUEST_LAUNCH_MAP";
    public static final String RIDE_REQUEST_LAUNCH_MAP = "com.xc0ffeelabs.taxicabdriver.RIDE_REQUEST_LAUNCH_MAP";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            Log.d(TAG, "Receiver intent null");
        } else {
            // Parse push message and handle accordingly
            processPush(context, intent);
        }

    }

    private void processPush(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "got action " + action);
        if (action.equals(intentAction)) {
            String channel = intent.getExtras().getString("com.parse.Channel");
            try {
                JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
                Log.d(TAG, "got action " + action + " on channel " + channel + " with:");
                // Iterate the parse keys if needed
                Iterator<String> itr = json.keys();
                while (itr.hasNext()) {
                    String key = (String) itr.next();
                    String value = json.getString(key);

                    Log.d(TAG, "..." + key + " => " + value);
                    // Extract custom push data
                    if (key.equals("customdata")) {
                        // create a local notification
                        createNotification(context, value);
                    } else if (key.equals("launch")) {
                        // Handle push notification by invoking activity directly
//                        launchSomeActivity(context, value);
                    } else if (key.equals("broadcast")) {
                        // OR trigger a broadcast to activity
//                        triggerBroadcastToActivity(context, value);
                    }
                }
            } catch (JSONException ex) {
                Log.d(TAG, "JSON failed!");
            }
        }


    }

    public static final int NOTIFICATION_ID = 45;

    // Create a local dashboard notification to tell user about the event
    // See: http://guides.codepath.com/android/Notifications
    private void createNotification(Context context, String datavalue) throws JSONException {


        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> services = activityManager
                .getRunningTasks(Integer.MAX_VALUE);
        JSONObject valJson = new JSONObject(datavalue);

        if (services.get(0).topActivity.getPackageName().toString()
                .equalsIgnoreCase(context.getPackageName().toString())) {
            //App is active. Launch alert message
            LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(context);
            Intent intent = new Intent(DriverNotificationReceiver.RIDE_REQUEST_LAUNCH_MAP);
            intent.putExtra("tripId", valJson.getString("tripId"));
            intent.putExtra("tripUserId", valJson.getString("userId"));
            broadcastManager.sendBroadcast(intent);
            return;
        }

        //create notification
        Intent it = new Intent(context, CreateNotification.class);
        it.putExtra("dataValue", datavalue);
        context.startService(it);

    }


}
