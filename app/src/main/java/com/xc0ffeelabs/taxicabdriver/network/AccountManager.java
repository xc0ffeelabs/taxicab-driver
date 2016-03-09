package com.xc0ffeelabs.taxicabdriver.network;

import android.text.TextUtils;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.securepreferences.SecurePreferences;
import com.xc0ffeelabs.taxicabdriver.activities.TaxiDriverApplication;
import com.xc0ffeelabs.taxicabdriver.models.Driver;

public class AccountManager {

    public interface LoginStatusCallback {
        void onLoginSuccess();
        void onLoginFailed();
    }

    private static final String USERNAME = "user-name";
    private static final String PASSWORD = "passwd";

    private static AccountManager mManager;

    private AccountManager() {
    }

    public static AccountManager getInstance() {
        if (mManager == null) {
            mManager = new AccountManager();
        }
        return mManager;
    }

    public boolean isCredentialsStored() {
        SecurePreferences prefs = TaxiDriverApplication.get().getSecureSharedPreferences();
        String username = prefs.getString(USERNAME, "");
        String passwd = prefs.getString(PASSWORD, "");
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(passwd)) {
            return false;
        }
        return true;
    }

    public void storeCredentials(String username, String passwd) {
        SecurePreferences prefs = TaxiDriverApplication.get().getSecureSharedPreferences();
        SecurePreferences.Editor editor = prefs.edit();
        editor.putString(USERNAME, username);
        editor.putString(PASSWORD, passwd);
        editor.apply();
    }

    public void autoLoginUser(final LoginStatusCallback cb) {
        SecurePreferences prefs = TaxiDriverApplication.get().getSecureSharedPreferences();
        String username = prefs.getString(USERNAME, "");
        String passwd = prefs.getString(PASSWORD, "");
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(passwd)) {
            cb.onLoginFailed();
        } else {
            loginUser(username, passwd, cb);
        }
    }

    /* TODO: Do we need to pass Exception details to clients? */
    public void loginUser(final String userName, final String passWd, final LoginStatusCallback callback) {
        ParseUser.logInInBackground(userName, passWd, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    if (user.get(Driver.ROLE).equals("driver")) {
                        storeCredentials(userName, passWd);
                        callback.onLoginSuccess();
                    } else {
                        callback.onLoginFailed();
                    }
                } else {
                    callback.onLoginFailed();
                }
            }
        });
    }
}
