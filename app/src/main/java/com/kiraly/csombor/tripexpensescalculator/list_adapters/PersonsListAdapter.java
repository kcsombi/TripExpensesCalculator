package com.kiraly.csombor.tripexpensescalculator.list_adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.kiraly.csombor.tripexpensescalculator.R;
import com.kiraly.csombor.tripexpensescalculator.model.data.Person;

import java.util.List;

/**
 * Created by Király Csombor on 2017. 08. 22..
 */

public class PersonsListAdapter extends RecyclerView.Adapter<PersonsListAdapter.PersonsListViewHolder> {

    private List<Person> persons;
    public PersonsListAdapterListener onClickListener;

    public PersonsListAdapter(List<Person> list, PersonsListAdapterListener listener){
        super();

        persons = list;
        onClickListener = listener;
    }

    @Override
    public PersonsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View thisItemsView = LayoutInflater.from(parent.getContext()).inflate(R.layout.persons_list_element, parent, false);
        return new PersonsListViewHolder(thisItemsView);
    }

    @Override
    public void onBindViewHolder(PersonsListViewHolder holder, int position) {
        holder.name.setText(persons.get(position).name);
    }

    @Override
    public int getItemCount() {
        return persons.size();
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

    public interface PersonsListAdapterListener {
        void rowClicked(View v, int position);
        void buttonRemoveClicked(View v, int position);
    }

    public class PersonsListViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        Button remove;

        public PersonsListViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.persons_list_element_name);
            remove = (Button) itemView.findViewById(R.id.person_list_element_remove_button);

            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.buttonRemoveClicked(v, getAdapterPosition());
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