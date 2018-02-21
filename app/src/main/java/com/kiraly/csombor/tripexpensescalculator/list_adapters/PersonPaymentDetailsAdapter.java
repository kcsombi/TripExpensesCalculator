package com.kiraly.csombor.tripexpensescalculator.list_adapters;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.kiraly.csombor.tripexpensescalculator.R;
import com.kiraly.csombor.tripexpensescalculator.model.data.Payment;
import com.kiraly.csombor.tripexpensescalculator.model.data.PerPersonPaymentDetail;
import com.kiraly.csombor.tripexpensescalculator.model.data.Person;

import java.util.List;

/**
 * Created by Király Csombor on 2017. 08. 30..
 */

public class PersonPaymentDetailsAdapter extends RecyclerView.Adapter<PersonPaymentDetailsAdapter.PersonPaymentDetailsViewHolder>  {

    private List<PerPersonPaymentDetail> paymentDetailsList;

    public PersonPaymentDetailsAdapterListener onClickListener;


    public PersonPaymentDetailsAdapter(List<PerPersonPaymentDetail> list, PersonPaymentDetailsAdapterListener listener){
        super();

        paymentDetailsList = list;
        onClickListener = listener;
    }

    @Override
    public PersonPaymentDetailsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View thisItemsView = LayoutInflater.from(parent.getContext()).inflate(R.layout.person_payment_detail_list_element, parent, false);
        return new PersonPaymentDetailsViewHolder(thisItemsView);
    }

    @Override
    public void onBindViewHolder(PersonPaymentDetailsViewHolder holder, int position) {
        PerPersonPaymentDetail pppd = paymentDetailsList.get(position);

        holder.name.setText(Person.findById(Person.class, pppd.personId).name);
        holder.checkBox.setChecked(pppd.isEnabled);

        if(!pppd.isRelative)
            holder.button.setText("");
        else {
            if(pppd.payDifference < 0)
                holder.button.setText("-");
            else
                holder.button.setText("+");
        }

        holder.difference.append(((Integer)Math.abs(pppd.payDifference)).toString());

        if(!pppd.isEnabled){
            holder.difference.setEnabled(false);
            holder.difference.setInputType(InputType.TYPE_NULL);
            holder.button.setEnabled(false);
        }
    }

    @Override
    public int getItemCount() {
        return paymentDetailsList.size();
    }

    public void updateChanges(){
        notifyDataSetChanged();
    }


    public interface PersonPaymentDetailsAdapterListener {
        void rowClicked(View v, int position);
        void CheckBoxClicked(View v, int position, boolean state);
        void EditTextUpdated(int position, String input, String buttonState);
    }

    public class PersonPaymentDetailsViewHolder extends RecyclerView.ViewHolder{

        CheckBox checkBox;
        TextView name;
        EditText difference;
        Button button;

        public PersonPaymentDetailsViewHolder(View itemView) {
            super(itemView);

            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox_addPayment_person_selected);
            name = (TextView) itemView.findViewById(R.id.textView_addPayment_person_name_detail);
            difference = (EditText) itemView.findViewById(R.id.editText_addPayment_difference);
            button = (Button) itemView.findViewById(R.id.button_isRelative);

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(checkBox.isChecked()){
                        onClickListener.CheckBoxClicked(v, getAdapterPosition(), true);
                        difference.setEnabled(true);
                        difference.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED);
                        button.setEnabled(true);
                    }else{
                        onClickListener.CheckBoxClicked(v, getAdapterPosition(), false);
                        difference.setEnabled(false);
                        difference.setInputType(InputType.TYPE_NULL);
                        button.setEnabled(false);
                    }
                }
            });

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String value = button.getText().toString();
                    if(value.equals("+")){
                        button.setText("-");
                    } else if(value.equals("-")) {
                        button.setText("");
                    } else {
                        button.setText("+");
                    }

                    onClickListener.EditTextUpdated(getAdapterPosition(), difference.getText().toString(), button.getText().toString());
                }
            });

            difference.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    onClickListener.EditTextUpdated(getAdapterPosition(), s.toString(), button.getText().toString());
                }
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
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
