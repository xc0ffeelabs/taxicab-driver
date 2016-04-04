package com.xc0ffeelabs.taxicabdriver.states;

import android.os.Bundle;

import com.xc0ffeelabs.taxicabdriver.activities.MapActivity;

public class StateManager {

    public enum States {
        Active,
        Inactive,
        EnrouteCustomer,
        EnrouteDestination,
        ReachedDestination;

        public static States getEnum(String s){
            if(Active.name().equals(s)){
                return Active;
            }else if(Inactive.name().equals(s)){
                return Inactive;
            }else if(EnrouteCustomer.name().equals(s)){
                return EnrouteCustomer;
            }else if (EnrouteDestination.name().equals(s)){
                return EnrouteDestination;
            }else if (ReachedDestination.name().equals(s)){
                return ReachedDestination;
            }
            throw new IllegalArgumentException("No State specified for this string");
        }
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
    private MapActivity mAcitivity;

    public static StateManager getInstance() {
        if (ourInstance == null) {
            ourInstance = new StateManager();
        }
        return ourInstance;
    }

    private StateManager() {
    }

    public void setActivity(MapActivity activity) {
        mAcitivity = activity;
    }

    public void startState(States nextState, Bundle data) {
        States currentState = getCurrentStateEnum();
        if (nextState == currentState) {
            //next state is same as current state. DO nothing
            return;
        }
        if (mCurrentState != null) {
            mCurrentState.exitState();
        }

        switch (nextState) {
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

    public States getCurrentStateEnum() {
        if (mCurrentState != null) {
            if (mCurrentState instanceof ActiveState) {
                return States.Active;
            }

            else if (mCurrentState instanceof InactiveState) {
                return States.Inactive;
            }

            else if (mCurrentState instanceof EnrouteCustomerState) {
                return States.EnrouteCustomer;
            }

            else if (mCurrentState instanceof EnrouteDestinationState) {
                return States.EnrouteDestination;
            }

        }
        return null;
    }

    public void resetCurrentState() {
        mCurrentState = null;
    }

}
