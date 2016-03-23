package com.xc0ffeelabs.taxicabdriver.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.xc0ffeelabs.taxicabdriver.R;
import com.xc0ffeelabs.taxicabdriver.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

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
                    JSONObject valJson = new JSONObject(value);
                    Log.d(TAG, "..." + key + " => " + value);
                    // Extract custom push data
                    if (key.equals("customdata")) {
                        // create a local notification
                        createNotification(context, valJson);
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
    private void createNotification(Context context, JSONObject datavalue) throws JSONException {


//        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.RunningTaskInfo> services = activityManager
//                .getRunningTasks(Integer.MAX_VALUE);
//
//        if (services.get(0).topActivity.getPackageName().toString()
//                .equalsIgnoreCase(context.getPackageName().toString())) {
//            //App is active. Launch alert message
//            LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(context);
//            Intent intent = new Intent(DriverNotificationReceiver.RIDE_REQUEST_LAUNCH_MAP);
//            intent.putExtra("tripId", datavalue.getString("tripId"));
//            intent.putExtra("tripUserId", datavalue.getString("userId"));
//            broadcastManager.sendBroadcast(intent);
//            return;
//        }


        //create a notification

        //deny intent
        Intent requestDenyInt = new Intent(RideRequestActionReceiver.DENY_ACTION);
        requestDenyInt.putExtra("tripId", datavalue.getString("tripId"));
        requestDenyInt.putExtra("driverId", datavalue.getString("driverId"));
        requestDenyInt.putExtra("userId", datavalue.getString("userId"));
        int rnd = (int)System.currentTimeMillis();
        PendingIntent pendingIntentDeny = PendingIntent.getBroadcast(context, rnd, requestDenyInt, Intent.FILL_IN_DATA);

        //accept intent
        Intent requestAcceptInt = new Intent(RideRequestActionReceiver.ACCEPT_ACTION);
        requestAcceptInt.putExtra("tripId", datavalue.getString("tripId"));
        requestAcceptInt.putExtra("driverId", datavalue.getString("driverId"));
        requestAcceptInt.putExtra("userId", datavalue.getString("userId"));
        PendingIntent pendingIntentAccept = PendingIntent.getBroadcast(context, rnd, requestAcceptInt, Intent.FILL_IN_DATA);

        Bitmap image = null;
        try {
            User user = (User)ParseUser.getQuery().get(datavalue.getString("userId"));
            if (user != null && user.getProfileImage() != null) {
                URL url = null;
                url = new URL(user.getProfileImage());
                image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            }
        } catch (ParseException p) {
            p.printStackTrace();
        } catch (IOException io) {
                io.printStackTrace();
        }


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_chariot_logo_9)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(datavalue.getString("text")))
                .setContentTitle("Request")
                .setContentText("Request for Pickup")
                .setPriority(Notification.PRIORITY_MAX)
                .addAction(R.drawable.thumbs_up, "Accept", pendingIntentAccept)
                .addAction(R.drawable.thumb_down_sm, "Deny", pendingIntentDeny);
        if (image != null) {
            mBuilder.setLargeIcon(image);
        }
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Notification note = mBuilder.build();
        note.flags |= Notification.FLAG_AUTO_CANCEL; // For Non cancellable notification
        mNotificationManager.notify(NOTIFICATION_ID, note);

        //set auto remove notification alarm
        RideRequestActionReceiver.setAutocancelAlarm(context);

//        }
    }


}
