package com.xc0ffeelabs.taxicabdriver.activities;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
    @Bind(R.id.moving_car) ImageView movingCar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        ButterKnife.bind(this);

        setSupportActionBar(mToolBar);

        mToolBar.setNavigationIcon(R.drawable.ic_chariot_logo_9);

        animateCarMoving();

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
                Intent mapIntent = new Intent(SignInActivity.this, MapActivity.class);
                startActivity(mapIntent);
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



    private void animateCarMoving() {

        BitmapDrawable bd=(BitmapDrawable) this.getResources().getDrawable(R.drawable.ic_sedan_car);
        int iconWidth=bd.getBitmap().getWidth();
        float deviceWidth = getDeviceWidth();

        Log.d("Debug", "Icon WIdth" + iconWidth);
        float animStopPixel = deviceWidth - iconWidth - 20;
        ObjectAnimator carMoving = ObjectAnimator.ofFloat(movingCar, "x", -2000f, animStopPixel);
        carMoving.setDuration(2500);
        carMoving.setInterpolator(new DecelerateInterpolator());
        carMoving.start();
    }

    private float getDeviceWidth() {
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        return  displayMetrics.widthPixels ;
    }

    private void handleLoginSuccessfull(String name, String passwd) {
    }
}
