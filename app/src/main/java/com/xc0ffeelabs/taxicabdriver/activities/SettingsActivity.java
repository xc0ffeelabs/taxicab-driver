package com.xc0ffeelabs.taxicabdriver.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.xc0ffeelabs.taxicabdriver.R;
import com.xc0ffeelabs.taxicabdriver.fragments.SettingsFragment;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //add fragment
        getFragmentManager().beginTransaction()
                .replace(R.id.settings, new SettingsFragment()).commit();
    }
}
