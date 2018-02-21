package com.kiraly.csombor.tripexpensescalculator;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.widget.EditText;
import android.widget.Toast;

import com.kiraly.csombor.tripexpensescalculator.model.data.Person;

/**
 * Created by Király Csombor on 2017. 08. 22..
 */

public class AddPersonDialogFragment extends DialogFragment {

    private AlertDialog thisDialog;

    private AddPersonDialogListener mListener;

    private EditText editText_name;
    private String name;

    private Long personId;
    private int listPosition;
    private boolean forUpdate;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (AddPersonDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement NoticeDialogListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        personId = getArguments().getLong(getString(R.string.argument_key_id), -1);
        listPosition = getArguments().getInt(getString(R.string.argument_key_pos), 0);
        if(personId == -1) forUpdate = false;
        else forUpdate = true;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_person_dialog, null);

        editText_name = (EditText) view.findViewById(R.id.editText_addPersonName);
        if(forUpdate){
            Person p = Person.findById(Person.class, personId);
            editText_name.append(p.name);
        }

        builder.setView(view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        name = editText_name.getText().toString();
                        mListener.onDialogPositiveClick(AddPersonDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddPersonDialogFragment.this.getDialog().cancel();
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

    public String getName(){
        return name;
    }

    public int getListPosition(){
        return listPosition;
    }

    public boolean isUpdate(){
        return forUpdate;
    }

    public interface AddPersonDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }
}