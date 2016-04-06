package com.xc0ffeelabs.taxicabdriver.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xc0ffeelabs.taxicabdriver.R;
import com.xc0ffeelabs.taxicabdriver.models.Trip;
import com.xc0ffeelabs.taxicabdriver.utils.CircleTransform;
import com.xc0ffeelabs.taxicabdriver.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;

public class TripHistoryAdapter extends RecyclerView.Adapter<TripHistoryAdapter.ViewHolder> {

    private List<Trip> mTrips;
    public TripHistoryAdapter(List<Trip> trips) {
        mTrips = trips;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public ImageView userImage;
        public TextView userName;
        public TextView tripDate;
        public TextView tripAmount;
        public TextView startLocation;
        public TextView destLocation;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            userImage = (ImageView) itemView.findViewById(R.id.userImage);
            userName = (TextView) itemView.findViewById(R.id.userName);
            tripDate = (TextView) itemView.findViewById(R.id.tripDate);
            tripAmount = (TextView) itemView.findViewById(R.id.tripAmount);
            startLocation = (TextView) itemView.findViewById(R.id.startPoint);
            destLocation = (TextView) itemView.findViewById(R.id.endPoint);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.history_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get the data model based on position
        Trip trip = mTrips.get(position);

        try {

            if (trip.getUser() == null) {
                return;
            } else {
                trip.getUser().fetchIfNeeded();
            }

            // Set item views based on the data model
            String name = trip.getUser() != null && trip.getUser().getString("name") !=null ? trip.getUser().getString("name"):"";
            name = Utils.firstLetterUppercase(name);
            holder.userName.setText(name);


            holder.tripDate.setText(new SimpleDateFormat("EEE MMM d, h:mm a").format(trip.getCreatedAt()));

            holder.tripAmount.setText("$" + generateRandom());

            String picUrl = trip.getUser().getString("profileImage");

            if (!TextUtils.isEmpty(picUrl)) {
                ImageView userImage = holder.userImage;
                Picasso.with(userImage.getContext()).load(picUrl).transform(new CircleTransform()).into(userImage);
            }

            String source = trip.getString("pickUpLocationString") != null ? trip.getString("pickUpLocationString") : "1 Facebook Way, Menlo Park, CA";
            String dest = trip.getString("destLocationString") != null ? trip.getString("destLocationString") : "Microsoft SVC Building 1, Mountain View, CA";
            holder.startLocation.setText(Html.fromHtml("Start: <i>"+ source + "</i>"));

            holder.destLocation.setText(Html.fromHtml("Dest: <i>"+ dest + "</i>"));

        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return mTrips.size();
    }

    public void addItems(List<Trip> items) {
        mTrips.clear();
        mTrips.addAll(items);
        notifyDataSetChanged();
    }

    private int generateRandom() {
        Random r = new Random();
        int Low = 10;
        int High = 50;
        return r.nextInt(High-Low) + Low;
    }
}