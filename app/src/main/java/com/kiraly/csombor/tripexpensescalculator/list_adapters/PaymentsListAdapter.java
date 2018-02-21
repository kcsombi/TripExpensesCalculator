package com.kiraly.csombor.tripexpensescalculator.list_adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.kiraly.csombor.tripexpensescalculator.R;
import com.kiraly.csombor.tripexpensescalculator.model.data.Payment;

import java.util.List;

/**
 * Created by Király Csombor on 2017. 08. 25..
 */

public class PaymentsListAdapter extends RecyclerView.Adapter<PaymentsListAdapter.PaymentsListViewHolder> {

    private List<Payment> payments;
    public PaymentsListAdapterListener onClickListener;

    public PaymentsListAdapter(List<Payment> list, PaymentsListAdapterListener listener){
        super();

        payments = list;
        onClickListener = listener;
    }

    @Override
    public PaymentsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View thisItemsView = LayoutInflater.from(parent.getContext()).inflate(R.layout.payments_list_element, parent, false);
        return new PaymentsListViewHolder(thisItemsView);
    }

    @Override
    public void onBindViewHolder(PaymentsListViewHolder holder, int position) {
        holder.name.setText(payments.get(position).payer.name);
        holder.amount.setText(((Integer)payments.get(position).amount).toString());
        holder.comment.setText(payments.get(position).comment);
    }

    @Override
    public int getItemCount() {
        return payments.size();
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

    public interface PaymentsListAdapterListener {
        void rowClicked(View v, int position);
        void buttonRemoveClicked(View v, int position);
    }

    public class PaymentsListViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        TextView amount;
        TextView comment;

        public PaymentsListViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.payments_list_element_name);
            amount = (TextView) itemView.findViewById(R.id.payments_list_element_amount);
            comment = (TextView) itemView.findViewById(R.id.payments_list_element_comment);

            Button delete = (Button) itemView.findViewById(R.id.payments_list_element_button_delete);
            delete.setOnClickListener(new View.OnClickListener() {
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
