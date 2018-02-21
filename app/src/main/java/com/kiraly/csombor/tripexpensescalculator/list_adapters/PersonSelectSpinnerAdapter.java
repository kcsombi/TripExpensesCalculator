package com.kiraly.csombor.tripexpensescalculator.list_adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kiraly.csombor.tripexpensescalculator.R;
import com.kiraly.csombor.tripexpensescalculator.model.data.Person;

import java.util.List;

/**
 * Created by Király Csombor on 2017. 08. 26..
 */

public class PersonSelectSpinnerAdapter extends ArrayAdapter<Person> {

    LayoutInflater inflater;
    int resource;

    public PersonSelectSpinnerAdapter(@NonNull Context context, @LayoutRes int resource,
                        @IdRes int textViewResourceId, @NonNull List<Person> objects) {
        super(context, resource, textViewResourceId, objects);
        inflater = LayoutInflater.from(context);
        this.resource = resource;

    }

    @Override
    public @NonNull View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return myCreateViewFromResource(inflater, position, convertView, parent, resource);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return myCreateViewFromResource(inflater, position, convertView, parent, resource);
    }

    private @NonNull
    View myCreateViewFromResource(@NonNull LayoutInflater inflater, int position, @Nullable View convertView, @NonNull ViewGroup parent, int resource) {
        final View view;
        final TextView text;

        if (convertView == null) {
            view = inflater.inflate(resource, parent, false);
        } else {
            view = convertView;
        }

        text = (TextView) view.findViewById(R.id.spinner_name);

        text.setText(getItem(position).name);

        return view;
    }
}