package com.xc0ffeelabs.taxicabdriver.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.xc0ffeelabs.taxicabdriver.R;
import com.xc0ffeelabs.taxicabdriver.adapter.TripHistoryAdapter;
import com.xc0ffeelabs.taxicabdriver.models.Trip;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HistoryActivity extends AppCompatActivity {

    @Bind(R.id.rvHistory)
    RecyclerView mHistoryView;
    @Bind(R.id.no_trips) View mNoTrips;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.progress_bar) View mPb;

    private TripHistoryAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAdapter = new TripHistoryAdapter(new ArrayList<Trip>());
        mHistoryView.setAdapter(mAdapter);
        mHistoryView.setLayoutManager(new LinearLayoutManager(this));

        new HistoryDowloadTask().execute();
        mPb.setVisibility(View.VISIBLE);
    }

    private class HistoryDowloadTask extends AsyncTask<String, Void, List<Trip>> {

        List<Trip> tripHistory;

        protected List<Trip> doInBackground(String... addresses) {
            ParseUser user = ParseUser.getCurrentUser();
            ParseQuery<Trip> trips = ParseQuery.getQuery("Trip");
            trips.include("driver");
            trips.include("user");
            trips.whereEqualTo("driver", user);
            trips.whereEqualTo("status", "done");
            trips.addDescendingOrder("createdAt");
            try {
                tripHistory =  trips.find();
                for (Trip trip : tripHistory) {
                    trip.fetchIfNeeded();
                    if(trip.getDriver() != null) trip.getDriver().fetchIfNeeded();
                    if (trip.getUser() != null) trip.getUser().fetchIfNeeded();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return tripHistory;
        }

        @Override
        protected void onPostExecute(List<Trip> history) {
            if (history.size() <= 0) {
                mPb.setVisibility(View.GONE);
                mNoTrips.setVisibility(View.VISIBLE);
            } else {
                mAdapter.addItems(history);
                mHistoryView.setVisibility(View.VISIBLE);
                mPb.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
