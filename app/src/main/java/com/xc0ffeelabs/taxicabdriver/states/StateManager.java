package com.xc0ffeelabs.taxicabdriver.states;

import android.os.Bundle;

import com.xc0ffeelabs.taxicabdriver.activities.MapActivityNew;

public class StateManager {

    public enum States {
        Active,
        Inactive,
        EnrouteCustomer,
        EnrouteDestination,
        ReachedDestination
//        ,
//        ListDriver,
//        TripRequested,
//        TaxiEnroute,
//        TaxiArrived,
//        DestEnroute,
//        DestArrived,
//        TripEnded
    }

    private static StateManager ourInstance;

    private State mCurrentState = null;
    private MapActivityNew mAcitivity;

    public static StateManager getInstance() {
        if (ourInstance == null) {
            ourInstance = new StateManager();
        }
        return ourInstance;
    }

    private StateManager() {
    }

    public void setActivity(MapActivityNew activity) {
        mAcitivity = activity;
    }

    public void startState(States state, Bundle data) {
        if (mCurrentState != null) {
            mCurrentState.exitState();
        }

        switch (state) {
            case Active:
                mCurrentState = ActiveState.getInstance();
                break;
            case Inactive:
                mCurrentState = InactiveState.getInstance();
                break;
            case EnrouteCustomer:
                mCurrentState = EnrouteCustomerState.getInstance();
                break;
            case EnrouteDestination:
                mCurrentState = EnrouteDestinationState.getInstance();
                break;
            case ReachedDestination:
                mCurrentState = ActiveState.getInstance();
                break;
            default:
                throw new UnsupportedOperationException("No such state");
        }
        mCurrentState.enterState(mAcitivity, data);
    }
}
