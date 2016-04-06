package com.xc0ffeelabs.taxicabdriver.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.xc0ffeelabs.taxicabdriver.R;
import com.xc0ffeelabs.taxicabdriver.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

/**
 * Created by skammila on 4/2/16.
 */
public class CreateNotification extends IntentService {
    private String text;
    private String userId;
    private String driverId;
    private String tripId;
    public CreateNotification() {
        super("CreateNotification");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        String dataValue = intent.getStringExtra("dataValue");
        JSONObject dataJson = null;
        try {
            dataJson = new JSONObject(dataValue);
            userId = dataJson.getString("userId");
            driverId = dataJson.getString("driverId");
            tripId = dataJson.getString("tripId");
        } catch (JSONException e) {
            e.printStackTrace();
//            stopSelf();
            return;
        }
        ParseUser.getQuery().include("pickUpLocation").include("destLocation").getInBackground(userId, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                User user = (User) object;
                String userProfileImage = null;
                if (user != null) {
                    try {
                        text = "";
                        userProfileImage = user.getProfileImage();
                        String name = user.getName();
                        text += name + " needs a ride. ";
                        if (user.getPickupLocation() != null) {
                            user.getPickupLocation().fetchIfNeeded();
                            String pickupLocation = user.getPickupLocation().getText();
                            if (pickupLocation != null && pickupLocation.length() > 0) {
                                text += "\nPICKUP: " + pickupLocation + " ";
                            }
                            Log.i("CreateNotification", name + "  pickup " + pickupLocation);
                        }

                        if (user.getDestLocation() != null) {
                            user.getDestLocation().fetchIfNeeded();
                            String destLocation = user.getDestLocation().getText();
                            if (destLocation != null && destLocation.length() > 0) {
                                text += "\nDESTINATION: " + destLocation + " ";
                            }

                            Log.i("CreateNotification", name + " dest " + destLocation);
                        }

                        text += "\nCan you pick " + name + "?";

                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    text = "User needs a ride. Can you pick the user?";
                }

                dowloadProfileImage(userProfileImage);
            }
        });

    }

    private void dowloadProfileImage(final String profileImage) {
        final Handler imageHandler = new Handler(){
            public void handleMessage(Message msg) {
                if(msg.obj!=null && msg.obj instanceof Bitmap){
                    showNotification(text, userId, driverId, tripId, (Bitmap)msg.obj);
                }

            };
        };

        new Thread(){
            public void run() {
                try {
                    Bitmap image;
                    if (profileImage != null && profileImage.length() > 0) {
                        URL url = new URL(profileImage);
                        image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    } else {
                        image = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                                R.drawable.profile_avatar);
                    }

//                    Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(profileImage);
                    Message msg = new Message();
                    msg.obj = image;
                    imageHandler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    protected void showNotification(String msg, String userId, String driverId, String tripId, Bitmap image) {
        //create a notification

        //deny intent
        Intent requestDenyInt = new Intent(RideRequestActionReceiver.DENY_ACTION);
        requestDenyInt.putExtra("tripId", tripId);
        requestDenyInt.putExtra("driverId", driverId);
        requestDenyInt.putExtra("userId", userId);
        int rnd = (int)System.currentTimeMillis();
        PendingIntent pendingIntentDeny = PendingIntent.getBroadcast(getApplicationContext(), rnd, requestDenyInt, Intent.FILL_IN_DATA);

        //accept intent
        Intent requestAcceptInt = new Intent(RideRequestActionReceiver.ACCEPT_ACTION);
        requestAcceptInt.putExtra("tripId", tripId);
        requestAcceptInt.putExtra("driverId", driverId);
        requestAcceptInt.putExtra("userId", userId);
        PendingIntent pendingIntentAccept = PendingIntent.getBroadcast(getApplicationContext(), rnd, requestAcceptInt, Intent.FILL_IN_DATA);


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_stat_chariot_logo_notification_icon)
                .setLargeIcon(image)
                .setColor(getResources().getColor(R.color.colorAccent))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setWhen(System.currentTimeMillis())
                .setContentTitle("Ride request")
                .setContentText("Request for ride")
                .setPriority(Notification.PRIORITY_MAX)
                .addAction(R.drawable.thumbs_up, "Accept", pendingIntentAccept)
                .addAction(R.drawable.thumb_down_sm, "Deny", pendingIntentDeny);

        NotificationManager mNotificationManager = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Notification note = mBuilder.build();
        note.flags |= Notification.FLAG_AUTO_CANCEL; // For Non cancellable notification
        mNotificationManager.notify(DriverNotificationReceiver.NOTIFICATION_ID, note);

        //set auto remove notification alarm
        RideRequestActionReceiver.setAutocancelAlarm(getApplicationContext());
        Log.i("Default Notification", "Default Notification");
        stopSelf();
    }

}