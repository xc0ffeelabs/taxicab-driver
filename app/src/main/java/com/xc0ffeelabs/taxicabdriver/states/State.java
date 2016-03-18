package com.xc0ffeelabs.taxicabdriver.states;

import android.os.Bundle;

import com.xc0ffeelabs.taxicabdriver.activities.MapActivityNew;

public interface State {
    void enterState(MapActivityNew activity, Bundle data);
    void exitState();
}
