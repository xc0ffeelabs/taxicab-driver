package com.xc0ffeelabs.taxicabdriver.fragments;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;
import com.xc0ffeelabs.taxicabdriver.R;
import com.xc0ffeelabs.taxicabdriver.activities.MapActivity;
import com.xc0ffeelabs.taxicabdriver.models.Trip;
import com.xc0ffeelabs.taxicabdriver.models.User;
import com.xc0ffeelabs.taxicabdriver.utils.CircleTransform;
import com.xc0ffeelabs.taxicabdriver.utils.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by skammila on 3/21/16.
 */
public class NotificationDialog extends DialogFragment{
    @Bind(R.id.notificationText)
    TextView notificationText;

    @Bind(R.id.profileImage)
    CircleImageView profileImage;

    @Bind(R.id.acceptBtn)Button acceptBtn;

    @Bind(R.id.denyBtn) Button denyButton;

    User tripUser;
    String tripId;

    private static String REMOVE_DIALOG_ACTION = "com.xc0ffeelabs.taxicabdriver.REMOVE_DIALOG_ACTION";

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
                ((MapActivity) getActivity()).onAccept(getTripId());
                getDialog().dismiss();
            }
        });

        denyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
                //perform deny
                try {
                    Log.d("NotificationDialog", "Deny TripId " + tripId);
                    ParseQuery tripQ = ParseQuery.getQuery("Trip");
                    tripQ.getInBackground(tripId, new GetCallback() {
                        @Override
                        public void done(ParseObject object, ParseException e) {
                            if (e == null) {
                                handleDone(object);
                            } else {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void done(Object o, Throwable throwable) {
                            if (throwable == null) {
                                handleDone(o);
                            } else {
                                throwable.printStackTrace();
                            }
                        }

                        private void handleDone(Object obj) {
                            Trip trip = (Trip) obj;
                            trip.put("status", "driver-denied");
                            trip.put("state", "driver-denied-trip-request");
                            trip.saveInBackground();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        setAutocancelAlarm(getContext());
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        createNotification();

    }

    private void createNotification() {
        String name = tripUser.getName() != null ? tripUser.getName() : "User";

        name = Utils.firstLetterUppercase(name);

        StyleSpan bold1 = new StyleSpan(Typeface.BOLD);
        StyleSpan bold2 = new StyleSpan(Typeface.BOLD);
        StyleSpan bold3 = new StyleSpan(Typeface.BOLD);
        StyleSpan bold4 = new StyleSpan(Typeface.BOLD);

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
        ssb.setSpan(bold4, ssb.length() - name.length(), ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        ssb.append("?");

        notificationText.setText(ssb);

        String profileImageUrl = tripUser.getProfileImage();
        if (profileImageUrl != null && profileImageUrl.length()>0) {
            Picasso.with(getContext()).load(profileImageUrl).transform(new CircleTransform()).into(profileImage);
        }
    }
//    private String firstLetterUppercase(String inp) {
//        StringBuilder rackingSystemSb = new StringBuilder(inp.toLowerCase());
//        rackingSystemSb.setCharAt(0, Character.toUpperCase(rackingSystemSb.charAt(0)));
//        return rackingSystemSb.toString();
//    }

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

    //Function to set alarm for auto cancel
    public static void setAutocancelAlarm(Context context) {
        AlarmManager alarmMgr =
                (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(REMOVE_DIALOG_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmMgr.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() +
                        30 * 1000,
                pendingIntent);
    }
}
