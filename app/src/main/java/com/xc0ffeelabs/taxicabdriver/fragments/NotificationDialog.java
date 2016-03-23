package com.xc0ffeelabs.taxicabdriver.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.xc0ffeelabs.taxicabdriver.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by skammila on 3/21/16.
 */
public class NotificationDialog extends DialogFragment{
    @Bind(R.id.notificationText)
    TextView notificationText;

    @Bind(R.id.acceptBtn)Button acceptBtn;

    @Bind(R.id.denyBtn) Button denyButton;

    public NotificationDialog() {

    }

    public static NotificationDialog newInstance(String title) {
        NotificationDialog frag = new NotificationDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_request_notif, container);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
//        mEditText = (EditText) view.findViewById(R.id.txt_your_name);
        // Fetch arguments from bundle and set title
//        String title = getArguments().getString("title", "Enter Name");
//        getDialog().setTitle(title);
        // Show soft keyboard automatically and request focus to field
//        mEditText.requestFocus();
//        getDialog().getWindow().setSoftInputMode(
//                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        notificationText.setText("Ride request");

    }

}
