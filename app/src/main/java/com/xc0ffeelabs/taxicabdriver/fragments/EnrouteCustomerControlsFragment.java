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

    //    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_controls, container, false);
//
//        ButterKnife.bind(this, view);
//
//        mPickupBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mListener != null) {
//                    mListener.onPickupButtonClicked();
//                }
//            }
//        });
//
//        setApprTime("--");
//        return view;
//    }

//    public void setApprTime(String time) {
//        if (!TextUtils.isEmpty(time)) {
//            String formattedStr = getContext().getString(R.string.appr_time, time);
//            mApprTimeText.setText(formattedStr);
//        }
//    }
}
