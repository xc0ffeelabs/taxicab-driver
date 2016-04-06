package com.xc0ffeelabs.taxicabdriver.fragments;

import com.xc0ffeelabs.taxicabdriver.activities.TaxiDriverApplication;
import com.xc0ffeelabs.taxicabdriver.states.StateManager;

public class EnrouteDestinationControlsFragment extends ControlsFragment {

    private static EnrouteDestinationControlsFragment mEnrouteDestinationControlsFragment = null;

    public static EnrouteDestinationControlsFragment newInstance() {
        if (mEnrouteDestinationControlsFragment == null) {
            mEnrouteDestinationControlsFragment = new EnrouteDestinationControlsFragment();
        }
        mEnrouteDestinationControlsFragment.setControlsInteraction(new ControlsInteraction() {
            @Override
            public void onPrimaryButtonClicked() {
                //Active button clicked.
                TaxiDriverApplication.getStateManager().startState(StateManager.States.ReachedDestination, null);
            }
        });


        return mEnrouteDestinationControlsFragment;
    }

    @Override
    public void updateControlText() {

        //Action available for EnrouteCustomer is 'Reached Customer'
        mEnrouteDestinationControlsFragment.setPrimaryButtonText("Reached Destination?");
        //current status is active.
        mEnrouteDestinationControlsFragment.setStatusText("Enroute destination");
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
