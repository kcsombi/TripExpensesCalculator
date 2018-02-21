package com.kiraly.csombor.tripexpensescalculator;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.kiraly.csombor.tripexpensescalculator.list_adapters.PersonsListAdapter;
import com.kiraly.csombor.tripexpensescalculator.model.data.Payment;
import com.kiraly.csombor.tripexpensescalculator.model.data.PerPersonPaymentDetail;
import com.kiraly.csombor.tripexpensescalculator.model.data.Person;

import java.util.ArrayList;
import java.util.List;

public class PersonsActivity extends AppCompatActivity implements AddPersonDialogFragment.AddPersonDialogListener, PersonsListAdapter.PersonsListAdapterListener{

    FloatingActionButton fab;
    RecyclerView recyclerView_personsList;
    PersonsListAdapter personsListAdapter;

    List<Person> personsList = new ArrayList<Person>();

    Long tripId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persons);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddPersonDialog();
            }
        });

        recyclerView_personsList = (RecyclerView) findViewById(R.id.recyclerView_personList);

        personsListAdapter = new PersonsListAdapter(personsList, this);
        recyclerView_personsList.setLayoutManager(new LinearLayoutManager(this));
        recyclerView_personsList.setAdapter(personsListAdapter);
    }

    @Override
    protected void onResume(){
        super.onResume();

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.shared_preferences_key), Context.MODE_PRIVATE);
        tripId = sharedPref.getLong(getString(R.string.shared_preferences_active_trip), -1L);

        reloadPersons();

        if(tripId == -1L) fab.setEnabled(false);
        else              fab.setEnabled(true);
    }

    public void showAddPersonDialog() {
        DialogFragment dialog = new AddPersonDialogFragment();
        Bundle args = new Bundle();
        args.putLong(getString(R.string.argument_key_id), -1);
        args.putInt(getString(R.string.argument_key_pos), 0);
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), getString(R.string.add_person_dialog_fragment_tag));
    }

    private void reloadPersons() {
        if(tripId == -1L) return;

        List<Person> persons = Person.find(Person.class, "trip_id = ?", tripId.toString());
        personsList.clear();
        for(Person p : persons){
            personsList.add(p);
        }
        personsListAdapter.updateChanges();
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        AddPersonDialogFragment fragment = (AddPersonDialogFragment)dialog;

        if(fragment.isUpdate()){
            Person p = personsList.get(fragment.getListPosition());
            p.name = fragment.getName();
            p.save();
            personsListAdapter.itemUpdated(fragment.getListPosition());

        } else {
            Person p = new Person(fragment.getName(), 0);
            p.setTripId(tripId);
            p.save();
            personsList.add(p);
            personsListAdapter.itemAdded(personsList.size() - 1);

            addPersonToPaymentDetails(p);
        }
    }

    void addPersonToPaymentDetails(Person p){
        List<Payment> payments = Payment.find(Payment.class, "trip_id = ?", tripId.toString());
        for(Payment payment : payments){
            PerPersonPaymentDetail pppd = new PerPersonPaymentDetail();
            pppd.paymentId = payment.getId();
            pppd.personId = p.getId();
            pppd.isEnabled = false;
            pppd.isRelative = true;
            pppd.payDifference = 0;
            pppd.save();
        }
    }

    void removePersonFromPaymentDetails(Person p){
        List<Payment> payments = Payment.find(Payment.class, "trip_id = ?", tripId.toString());
        for(Payment payment : payments){
            List <PerPersonPaymentDetail> detailsList = PerPersonPaymentDetail.find(PerPersonPaymentDetail.class, "payment_id = ?", payment.getId().toString());
            for(PerPersonPaymentDetail pppd : detailsList){
                if(pppd.personId == p.getId())
                    pppd.delete();
            }
        }
    }


    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    @Override
    public void rowClicked(View v, int position) {
        DialogFragment dialog = new AddPersonDialogFragment();
        Bundle args = new Bundle();
        args.putLong(getString(R.string.argument_key_id), personsList.get(position).getId());
        args.putInt(getString(R.string.argument_key_pos), position);
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), getString(R.string.add_person_dialog_fragment_tag));
    }

    @Override
    public void buttonRemoveClicked(View v, int position) {
        Person p = personsList.get(position);

        boolean hasPayment = false;

        List<Payment> payments = Payment.find(Payment.class, "trip_id = ?", tripId.toString());
        for(Payment payment : payments){
            List<PerPersonPaymentDetail> pppds = PerPersonPaymentDetail.find(PerPersonPaymentDetail.class, "payment_id = ?", payment.getId().toString());
            for(PerPersonPaymentDetail paymentDeatil : pppds){
                if(paymentDeatil.personId == p.getId().longValue() && paymentDeatil.isEnabled){
                    hasPayment = true;
                }
            }
        }

        if(hasPayment){
            Toast.makeText(this, R.string.toast_first_delete_his_payments, Toast.LENGTH_LONG).show();
            return;
        }

        removePersonFromPaymentDetails(p);

        p.delete();
        personsList.remove(position);
        personsListAdapter.itemRemoved(position);
    }
}
