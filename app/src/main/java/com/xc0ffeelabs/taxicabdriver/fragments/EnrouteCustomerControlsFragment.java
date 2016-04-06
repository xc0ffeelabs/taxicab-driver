package com.xc0ffeelabs.taxicabdriver.fragments;

import android.os.Bundle;
import android.view.View;

import com.squareup.picasso.Picasso;
import com.xc0ffeelabs.taxicabdriver.activities.TaxiDriverApplication;
import com.xc0ffeelabs.taxicabdriver.models.User;
import com.xc0ffeelabs.taxicabdriver.states.StateManager;

public class EnrouteCustomerControlsFragment extends ControlsFragment {
    private User user;



    private static EnrouteCustomerControlsFragment mEnrouteCustomerControlsFragment = null;

    public static EnrouteCustomerControlsFragment newInstance(User user) {
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

        mEnrouteCustomerControlsFragment.user = user;


        return mEnrouteCustomerControlsFragment;
    }

    @Override
    public void updateControlText() {
        //Action available for EnrouteCustomer is 'Reached Customer'
        mEnrouteCustomerControlsFragment.setPrimaryButtonText("Reached Customer?");
        //current status is active.
        mEnrouteCustomerControlsFragment.setStatusText("Go pickup customer...");
        showContactCard(user.getName(), user.getDestLocation() == null ? null :
                user.getDestLocation().getText(), user.getProfileImage());
    }

    public void showContactCard(String name, String location, String imageUrl) {
        contactCard.setVisibility(View.VISIBLE);
        userName.setText(name);
        if (location != null) {
            destLoc.setText(location);
        }
        if (imageUrl != null) {
            Picasso.with(getContext()).load(imageUrl).into(userImage);
        }
    }

    public void hideContactCard(String name, String location) {
        contactCard.setVisibility(View.GONE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }
}
