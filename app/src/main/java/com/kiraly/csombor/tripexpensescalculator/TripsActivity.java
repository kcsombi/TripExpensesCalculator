package com.kiraly.csombor.tripexpensescalculator;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.kiraly.csombor.tripexpensescalculator.list_adapters.PersonsListAdapter;
import com.kiraly.csombor.tripexpensescalculator.list_adapters.TripsListAdapter;
import com.kiraly.csombor.tripexpensescalculator.model.data.Payment;
import com.kiraly.csombor.tripexpensescalculator.model.data.Person;
import com.kiraly.csombor.tripexpensescalculator.model.data.Trip;

import java.util.ArrayList;
import java.util.List;

public class TripsActivity extends AppCompatActivity implements AddTripDialogFragment.AddTripDialogListener, TripsListAdapter.TripsListAdapterListener{

    RecyclerView recyclerView_tripsList;
    TripsListAdapter tripsListAdapter;
    List<Trip> tripsList = new ArrayList<Trip>();

    Long tripId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddTripDialog();
            }
        });

        recyclerView_tripsList = (RecyclerView) findViewById(R.id.recyclerView_tripList);

        tripsListAdapter = new TripsListAdapter(tripsList, this);
        recyclerView_tripsList.setLayoutManager(new LinearLayoutManager(this));
        recyclerView_tripsList.setAdapter(tripsListAdapter);
    }

    @Override
    protected void onResume(){
        super.onResume();

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.shared_preferences_key), Context.MODE_PRIVATE);
        tripId = sharedPref.getLong(getString(R.string.shared_preferences_active_trip), -1L);

        reloadTrips();
    }

    public void showAddTripDialog() {
        DialogFragment dialog = new AddTripDialogFragment();
        Bundle args = new Bundle();
        args.putLong(getString(R.string.argument_key_id), -1);
        args.putInt(getString(R.string.argument_key_pos), 0);
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), getString(R.string.add_trip_dialog_fragment_tag));
    }

    private void reloadTrips() {
        List<Trip> trips = Trip.listAll(Trip.class);
        tripsList.clear();
        for(Trip t : trips){
            tripsList.add(t);
        }
        tripsListAdapter.activeTrip = tripId;
        tripsListAdapter.updateChanges();
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        AddTripDialogFragment fragment = (AddTripDialogFragment)dialog;

        if(fragment.isUpdate()){
            Trip t = tripsList.get(fragment.getListPosition());
            t.clone(fragment.getTrip());
            t.save();
            tripsListAdapter.itemUpdated(fragment.getListPosition());

        } else {
            Trip t = new Trip(fragment.getTrip());
            t.save();

            SharedPreferences sharedPref = getSharedPreferences(getString(R.string.shared_preferences_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putLong(getString(R.string.shared_preferences_active_trip), t.getId());
            editor.apply();

            tripId = t.getId();

            tripsList.add(t);
            tripsListAdapter.activeTrip = tripId;
            tripsListAdapter.updateChanges();
            tripsListAdapter.itemAdded(tripsList.size() - 1);
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    @Override
    public void rowClicked(View v, int position) {
        DialogFragment dialog = new AddTripDialogFragment();
        Bundle args = new Bundle();
        args.putLong(getString(R.string.argument_key_id), tripsList.get(position).getId());
        args.putInt(getString(R.string.argument_key_pos), position);
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), getString(R.string.add_trip_dialog_fragment_tag));
    }

    @Override
    public void buttonRemoveClicked(View v, int position) {
        Trip t = tripsList.get(position);

        Person.deleteAll(Person.class, "trip_id = ?", t.getId().toString());
        Payment.deleteAll(Payment.class, "trip_id = ?", t.getId().toString());

        t.delete();
        tripsList.remove(position);
        tripsListAdapter.itemRemoved(position);


        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.shared_preferences_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        if(tripsList.isEmpty()){
            tripId = -1L;
        } else if(tripsList.size() == position) {
            tripId = tripsList.get(position - 1).getId();
        } else {
            tripId = tripsList.get(position).getId();
        }

        editor.putLong(getString(R.string.shared_preferences_active_trip), tripId);
        editor.apply();

        tripsListAdapter.activeTrip = tripId;
        tripsListAdapter.updateChanges();
    }

    @Override
    public void buttonActivateClicked(View v, int position){
        Trip t = tripsList.get(position);
        tripId = t.getId();

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.shared_preferences_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(getString(R.string.shared_preferences_active_trip), tripId);
        editor.apply();

        tripsListAdapter.activeTrip = tripId;
        tripsListAdapter.updateChanges();
    }

}
