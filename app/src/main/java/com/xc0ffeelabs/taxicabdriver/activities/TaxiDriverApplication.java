package com.xc0ffeelabs.taxicabdriver.activities;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.interceptors.ParseLogInterceptor;
import com.securepreferences.SecurePreferences;
import com.xc0ffeelabs.taxicabdriver.models.Driver;
import com.xc0ffeelabs.taxicabdriver.network.AccountManager;
import com.xc0ffeelabs.taxicabdriver.states.StateManager;

public class TaxiDriverApplication extends Application {

    private static final String APP_ID = "gotaxi";
    private static final String PARSE_URL = "https://gotaxi.herokuapp.com/parse/";

    private static TaxiDriverApplication mApp;

    private SecurePreferences mSecurePrefs;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        initializeParse();
    }

    public static TaxiDriverApplication get(){
        return mApp;
    }

    public static AccountManager getAccountManager() {
        return AccountManager.getInstance();
    }

    public static StateManager getStateManager() {
        return StateManager.getInstance();
    }

    private void initializeParse() {

        ParseObject.registerSubclass(Driver.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(APP_ID) // should correspond to APP_ID env variable
                .addNetworkInterceptor(new ParseLogInterceptor())
                .server(PARSE_URL).build());
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

    public SecurePreferences getSecureSharedPreferences() {
        if (mSecurePrefs == null){
            mSecurePrefs = new SecurePreferences(this, "", "my_prefs.xml");
            SecurePreferences.setLoggingEnabled(true);
        }
        return mSecurePrefs;
    }
}