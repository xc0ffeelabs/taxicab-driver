package com.xc0ffeelabs.taxicabdriver.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.xc0ffeelabs.taxicabdriver.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ControlsFragment extends Fragment {

    public interface ControlsInteraction {
        void onPrimaryButtonClicked();
    }
    private ControlsInteraction mListener;

    @Bind(R.id.primaryBtn) Button primaryBtn;
    @Bind(R.id.tvStatus) TextView tvStatus;

    public void setControlsInteraction(ControlsInteraction listener) {
        mListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_controls, container, false);

        ButterKnife.bind(this, view);

        primaryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onPrimaryButtonClicked();
                }
            }
        });

        setPrimaryButtonText(null);

        setStatusText(null);

        updateControlText();

        return view;
    }

    public void setPrimaryButtonText(String buttonText){
        if (buttonText == null) buttonText = "Go Active";
        primaryBtn.setText(buttonText);
    }

    public void setStatusText(String status){
        if (status == null) status = "Go Active to get rides...";
        tvStatus.setText(status);
    }
    public void updateControlText(){}

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
