package com.xc0ffeelabs.taxicabdriver.states;

import android.os.Bundle;

import com.xc0ffeelabs.taxicabdriver.activities.MapActivity;

public interface State {
    void enterState(MapActivity activity, Bundle data);
    void exitState();
}
