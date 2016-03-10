package com.xc0ffeelabs.taxicabdriver.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by skammila on 3/9/16.
 */
public class LocationBoradcastReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345;
    public static final String ACTION = "com.codepath.example.servicesdemo.alarm";

    // Triggered by the Alarm periodically (starts the service to run task)
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, LocationService.class);
        i.putExtra("foo", "bar");
        context.startService(i);
    }
}