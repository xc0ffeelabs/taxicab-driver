package com.xc0ffeelabs.taxicabdriver.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.squareup.picasso.Picasso;
import com.xc0ffeelabs.taxicabdriver.activities.TaxiDriverApplication;
import com.xc0ffeelabs.taxicabdriver.models.User;
import com.xc0ffeelabs.taxicabdriver.states.StateManager;
import com.xc0ffeelabs.taxicabdriver.utils.Utils;

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
        showContactCard(user.getName(), user.getPickupLocation().getText(), user.getDestLocation().getText(), user.getProfileImage(), user.getPhone());
    }

    public void showContactCard(String name, String pickupLocation, String destLocation, String imageUrl, final String phoneNumber) {
        contactCard.setVisibility(View.VISIBLE);
        userName.setText(Utils.firstLetterUppercase(name));
        if (pickupLocation != null && pickupLocation.length() > 0) {
            pickupLoc.setText("PICKUP: " + pickupLocation);
        }
        if (destLocation != null && destLocation.length() > 0) {
            destLoc.setText("DEST: " + destLocation);
        }
        if (imageUrl != null) {
            Picasso.with(getContext()).load(imageUrl).into(userImage);
        }
        if (phoneNumber != null && phoneNumber.length() > 0) {
            phoneCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String uri = "tel:" + phoneNumber ;
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse(uri));
                    startActivity(intent);
                }
            });
        } else {
            phoneCall.setVisibility(View.INVISIBLE);
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
