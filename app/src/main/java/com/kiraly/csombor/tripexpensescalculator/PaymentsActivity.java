package com.kiraly.csombor.tripexpensescalculator;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.kiraly.csombor.tripexpensescalculator.list_adapters.PaymentsListAdapter;
import com.kiraly.csombor.tripexpensescalculator.model.data.Payment;
import com.kiraly.csombor.tripexpensescalculator.model.data.PerPersonPaymentDetail;

import java.util.ArrayList;
import java.util.List;

public class PaymentsActivity extends AppCompatActivity implements PaymentsListAdapter.PaymentsListAdapterListener, AddPaymentDialogFragment.AddPaymentDialogListener{

    RecyclerView recyclerView_paymentsList;
    PaymentsListAdapter paymentsListAdapter;
    List<Payment> paymentsList = new ArrayList<Payment>();

    Long tripId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView_paymentsList = (RecyclerView) findViewById(R.id.recyclerView_paymentList);

        paymentsListAdapter = new PaymentsListAdapter(paymentsList, this);
        recyclerView_paymentsList.setLayoutManager(new LinearLayoutManager(this));
        recyclerView_paymentsList.setAdapter(paymentsListAdapter);
    }

    @Override
    protected void onResume(){
        super.onResume();

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.shared_preferences_key), Context.MODE_PRIVATE);
        tripId = sharedPref.getLong(getString(R.string.shared_preferences_active_trip), -1L);

        reloadPayments();
    }

    private void reloadPayments() {
        if(tripId == -1L) return;

        List<Payment> payments = Payment.find(Payment.class, "trip_id = ?", tripId.toString());
        paymentsList.clear();
        for(Payment p : payments){
            paymentsList.add(p);
        }
        paymentsListAdapter.updateChanges();
    }

    @Override
    public void buttonRemoveClicked(View v, int position) {
        Payment p = paymentsList.get(position);

        List<PerPersonPaymentDetail> details = PerPersonPaymentDetail.find(PerPersonPaymentDetail.class, "payment_id = ?", p.getId().toString());
        for(PerPersonPaymentDetail pppd: details)
            pppd.delete();

        p.delete();
        paymentsList.remove(position);

        paymentsListAdapter.itemRemoved(position);
    }

    @Override
    public void rowClicked(View v, int position) {
        DialogFragment dialog = new AddPaymentDialogFragment();
        Bundle args = new Bundle();
        args.putLong(getString(R.string.argument_key_id), paymentsList.get(position).getId());
        args.putLong(getString(R.string.argument_key_tripId), tripId);
        args.putInt(getString(R.string.argument_key_pos), position);
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), getString(R.string.add_payment_dialog_fragment_tag));
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        AddPaymentDialogFragment fragment = (AddPaymentDialogFragment)dialog;

        for(PerPersonPaymentDetail pppd : fragment.paymentDetailsList){
            pppd.save();
        }

        Payment p = paymentsList.get(fragment.getListPosition());
        p.payer = fragment.person;
        p.amount = fragment.amount;
        p.comment = fragment.comment;
        p.save();
        paymentsListAdapter.itemUpdated(fragment.getListPosition());
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {}
}
