package com.xc0ffeelabs.taxicabdriver.fragments;

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
        //Action available for active state is Inactive
        mActiveControlsFragment.setPrimaryButtonText("Go Inactive");
        //current status is active.
        mActiveControlsFragment.setStatusText("You are in queue...");
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
