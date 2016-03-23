package com.xc0ffeelabs.taxicabdriver.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;
import com.xc0ffeelabs.taxicabdriver.R;
import com.xc0ffeelabs.taxicabdriver.fragments.MapsFragment;
import com.xc0ffeelabs.taxicabdriver.fragments.NotificationDialog;
import com.xc0ffeelabs.taxicabdriver.models.Driver;
import com.xc0ffeelabs.taxicabdriver.models.Trip;
import com.xc0ffeelabs.taxicabdriver.models.User;
import com.xc0ffeelabs.taxicabdriver.services.DriverNotificationReceiver;
import com.xc0ffeelabs.taxicabdriver.states.StateManager;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MapActivity extends AppCompatActivity implements MapsFragment.MapReady{

    @Bind(R.id.nvView)
    NavigationView mNavView;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.dl_nav_drawer)
    DrawerLayout mDrawer;

    private ActionBarDrawerToggle mDrawerToggle;
    private GoogleMap mMap;
    private GoogleApiClient mApiClient;
    private Driver mDriver;
    private Trip mTrip;
    private User mTripUser;
    private boolean mIsMapReady = false;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent mapsIntent = new Intent(context, MapActivity.class);
            mapsIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(mapsIntent);
        }
    };

    private BroadcastReceiver rideRequstreceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showRequestNotificationFragment();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        setupNavDrawer();

        setupDriver();

        setupTripInfo(getIntent());

//        setupInitialState();

        TaxiDriverApplication.getStateManager().setActivity(this);

        registerUserWithParseInstallation();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        MapsFragment mapsFragment = MapsFragment.newInstance();
        mapsFragment.setMapReadyListener(this);
        ft.replace(R.id.fmMap, mapsFragment);
        ft.commit();

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
                new IntentFilter(DriverNotificationReceiver.ACCEPT_REQUEST_LAUNCH_MAP));

        LocalBroadcastManager.getInstance(this).registerReceiver(rideRequstreceiver,
                new IntentFilter(DriverNotificationReceiver.RIDE_REQUEST_LAUNCH_MAP));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("NAYAN", "onResume");
        if (mIsMapReady) {
            initiateDriverState();
        }
        setupTripInfo(getIntent());
    }

    private void registerUserWithParseInstallation() {
        Driver user = (Driver) ParseUser.getCurrentUser();
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("ownerId", user.getObjectId());
        installation.saveInBackground();
    }

    private void setupNavDrawer() {
        mNavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                selectDrawerItem(item);
                return true;
            }
        });
        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawer.addDrawerListener(mDrawerToggle);

    }

    private void selectDrawerItem(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nd_logout:
                logoutUserConf();
                break;
            default:
                throw new UnsupportedOperationException("Invalid menu item clicked");
        }
        mDrawer.closeDrawers();
    }

    private void logoutUserConf() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.logout_conf)
                .setMessage(R.string.logout_msg)
                .setPositiveButton(getString(R.string.logout), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logout();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create().show();
    }

    private void setupDriver() {
        if (mDriver == null) {
            mDriver = (Driver)ParseUser.getCurrentUser();
        } else {
            try {
                mDriver = (Driver)mDriver.fetch();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (mDriver == null) {
            //Driver not found. So go back to SignIn screen
            Log.e("MapActivity", "Driver not found");
            finish();
        } else {
            String imageUrl = mDriver.getProfileImage();
            ImageView iv = (ImageView)mNavView.getHeaderView(0).findViewById(R.id.profileImage);
            if (imageUrl!=null) {
                Picasso.with(this).load(imageUrl).into(iv);
            }
            TextView name = (TextView)mNavView.getHeaderView(0).findViewById(R.id.UserName);
            name.setText(mDriver.getName());

            TextView email = (TextView)mNavView.getHeaderView(0).findViewById(R.id.email);
            email.setText(mDriver.getEmail());
        }
    }

//    private void setupInitialState() {
//        String state = mDriver.getString(Driver.STATE);
//        if (state==null || state.length()<0) state= DriverStates.INACTIVE;
//        TaxiDriverApplication.getStateManager().startState(StateManager.States.getEnum(state), null);
//    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        TaxiDriverApplication.getAccountManager().logoutUser();
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    private void setupTripInfo(Intent intent) {

        String tripId = intent.getStringExtra("tripId");
        String userId = intent.getStringExtra("tripUserId");
        String driverTripId = mDriver.getString("driver_currentTripId");

        if (tripId != null) {
            try {
                mTrip = (Trip)ParseQuery.getQuery("Trip").include("_User").get(tripId);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        if (mTrip == null && driverTripId != null) {
            try {
                mTrip = (Trip)ParseQuery.getQuery("Trip").include("_User").get(driverTripId);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (userId != null) {
            try {
                mTripUser = (User)ParseUser.getQuery().get(userId);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (mTripUser == null && mTrip != null) {
            mTripUser = (User)mTrip.getParseUser("user");
        }
    }

    @Override
    public void onMapReady(GoogleMap map, GoogleApiClient apiClient) {
        mMap = map;
        mApiClient = apiClient;
        if (!mIsMapReady) {
            mIsMapReady = true;
            initiateDriverState();
        }
    }

    private void initiateDriverState() {
        if (mIsMapReady) {
            try {
                mDriver = (Driver)mDriver.fetch();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String state = mDriver.getString(Driver.STATE);
            Log.d("NAYAN", "State = " + state);
            if (state == null || state.length() < 0)
                state = StateManager.States.Inactive.toString();
            TaxiDriverApplication.getStateManager().startState(StateManager.States.getEnum(state), null);
        }
    }


    public GoogleApiClient getApiClient() {
        return mApiClient;
    }

    public GoogleMap getMap() {
        return mMap;
    }

    public Driver getmDriver() {
        return mDriver;
    }

    public Trip getmTrip() {
        return mTrip;
    }

    public User getmTripUser() {
        return mTripUser;
    }

    public void setmTripUser(User mTripUser) {
        this.mTripUser = mTripUser;
    }

    public void setmTrip(Trip mTrip) {
        this.mTrip = mTrip;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(rideRequstreceiver);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


    private void showRequestNotificationFragment() {
        FragmentManager fm = getSupportFragmentManager();
        NotificationDialog rideRequest = NotificationDialog.newInstance("Title");
        rideRequest.show(fm, "fragment_edit_name");

    }
}
