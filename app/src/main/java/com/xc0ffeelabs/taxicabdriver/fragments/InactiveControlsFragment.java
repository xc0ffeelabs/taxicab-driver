package com.xc0ffeelabs.taxicabdriver.fragments;

import com.xc0ffeelabs.taxicabdriver.activities.TaxiDriverApplication;
import com.xc0ffeelabs.taxicabdriver.states.StateManager;

public class InactiveControlsFragment extends ControlsFragment {



    private static InactiveControlsFragment mInactiveControlsFragment = null;

    public static InactiveControlsFragment newInstance() {
        if (mInactiveControlsFragment == null) {
            mInactiveControlsFragment = new InactiveControlsFragment();
        }
        mInactiveControlsFragment.setControlsInteraction(new ControlsInteraction() {
            @Override
            public void onPrimaryButtonClicked() {
                //Active button clicked.
                TaxiDriverApplication.getStateManager().startState(StateManager.States.Active, null);
            }
        });

        return mInactiveControlsFragment;
    }

    @Override
    public void updateControlText() {
        //Action available for inactive state is Active
        mInactiveControlsFragment.setPrimaryButtonText("Active");
        //current status is inactive
        mInactiveControlsFragment.setStatusText("Go Active to get rides...");
    }
}
