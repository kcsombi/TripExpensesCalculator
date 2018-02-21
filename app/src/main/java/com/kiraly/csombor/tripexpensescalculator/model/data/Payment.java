package com.kiraly.csombor.tripexpensescalculator.model.data;

import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Király Csombor on 2017. 08. 25..
 */

public class Payment extends SugarRecord<Payment> {

    public Long tripId;

    public Person payer;
    public Long payerId;
    public int amount;
    public String comment;

    public Payment(){}



    public Payment(Person p, int a, String c){
        payer = p;
        amount = a;
        comment = c;
    }

    public Payment(Payment p){
        tripId = p.tripId;
        payer = p.payer;
        payerId = p.payerId;
        amount = p.amount;
        comment = p.comment;
    }

    public void setTripId(long id){
        tripId = id;
    }

    @Override
    public void save() {
        payerId = payer.getId();
        super.save();
    }
}
