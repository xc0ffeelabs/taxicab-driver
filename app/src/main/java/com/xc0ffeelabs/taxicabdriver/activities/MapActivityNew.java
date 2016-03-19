package com.xc0ffeelabs.taxicabdriver.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.xc0ffeelabs.taxicabdriver.R;
import com.xc0ffeelabs.taxicabdriver.fragments.MapsFragment;
import com.xc0ffeelabs.taxicabdriver.models.Driver;
import com.xc0ffeelabs.taxicabdriver.models.Trip;
import com.xc0ffeelabs.taxicabdriver.services.GPSTracker;
import com.xc0ffeelabs.taxicabdriver.states.StateManager;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by skammila on 3/17/16.
 */

public class MapActivityNew extends AppCompatActivity implements MapsFragment.MapReady{

    @Bind(R.id.nvView)
    NavigationView mNavView;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.dl_nav_drawer)
    DrawerLayout mDrawer;

    private ActionBarDrawerToggle mDrawerToggle;
    private GoogleMap mMap;
    private GoogleApiClient mApiClient;
    private GPSTracker gpstTacker;
    private Driver mDriver;
    private Trip mTrip;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mDriver = (Driver)ParseUser.getCurrentUser();
            ParseQuery tripQ = ParseQuery.getQuery("Trip");
            try {
                mDriver = (Driver)mDriver.fetch();
                mTrip = (Trip)tripQ.get(mDriver.getString("driver_currentTripId"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String tripState = mTrip != null? mTrip.getString(Driver.STATE) : null;

            StateManager.getInstance().startState(StateManager.States.getEnum(mDriver.getString(Driver.STATE)), null);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        setupNavDrawer();

        setupLocationTack();

        setupDriver();

//        setupInitialState();

        TaxiDriverApplication.getStateManager().setActivity(this);

        registerUserWithParseInstallation();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        MapsFragment mapsFragment = MapsFragment.newInstance();
        mapsFragment.setMapReadyListener(this);
        ft.replace(R.id.fmMap, mapsFragment);
        ft.commit();
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

    private void setupLocationTack() {
        gpstTacker = new GPSTracker(this, mDriver);
    }

    private void setupDriver() {
        if (mDriver == null) {
            mDriver = (Driver)ParseUser.getCurrentUser();
        }
        if (mDriver == null) {
            //Driver not found. So go back to SignIn screen
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

    @Override
    public void onMapReady(GoogleMap map, GoogleApiClient apiClient) {
        mMap = map;
        mApiClient = apiClient;
        String state = mDriver.getString(Driver.STATE);
        if (state==null || state.length()<0) state = StateManager.States.Inactive.toString();
        TaxiDriverApplication.getStateManager().startState(StateManager.States.getEnum(state), null);
    }


    public GoogleApiClient getApiClient() {
        return mApiClient;
    }

    public GoogleMap getMap() {
        return mMap;
    }

    public GPSTracker getGpstTacker() {
        return gpstTacker;
    }

    public Driver getmDriver() {
        return mDriver;
    }
}
