package com.kiraly.csombor.tripexpensescalculator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kiraly.csombor.tripexpensescalculator.graph.Bar;
import com.kiraly.csombor.tripexpensescalculator.graph.BarGraph;
import com.kiraly.csombor.tripexpensescalculator.graph.PieGraph;
import com.kiraly.csombor.tripexpensescalculator.graph.PieSlice;
import com.kiraly.csombor.tripexpensescalculator.graph.ResultView;
import com.kiraly.csombor.tripexpensescalculator.graph.StatView;
import com.kiraly.csombor.tripexpensescalculator.model.data.Payment;
import com.kiraly.csombor.tripexpensescalculator.model.data.PerPersonPaymentDetail;
import com.kiraly.csombor.tripexpensescalculator.model.data.Person;
import com.kiraly.csombor.tripexpensescalculator.model.data.Trip;
import com.kiraly.csombor.tripexpensescalculator.model.solver.Debit;
import com.kiraly.csombor.tripexpensescalculator.model.solver.SimpleSolver;
import com.kiraly.csombor.tripexpensescalculator.model.solver.Statistics;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AddPaymentDialogFragment.AddPaymentDialogListener {
    MainActivity thisActivity;

    TextView textView_tripName;
    PieGraph pieGraph;
    BarGraph barGraph;
    ResultView resultView;
    StatView statView;
    LinearLayout layout;
    FloatingActionButton fab;

    Long tripId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        navView.getMenu().setGroupCheckable(0, false, false);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialog = new AddPaymentDialogFragment();
                Bundle args = new Bundle();
                args.putLong(getString(R.string.argument_key_id), -1);
                args.putLong(getString(R.string.argument_key_tripId), tripId);
                dialog.setArguments(args);
                dialog.show(getSupportFragmentManager(), getString(R.string.add_payment_dialog_fragment_tag));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        thisActivity = this;

        checkDB();

        layout = (LinearLayout) findViewById(R.id.main_linearLayout);
        textView_tripName = (TextView) findViewById(R.id.trip_name);
        pieGraph = (PieGraph) findViewById(R.id.pie_graph);
        barGraph = (BarGraph) findViewById(R.id.bar_graph);
        resultView = (ResultView) findViewById(R.id.result_view);
        statView = (StatView) findViewById(R.id.stat_view);

        pieGraph.setDuration(1000);
        pieGraph.setInterpolator(new AccelerateDecelerateInterpolator());

        barGraph.setDuration(1000);
        barGraph.setInterpolator(new AccelerateDecelerateInterpolator());
    }

    @Override
    protected void onResume(){
        super.onResume();

        SharedPreferences sharedP = getSharedPreferences(getString(R.string.shared_preferences_key), Context.MODE_PRIVATE);
        tripId = sharedP.getLong(getString(R.string.shared_preferences_active_trip), -1L);

        if(tripId == -1L) {
            textView_tripName.setText(R.string.create_new_trip);
            fab.setEnabled(false);
        } else {
            Trip t = Trip.findById(Trip.class, tripId);
            textView_tripName.setText(t.name);

            List<Person> persons = Person.find(Person.class, "trip_id = ?", tripId.toString());
            if(persons.size() > 0) fab.setEnabled(true);
            else                   fab.setEnabled(false);
        }

        updateGraphs();
    }

    void checkDB(){
        if(Trip.listAll(Trip.class).size() == 0){
            SharedPreferences sharedPref = getSharedPreferences(getString(R.string.shared_preferences_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putLong(getString(R.string.shared_preferences_active_trip), -1L);
            editor.apply();

            Person.deleteAll(Person.class);
            Payment.deleteAll(Payment.class);
            PerPersonPaymentDetail.deleteAll(PerPersonPaymentDetail.class);
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        AddPaymentDialogFragment fragment = (AddPaymentDialogFragment)dialog;
        Payment p = new Payment(fragment.person, fragment.amount, fragment.comment);

        p.setTripId(tripId);
        p.save();

        List<PerPersonPaymentDetail> details = fragment.paymentDetailsList;
        for(PerPersonPaymentDetail pppd : details){
            pppd.paymentId = p.getId();
            pppd.save();
        }

        updateGraphs();
    }

    void updateGraphs(){
        List<Person> personList;
        List<Debit> debitList;

        if(tripId == -1L){
            personList = new ArrayList<>();
            debitList = new ArrayList<>();
            statView.setData(0, 0, 0);
        } else {
            List<Person> persons = Person.find(Person.class, "trip_id = ?", tripId.toString());
            List<Payment> payments = Payment.find(Payment.class, "trip_id = ?", tripId.toString());
            List<PerPersonPaymentDetail> pppd = new ArrayList<>();
            for (Payment p : payments) {
                pppd.addAll(PerPersonPaymentDetail.find(PerPersonPaymentDetail.class, "payment_id = ?", p.getId().toString()));
            }

            Statistics stat = new Statistics(persons, payments, pppd);
            personList = stat.getPersons();
            debitList = new SimpleSolver().solve(personList);

            statView.setData(stat.getPaidAmountSum(),
                             stat.getPaidAmountAvgByPersons(),
                             stat.getPaidAmountAvgByPayment());
        }

        for(Person p : personList){
            //pie
            boolean found = false;
            for(PieSlice slice : pieGraph.getSlices())
                if(slice.personId == p.getId()){
                    found = true;
                    slice.setGoalValue(p.payed);
                }
            if(!found){
                PieSlice slice = new PieSlice(0, p.getId());
                slice.setGoalValue(p.payed);
                slice.setTitle(p.name);
                slice.setColor(Color.argb(255, (int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255)));
                pieGraph.addSlice(slice);
            }
            //bar
            found = false;
            for(Bar bar : barGraph.getBars())
                if(bar.personId == p.getId()){
                    found = true;
                    bar.setGoalValue(p.payed - p.needToPay);
                }
            if(!found){
                Bar bar = new Bar(0, p.getId());
                bar.setGoalValue(p.payed - p.needToPay);
                bar.setTitle(p.name);
                barGraph.addBar(bar);
            }
        }

        List<PieSlice> slices = pieGraph.getSlices();
        for(int i = 0; i < slices.size(); i++){
            boolean found = false;
            for(Person p : personList)
                if(p.getId() == slices.get(i).personId)
                    found = true;
            if(!found)
                slices.remove(i--);
        }

        List<Bar> bars = barGraph.getBars();
        for(int i = 0; i < bars.size(); i++){
            boolean found = false;
            for(Person p : personList)
                if(p.getId() == bars.get(i).personId)
                    found = true;
            if(!found)
                bars.remove(i--);
        }

        resultView.removeDebits();
        resultView.setDebits(debitList);

        resultView.calculateHeight();
        barGraph.calculateHeight();

        layout.requestLayout();
        layout.invalidate();

        pieGraph.animateToGoalValues();
        barGraph.animateToGoalValues();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_payments) {
            Intent intent = new Intent(thisActivity, PaymentsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_trips) {
            Intent intent = new Intent(thisActivity, TripsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_persons) {
            Intent intent = new Intent(thisActivity, PersonsActivity.class);
            startActivity(intent);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
