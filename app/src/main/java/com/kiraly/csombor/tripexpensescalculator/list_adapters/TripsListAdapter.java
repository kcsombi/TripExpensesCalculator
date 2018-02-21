package com.kiraly.csombor.tripexpensescalculator.list_adapters;

import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.kiraly.csombor.tripexpensescalculator.R;
import com.kiraly.csombor.tripexpensescalculator.model.data.Trip;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Király Csombor on 2017. 11. 24..
 */

public class TripsListAdapter extends RecyclerView.Adapter<TripsListAdapter.TripsListViewHolder> {

    private List<Trip> trips;
    public Long activeTrip;

    public TripsListAdapterListener onClickListener;

    public TripsListAdapter(List<Trip> list, TripsListAdapterListener listener){
        super();

        trips = list;
        onClickListener = listener;
    }

    @Override
    public TripsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View thisItemsView = LayoutInflater.from(parent.getContext()).inflate(R.layout.trips_list_element, parent, false);
        return new TripsListViewHolder(thisItemsView);
    }

    @Override
    public void onBindViewHolder(TripsListViewHolder holder, int position) {
        holder.name.setText(trips.get(position).name);
        holder.date.setText(new SimpleDateFormat("yyyy.MM.dd").format(new Date(trips.get(position).date)));
        if(trips.get(position).getId().equals(activeTrip)) {
            holder.activate.setText(R.string.activate_button_active);
            //holder.activate.setBackgroundColor(Color.YELLOW);
            holder.activate.getBackground().setColorFilter(ContextCompat.getColor(holder.activate.getContext(), R.color.activeTrip), PorterDuff.Mode.MULTIPLY);
        } else {
            holder.activate.setText(R.string.activate_button_activate);
            holder.activate.getBackground().clearColorFilter();
        }
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    public void updateChanges(){
        notifyDataSetChanged();
    }

    public void itemRemoved(int pos){
        notifyItemRemoved(pos);
    }

    public void itemAdded(int pos){
        notifyItemInserted(pos);
    }

    public void itemUpdated(int pos){
        notifyItemChanged(pos);
    }

    public interface TripsListAdapterListener {

        void rowClicked(View v, int position);
        void buttonRemoveClicked(View v, int position);
        void buttonActivateClicked(View v, int position);
    }

    public class TripsListViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        TextView date;
        Button remove;
        Button activate;

        public TripsListViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.trips_list_element_name);
            date = (TextView) itemView.findViewById(R.id.trips_list_element_date);
            remove = (Button) itemView.findViewById(R.id.trips_list_element_remove_button);
            activate = (Button) itemView.findViewById(R.id.trips_list_element_activate_button);

            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.buttonRemoveClicked(v, getAdapterPosition());
                }
            });

            activate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.buttonActivateClicked(v, getAdapterPosition());
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.rowClicked(v, getAdapterPosition());
                }
            });
        }
    }
}
