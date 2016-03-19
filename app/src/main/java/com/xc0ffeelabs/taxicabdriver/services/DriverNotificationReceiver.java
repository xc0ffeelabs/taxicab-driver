package com.xc0ffeelabs.taxicabdriver.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.xc0ffeelabs.taxicabdriver.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by skammila on 3/13/16.
 */
public class DriverNotificationReceiver  extends BroadcastReceiver {
    private static final String TAG = "NotificationReceiver";
    public static final String intentAction = "com.parse.push.intent.RECEIVE";

    public static final String ACCEPT_ACTION = "com.xc0ffeelabs.taxicabdriver.ACCEPT_REQUEST";
    public static final String DENY_ACTION = "com.xc0ffeelabs.taxicabdriver.DENY_REQUEST";
    public static final String ACCEPT_REQUEST_LAUNCH_MAP = "com.xc0ffeelabs.taxicabdriver.ACCEPT_REQUEST_LAUNCH_MAP";

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


//        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
//        ComponentName componentInfo = taskInfo.get(0).topActivity;
//        if(componentInfo.getPackageName().equalsIgnoreCase("com.xc0ffeelabs.taxicabdriver")){
//            //Activity Running
////            Send a broadcast with the intent-filter which you register in your activity
////            where you want to have the updates

//        Intent pupInt = new Intent(context, MapActivity.class);
//        pupInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        pupInt.putExtra("data", datavalue);
//        context.getApplicationContext().startActivity(pupInt);
//
//
//        }
//        else {


        //deny intent
        Intent requestDenyInt = new Intent(DENY_ACTION);
        requestDenyInt.putExtra("tripId", datavalue.getString("tripId"));
        requestDenyInt.putExtra("driverId", datavalue.getString("driverId"));
        requestDenyInt.putExtra("userId", datavalue.getString("userId"));
        int rnd = (int)System.currentTimeMillis();
        PendingIntent pendingIntentDeny = PendingIntent.getBroadcast(context, rnd, requestDenyInt, Intent.FILL_IN_DATA);

        //accept intent
        Intent requestAcceptInt = new Intent(ACCEPT_ACTION);
        requestAcceptInt.putExtra("tripId", datavalue.getString("tripId"));
        requestAcceptInt.putExtra("driverId", datavalue.getString("driverId"));
        requestAcceptInt.putExtra("userId", datavalue.getString("userId"));
        PendingIntent pendingIntentAccept = PendingIntent.getBroadcast(context, rnd, requestAcceptInt, Intent.FILL_IN_DATA);


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_light)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(datavalue.getString("text")))
                .setContentTitle("Request")
                .setContentText("Request for Pickup")
                .setPriority(Notification.PRIORITY_MAX)
                .addAction(R.drawable.thumbs_up, "Accept", pendingIntentAccept)
                .addAction(R.drawable.thumb_down_sm, "Deny", pendingIntentDeny);
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Notification note = mBuilder.build();
        note.flags |= Notification.FLAG_AUTO_CANCEL; // For Non cancellable notification
        mNotificationManager.notify(NOTIFICATION_ID, note);
//        }
    }


}
