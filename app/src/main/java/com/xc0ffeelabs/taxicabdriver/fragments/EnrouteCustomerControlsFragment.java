package com.xc0ffeelabs.taxicabdriver.fragments;

import com.xc0ffeelabs.taxicabdriver.activities.TaxiDriverApplication;
import com.xc0ffeelabs.taxicabdriver.states.StateManager;

public class EnrouteCustomerControlsFragment extends ControlsFragment {



    private static EnrouteCustomerControlsFragment mEnrouteCustomerControlsFragment = null;

    public static EnrouteCustomerControlsFragment newInstance() {
        if (mEnrouteCustomerControlsFragment == null) {
            mEnrouteCustomerControlsFragment = new EnrouteCustomerControlsFragment();
        }
        mEnrouteCustomerControlsFragment.setControlsInteraction(new ControlsInteraction() {
            @Override
            public void onPrimaryButtonClicked() {
                //Active button clicked.
                TaxiDriverApplication.getStateManager().startState(StateManager.States.EnrouteDestination, null);
            }
        });


        return mEnrouteCustomerControlsFragment;
    }

    @Override
    public void updateControlText() {

        //Action available for EnrouteCustomer is 'Reached Customer'
        mEnrouteCustomerControlsFragment.setPrimaryButtonText("Reached Customer?");
        //current status is active.
        mEnrouteCustomerControlsFragment.setStatusText("Go pickup customer...");
    }

}
