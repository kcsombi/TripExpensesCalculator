package com.kiraly.csombor.tripexpensescalculator.model.data;

import com.orm.SugarRecord;

/**
 * Created by Király Csombor on 2017. 08. 30..
 */

public class PerPersonPaymentDetail extends SugarRecord<PerPersonPaymentDetail> {

    public Long paymentId;
    public Long personId;
    public boolean isEnabled;
    public boolean isRelative;
    public int payDifference;
    public Person person;

    public PerPersonPaymentDetail() {}

    public PerPersonPaymentDetail(Long paymentId, Long personId, boolean isEnabled, int payDifference, boolean isRelative){
        this.paymentId = paymentId;
        this.personId = personId;
        this.isEnabled = isEnabled;
        this.payDifference = payDifference;
        this.isRelative = isRelative;
    }

    public PerPersonPaymentDetail(PerPersonPaymentDetail p){
        paymentId = p.paymentId;
        personId = p.personId;
        isEnabled = p.isEnabled;
        isRelative = p.isRelative;
        payDifference = p.payDifference;
        person = p.person;
    }



}
