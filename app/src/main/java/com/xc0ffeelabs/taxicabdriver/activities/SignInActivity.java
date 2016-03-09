package com.xc0ffeelabs.taxicabdriver.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.xc0ffeelabs.taxicabdriver.R;
import com.xc0ffeelabs.taxicabdriver.network.AccountManager;
import com.xc0ffeelabs.taxicabdriver.utils.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = SignInActivity.class.getSimpleName();

    @Bind(R.id.user_email) EditText mUserEmail;
    @Bind(R.id.user_passwd) EditText mUserPasswd;
    @Bind(R.id.btn_signin) Button mBtnSignIn;
    @Bind(R.id.btn_signup) Button mBtnSignup;
    @Bind(R.id.pb_loading) View mPbLoading;
    @Bind(R.id.toolbar) Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        ButterKnife.bind(this);

        setSupportActionBar(mToolBar);

        mBtnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignInBtnClicked();
            }
        });

        mBtnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignUpBtnClicked();
            }
        });
    }

    private void onSignUpBtnClicked() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    private void onSignInBtnClicked() {
        final String email = mUserEmail.getText().toString();

        if (!Utils.isValidEmail(email)) {
            Toast.makeText(this, R.string.email_invalid, Toast.LENGTH_LONG).show();
            return;
        }

        final String password = mUserPasswd.getText().toString();
        login(email, password);
    }

    private void login(String name, String password) {
        setLoading(true);
        TaxiDriverApplication.getAccountManager().loginUser(name, password, new AccountManager.LoginStatusCallback() {
            @Override
            public void onLoginSuccess() {
                setLoading(false);
                Log.d(TAG, "Driver logged in");
            }

            @Override
            public void onLoginFailed() {
                setLoading(false);
                Log.d(TAG, "Failed to login user");
                Toast.makeText(SignInActivity.this, R.string.login_fail, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLoading(boolean loading) {
        if (loading) {
            mPbLoading.setVisibility(View.VISIBLE);
        } else {
            mPbLoading.setVisibility(View.GONE);
        }
    }

    private void handleLoginSuccessfull(String name, String passwd) {
    }
}
