package com.kiraly.csombor.tripexpensescalculator;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.kiraly.csombor.tripexpensescalculator.list_adapters.PersonPaymentDetailsAdapter;
import com.kiraly.csombor.tripexpensescalculator.list_adapters.PersonSelectSpinnerAdapter;
import com.kiraly.csombor.tripexpensescalculator.model.data.Payment;
import com.kiraly.csombor.tripexpensescalculator.model.data.PerPersonPaymentDetail;
import com.kiraly.csombor.tripexpensescalculator.model.data.Person;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Király Csombor on 2017. 08. 25..
 */

public class AddPaymentDialogFragment extends DialogFragment implements AdapterView.OnItemSelectedListener,
                                                                        PersonPaymentDetailsAdapter.PersonPaymentDetailsAdapterListener {

    private AddPaymentDialogListener mListener;

    public Person person;
    public int amount;
    public String comment;

    private List<Person> personsList;
    private Payment payment;
    public List<PerPersonPaymentDetail> paymentDetailsList = new ArrayList<PerPersonPaymentDetail>();

    private Long paymentId;
    private int listPosition;
    private boolean forUpdate;

    Spinner spinner_buyer;
    PersonSelectSpinnerAdapter spinnerAdapter;
    EditText editText_amount;
    EditText editText_comment;
    RecyclerView recyclerView_details;
    PersonPaymentDetailsAdapter recyclerAdapter;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (AddPaymentDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement NoticeDialogListener");
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        paymentId = getArguments().getLong(getString(R.string.argument_key_id), -1);
        listPosition = getArguments().getInt(getString(R.string.argument_key_pos), 0);
        Long tripId = getArguments().getLong(getString(R.string.argument_key_tripId), -1);
        if(paymentId == -1) forUpdate = false;
        else                forUpdate = true;

        personsList = Person.find(Person.class, "trip_id = ?", tripId.toString());
        if(!forUpdate) {
            for (int i = 0; i < personsList.size(); i++) {
                PerPersonPaymentDetail pppd = new PerPersonPaymentDetail(0L, personsList.get(i).getId(), true, 0, true);
                paymentDetailsList.add(pppd);
            }
        }

        if(forUpdate) {
            payment = Payment.findById(Payment.class, paymentId);
            payment.payer = Person.findById(Person.class, payment.payerId);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.add_payment_dialog, null);

        editText_amount = (EditText) view.findViewById(R.id.editText_addPayment_amount);
        editText_comment = (EditText) view.findViewById(R.id.editText_add_comment);
        spinner_buyer = (Spinner)view.findViewById(R.id.spinner_name_select);
        recyclerView_details = (RecyclerView) view.findViewById(R.id.recyclerView_addPayment_person_list);

        spinner_buyer.setOnItemSelectedListener(this);
        spinnerAdapter = new PersonSelectSpinnerAdapter(getContext(), R.layout.person_select_spinner_element, R.id.spinner_name, personsList);
        spinner_buyer.setAdapter(spinnerAdapter);

        recyclerAdapter = new PersonPaymentDetailsAdapter(paymentDetailsList, this);
        recyclerView_details.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView_details.setAdapter(recyclerAdapter);

        if(forUpdate) {
            fillDialogFromPayment();
        }

        builder.setView(view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            amount = Integer.parseInt(editText_amount.getText().toString());
                        } catch (NumberFormatException e) {
                            Toast.makeText(getContext(), R.string.toast_invalid_number, Toast.LENGTH_LONG).show();
                            return;
                        }
                        comment = editText_comment.getText().toString();

                        boolean relativePaymentDetailExists = false;
                        for(PerPersonPaymentDetail pppd : paymentDetailsList)
                            if(pppd.isRelative)
                                relativePaymentDetailExists = true;

                        if(relativePaymentDetailExists)
                            mListener.onDialogPositiveClick(AddPaymentDialogFragment.this);
                        else
                            Toast.makeText(getContext(), R.string.toast_inconsistent_payment, Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddPaymentDialogFragment.this.getDialog().cancel();
                    }
                });

        Dialog dialog = builder.create();
        return dialog;
    }

    private void fillDialogFromPayment(){
        int index = 0;
        for(int i = 0; i < personsList.size(); i++){
            if(personsList.get(i).getId() == payment.payerId)
                index = i;
        }
        spinner_buyer.setSelection(index);

        person = payment.payer;
        amount = payment.amount;
        comment = payment.comment;

        editText_amount.append(((Integer)amount).toString());
        editText_comment.append(comment);

        List<PerPersonPaymentDetail> details = PerPersonPaymentDetail.find(PerPersonPaymentDetail.class, "payment_id = ?", paymentId.toString());
        paymentDetailsList.addAll(details);
        recyclerAdapter.updateChanges();
    }

    public int getListPosition(){
        return listPosition;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        person = (Person)parent.getItemAtPosition(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void rowClicked(View v, int position) {}

    @Override
    public void CheckBoxClicked(View v, int position, boolean state) {
        paymentDetailsList.get(position).isEnabled = state;
    }

    @Override
    public void EditTextUpdated(int position, String input, String type) {
        parseDifference(input, paymentDetailsList.get(position), type);
    }

    private void parseDifference(String input, PerPersonPaymentDetail pppd, String type){
        try{
            pppd.payDifference = Integer.parseInt(input);
        } catch (NumberFormatException e){
            pppd.payDifference = 0;
        }

        if(type.equals("+")){
            pppd.isRelative = true;
        } else if(type.equals("-")){
            pppd.isRelative = true;
            pppd.payDifference *= -1;
        } else {
            pppd.isRelative = false;
        }
    }


    public interface AddPaymentDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }
}
