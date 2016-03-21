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

import com.parse.ParseException;
import com.parse.SignUpCallback;
import com.xc0ffeelabs.taxicabdriver.R;
import com.xc0ffeelabs.taxicabdriver.models.Driver;
import com.xc0ffeelabs.taxicabdriver.utils.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = SignUpActivity.class.getSimpleName();

    @Bind(R.id.toolbar) Toolbar mToolBar;
    @Bind(R.id.user_name) EditText mUsername;
    @Bind(R.id.user_phone) EditText mPhoneNumber;
    @Bind(R.id.user_email) EditText mEmailAdd;
    @Bind(R.id.user_passwd) EditText mPasswd;
    @Bind(R.id.user_conf_password) EditText mConfPasswd;
    @Bind(R.id.btn_signup) Button mBtnSignUp;
    @Bind(R.id.pb_loading) View mPbLoading;
    @Bind(R.id.et_license) EditText mLicense;
    @Bind(R.id.et_carmodel) EditText mCarModel;
    @Bind(R.id.et_carnumber) EditText mCarNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ButterKnife.bind(this);

        setSupportActionBar(mToolBar);

        mBtnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignUpClicked();
            }
        });
    }

    private void onSignUpClicked() {
        final String email = mEmailAdd.getText().toString();
        final String password = mPasswd.getText().toString();
        String confirmPassword = mConfPasswd.getText().toString();
        String name = mUsername.getText().toString();
        String phNumber = mPhoneNumber.getText().toString();
        String licenseNo = mLicense.getText().toString();
        String carModel = mCarModel.getText().toString();
        String carNo = mCarNumber.getText().toString();

        if (!Utils.isValidEmail(email)) {
            Toast.makeText(this, R.string.email_invalid, Toast.LENGTH_LONG).show();
            return;
        }

        if (email.isEmpty()
                || password.isEmpty()
                || confirmPassword.isEmpty()
                || name.isEmpty()
                || phNumber.isEmpty()
                || licenseNo.isEmpty()
                || carModel.isEmpty()
                || carNo.isEmpty()) {
            Toast.makeText(this, R.string.required_fields, Toast.LENGTH_LONG).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, R.string.passwd_mismatch, Toast.LENGTH_LONG).show();
            return;
        }

        String phoneNumber = mPhoneNumber.getText().toString();
        if (!phoneNumber.startsWith("+1") && !phoneNumber.startsWith("1"))
            phoneNumber = "+1" + phoneNumber;
        else if (phoneNumber.startsWith("1"))
            phoneNumber = "+" + phoneNumber;

        // Store in Parse
        Driver driver = new Driver();
        driver.setUsername(email);
        driver.setPassword(password);
        driver.setName(name);
        driver.setRole();
        driver.setPhone(phoneNumber);
        driver.setLicense(licenseNo);
        driver.setCarModel(carModel);
        driver.setCarNumber(carNo);
        setLoading(true);
        driver.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                setLoading(false);
                if (e == null) {
                    TaxiDriverApplication.getAccountManager().storeCredentials(email, password);
                    signUpSuccess();
                } else {
                    Log.d(TAG, "Failed to sign up driver");
                    Toast.makeText(SignUpActivity.this, "Unable to sign up driver", Toast.LENGTH_SHORT).show();
                    signUpFailed();
                }
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

    private void signUpSuccess() {
        Log.d(TAG, "Driver signed up!");
        Intent mapIntent = new Intent(this, MapActivity.class);
        startActivity(mapIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    private void signUpFailed() {
        Log.e(TAG, "Driver signup failed");
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
