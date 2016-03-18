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
