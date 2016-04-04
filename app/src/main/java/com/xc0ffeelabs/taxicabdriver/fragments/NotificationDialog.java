package com.xc0ffeelabs.taxicabdriver.fragments;

import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseException;
import com.squareup.picasso.Picasso;
import com.xc0ffeelabs.taxicabdriver.R;
import com.xc0ffeelabs.taxicabdriver.activities.MapActivity;
import com.xc0ffeelabs.taxicabdriver.models.User;
import com.xc0ffeelabs.taxicabdriver.utils.CircleTransform;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by skammila on 3/21/16.
 */
public class NotificationDialog extends DialogFragment{
    @Bind(R.id.notificationText)
    TextView notificationText;

    @Bind(R.id.profileImage)
    ImageView profileImage;

    @Bind(R.id.acceptBtn)Button acceptBtn;

    @Bind(R.id.denyBtn) Button denyButton;

    User tripUser;
    String tripId;

    public NotificationDialog() {

    }

    public static NotificationDialog newInstance(User tripuser, String tripId) {
        NotificationDialog frag = new NotificationDialog();
        frag.setTripUser(tripuser);
        frag.setTripId(tripId);
//        Bundle args = new Bundle();
//        args.putString("profileImage", profileImage);
//        args.putString("text", text);
//        frag.setArguments(args);
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_request_notif, container);
        ButterKnife.bind(this, view);

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MapActivity)getActivity()).onAccept(getTripId());
                getDialog().dismiss();
            }
        });

        denyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        createNotification();

    }

    private void createNotification() {
        String name = tripUser.getName() != null ? tripUser.getName() : "User";

        name = firstLetterUppercase(name);

        StyleSpan bold1 = new StyleSpan(Typeface.BOLD);
        StyleSpan bold2 = new StyleSpan(Typeface.BOLD);
        StyleSpan bold3 = new StyleSpan(Typeface.BOLD);
        StyleSpan bold4 = new StyleSpan(Typeface.BOLD);

//        ForegroundColorSpan redColorSpan1 = new ForegroundColorSpan(
//                getResources().getColor(R.color.colorAccent));

//        ForegroundColorSpan greenColorSpan1 = new ForegroundColorSpan(
//                getResources().getColor(R.color.greenDarkMaterial));
//
//        ForegroundColorSpan greenColorSpan2 = new ForegroundColorSpan(
//                getResources().getColor(R.color.greenDarkMaterial));


        SpannableStringBuilder ssb = new SpannableStringBuilder(name);

        ssb.setSpan(bold1, 0, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        ssb.append(" needs a ride.");
        StyleSpan italic1 = new StyleSpan(Typeface.ITALIC);
        StyleSpan italic2 = new StyleSpan(Typeface.ITALIC);

        try {
            if (tripUser.getPickupLocation() != null)
                tripUser.getPickupLocation().fetchIfNeeded();
            if (tripUser.getDestLocation() != null)
                tripUser.getDestLocation().fetchIfNeeded();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (tripUser.getPickupLocation() != null && tripUser.getPickupLocation().getText() != null && tripUser.getPickupLocation().getText().length() > 0) {
            String msg2 = "\nPICKUP: ";
            ssb.append(msg2);
            ssb.setSpan(bold2, ssb.length() - msg2.length(), ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.append(tripUser.getPickupLocation().getText());
            ssb.setSpan(italic1, ssb.length() - tripUser.getPickupLocation().getText().length(), ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if (tripUser.getDestLocation() != null && tripUser.getDestLocation().getText() != null && tripUser.getDestLocation().getText().length() > 0) {
            String msg3 = "\nDESTINATION: ";
            ssb.append(msg3);
            ssb.setSpan(bold3, ssb.length() - msg3.length(), ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.append(tripUser.getDestLocation().getText());
            ssb.setSpan(italic2, ssb.length() - tripUser.getDestLocation().getText().length(), ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        String msg4 = "\nCan you pick " + name;
        ssb.append(msg4);
//        ForegroundColorSpan redColorSpan2 = new ForegroundColorSpan(
//                getResources().getColor(R.color.colorAccent));
        ssb.setSpan(bold4, ssb.length() - name.length(), ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        ssb.append("?");

        notificationText.setText(ssb);

        String profileImageUrl = tripUser.getProfileImage();
        if (profileImageUrl != null && profileImageUrl.length()>0) {
            Picasso.with(getContext()).load(profileImageUrl).transform(new CircleTransform()).into(profileImage);
        }
    }
    private String firstLetterUppercase(String inp) {
        StringBuilder rackingSystemSb = new StringBuilder(inp.toLowerCase());
        rackingSystemSb.setCharAt(0, Character.toUpperCase(rackingSystemSb.charAt(0)));
        return rackingSystemSb.toString();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    public User getTripUser() {
        return tripUser;
    }

    public void setTripUser(User tripUser) {
        this.tripUser = tripUser;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }
}
