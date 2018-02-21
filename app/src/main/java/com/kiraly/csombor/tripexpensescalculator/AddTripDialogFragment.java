package com.kiraly.csombor.tripexpensescalculator;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.kiraly.csombor.tripexpensescalculator.model.data.Person;
import com.kiraly.csombor.tripexpensescalculator.model.data.Trip;

/**
 * Created by Király Csombor on 2017. 11. 24..
 */

public class AddTripDialogFragment extends DialogFragment {

    private AlertDialog thisDialog;
    private AddTripDialogListener mListener;
    private EditText editText_name;

    Trip tripData;

    private Long tripId;
    private int listPosition;
    private boolean forUpdate;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (AddTripDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement NoticeDialogListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tripId = getArguments().getLong(getString(R.string.argument_key_id), -1);
        listPosition = getArguments().getInt(getString(R.string.argument_key_pos), 0);
        if(tripId == -1) forUpdate = false;
        else forUpdate = true;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_trip_dialog, null);

        editText_name = (EditText) view.findViewById(R.id.editText_addTripName);
        if(forUpdate){
            Trip t = Trip.findById(Trip.class, tripId);
            editText_name.append(t.name);
        }

        builder.setView(view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        tripData = new Trip(editText_name.getText().toString());
                                            //Long.parseLong(editText_date.getText().toString()));
                        mListener.onDialogPositiveClick(AddTripDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddTripDialogFragment.this.getDialog().cancel();
                    }
                });

        thisDialog = builder.create();

        editText_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    thisDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        return thisDialog;
    }

    public Trip getTrip(){
        return tripData;
    }

    public int getListPosition(){
        return listPosition;
    }

    public boolean isUpdate(){
        return forUpdate;
    }

    public interface AddTripDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

}
