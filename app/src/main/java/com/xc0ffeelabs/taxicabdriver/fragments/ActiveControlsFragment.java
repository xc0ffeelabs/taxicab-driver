package com.xc0ffeelabs.taxicabdriver.fragments;

import android.os.Bundle;
import android.util.Log;

import com.xc0ffeelabs.taxicabdriver.activities.TaxiDriverApplication;
import com.xc0ffeelabs.taxicabdriver.states.StateManager;

public class ActiveControlsFragment extends ControlsFragment {


    private static ActiveControlsFragment mActiveControlsFragment = null;

    public static ActiveControlsFragment newInstance() {
        if (mActiveControlsFragment == null) {
            mActiveControlsFragment = new ActiveControlsFragment();
        }
        mActiveControlsFragment.setControlsInteraction(new ControlsInteraction() {
            @Override
            public void onPrimaryButtonClicked() {
                //Active button clicked.
                TaxiDriverApplication.getStateManager().startState(StateManager.States.Inactive, null);
            }
        });

        return mActiveControlsFragment;
    }

    @Override
    public void updateControlText() {
        Log.d("NAYAN", "Update control text");
        //Action available for active state is Inactive
        mActiveControlsFragment.setPrimaryButtonText("Go Inactive");
        //current status is active.
        mActiveControlsFragment.setStatusText("Waiting for pickup request.");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }

}
