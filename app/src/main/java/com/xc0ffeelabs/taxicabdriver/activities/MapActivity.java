package com.xc0ffeelabs.taxicabdriver.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.xc0ffeelabs.taxicabdriver.R;
import com.xc0ffeelabs.taxicabdriver.fragments.MapsFragment;
import com.xc0ffeelabs.taxicabdriver.models.Driver;
import com.xc0ffeelabs.taxicabdriver.models.Trip;
import com.xc0ffeelabs.taxicabdriver.services.DriverNotificationReceiver;
import com.xc0ffeelabs.taxicabdriver.states.StateManager;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by skammila on 3/17/16.
 */

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
    private ParseObject mTrip;
    private ParseUser mTripUser;
    private boolean mIsMapReady = false;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("NAYAN", "onReceive of broacast");
            //setupTripInfo(intent);
            //initiateDriverState();
            Intent mapsIntent = new Intent(context, MapActivity.class);
            mapsIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(mapsIntent);
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("NAYAN", "onResume");
        if (mIsMapReady) {
            initiateDriverState();
        }
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
        finish();
    }

    private void setupTripInfo(Intent intent) {

        String tripId = intent.getStringExtra("tripId");
        String userId = intent.getStringExtra("tripUserId");
        String driverTripId = mDriver.getString("driver_currentTripId");

        if (tripId != null) {
            try {
                mTrip = ParseQuery.getQuery("Trip").include("_User").get(tripId);
            } catch (ParseException e) {
                e.printStackTrace();
            }


        }

        if (mTrip == null && driverTripId != null) {
            try {
                mTrip = ParseQuery.getQuery("Trip").include("_User").get(driverTripId);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (userId != null) {
            try {
                mTripUser = ParseUser.getQuery().get(userId);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (mTripUser == null && mTrip != null) {
            mTripUser = mTrip.getParseUser("user");
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

    public ParseObject getmTrip() {
        return mTrip;
    }

    public ParseUser getmTripUser() {
        return mTripUser;
    }

    public void setmTripUser(ParseUser mTripUser) {
        this.mTripUser = mTripUser;
    }

    public void setmTrip(Trip mTrip) {
        this.mTrip = mTrip;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }
}
