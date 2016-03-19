package com.xc0ffeelabs.taxicabdriver.stubs;

import com.google.android.gms.maps.model.LatLng;
import com.xc0ffeelabs.taxicabdriver.models.Driver;

public class NavigateDriverToUserStub {

    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/directions/json?";

    private LatLng mSrc;
    private LatLng mDest;
    private Driver mDriver;
    private DriverArrived mListener;

    public interface DriverArrived {
        void onDriverArrived();
    }

    public NavigateDriverToUserStub(LatLng src,
                                    LatLng to,
                                    Driver driver,
                                    DriverArrived listener) {
        mSrc = src;
        mDest = to;
        mDriver = driver;
        mListener = listener;
    }

    public void run() {
        getDirections();
    }

    private void getDirections() {
//        String url = directionsUrl(mSrc, mDest);
//        AsyncHttpClient client = new AsyncHttpClient();
//        client.get(url, new TextHttpResponseHandler() {
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                Log.e("NAYAN", "Couldn't get directions");
//            }
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                Gson gson = new GsonBuilder().create();
//                DirectionsResponse response = gson.fromJson(responseString, DirectionsResponse.class);
//                if (response != null && response.routes != null
//                        && response.routes.size() > 0
//                        && response.routes.get(0).legs != null
//                        && response.routes.get(0).legs.size() > 0
//                        && response.routes.get(0).legs.get(0).steps != null) {
//
//                    final List<Steps> steps = response.routes.get(0).legs.get(0).steps;
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (steps != null && steps.size() > 0) {
//                                for (Steps step : steps) {
//                                    ParseGeoPoint point = mDriver.getParseGeoPoint("currentLocation");
//                                    point.setLatitude(step.end_location.lat);
//                                    point.setLongitude(step.end_location.lng);
//                                    mDriver.put("currentLocation", point);
//                                    mDriver.saveInBackground();
//                                    try {
//                                        Thread.sleep(10000);
//                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//
//                                mListener.onDriverArrived();
//                            }
//                        }
//                    }).start();
//                }
//            }
//        });
    }

    private static String directionsUrl(LatLng src, LatLng dest) {
        return BASE_URL + origin(src) + "&" + dest(dest);
    }

    private static String origin(LatLng position) {
        return "origin=" + position.latitude + "," + position.longitude;
    }

    private static String dest(LatLng position) {
        return "destination=" + position.latitude + "," + position.longitude;
    }
}