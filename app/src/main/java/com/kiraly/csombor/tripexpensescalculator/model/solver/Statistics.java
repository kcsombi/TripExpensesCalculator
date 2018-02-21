package com.kiraly.csombor.tripexpensescalculator.model.solver;

import android.support.annotation.Nullable;
import android.util.Log;

import com.kiraly.csombor.tripexpensescalculator.model.data.Payment;
import com.kiraly.csombor.tripexpensescalculator.model.data.PerPersonPaymentDetail;
import com.kiraly.csombor.tripexpensescalculator.model.data.Person;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Király Csombor on 2017. 08. 26..
 */

public class Statistics {

    private List<Person> persons;
    private List<Payment> payments;
    private List<PerPersonPaymentDetail> paymentDetails;

    private int paidAmountSum;

    public Statistics(List<Person> Persons, List<Payment> Payments, List<PerPersonPaymentDetail> PaymentDetails) {
        persons = new ArrayList<>(Persons);
        payments = new ArrayList<>(Payments);
        paymentDetails = new ArrayList<>(PaymentDetails);

        //adding person reference to payments
        for(Payment p: payments){
            p.payer = findPersonById(persons, p.payerId);
        }
        //adding person reference to payment details
        for(PerPersonPaymentDetail pppd: paymentDetails){
            pppd.person = findPersonById(persons, pppd.personId);
        }

        //organizing payment details to payments
        Map<Long, List<PerPersonPaymentDetail>> paymentsMap = new HashMap<>();
        //creating payment detail lists for payments
        for(Payment p : payments) {
            paymentsMap.put(p.getId(), new ArrayList<PerPersonPaymentDetail>());
        }
        //adding payment details to lists, which are referenced by the corresponding payment's id
        for(PerPersonPaymentDetail pppd : paymentDetails){
            paymentsMap.get(pppd.paymentId).add(pppd);
        }

        //calculating the amount people need to pay
        paidAmountSum = 0;
        for(Payment payment : payments){
            paidAmountSum += payment.amount;

            //adding payment amount to the payer
            payment.payer.payed += payment.amount;

            //list of payment details for current payment
            List<PerPersonPaymentDetail> currentPaymentDetails = paymentsMap.get(payment.getId());

            int payedSum = payment.amount; //only the shared payed amount without
            int participantNum = currentPaymentDetails.size(); //number of people who takes part in payment

            for(PerPersonPaymentDetail paymentDetail : currentPaymentDetails){
                if(!paymentDetail.isEnabled){ //if person didn't participate, we don't care
                    participantNum--;
                } else {
                    if (!paymentDetail.isRelative) { //if person didn't buy shared items, we don't care
                        participantNum--;
                    }
                    //subtracting payed amount that doesn't concern others
                    payedSum -= paymentDetail.payDifference;
                    //adding payed amount that doesn't concern others
                    paymentDetail.person.needToPay += paymentDetail.payDifference;
                }
            }

            //amount people need to pay for the shared stuff
            int avgPaying = payedSum / participantNum;

            //adding the shared amount
            for(PerPersonPaymentDetail paymentDetail : currentPaymentDetails){
                if(paymentDetail.isEnabled && paymentDetail.isRelative)
                    paymentDetail.person.needToPay += avgPaying;
            }
        }
    }

    public List<Person> getPersons(){
        return persons;
    }

    public int getPaidAmountSum(){
        return paidAmountSum;
    }

    public int getPaidAmountAvgByPersons(){
        if(persons.size() == 0) return 0;
        return paidAmountSum / persons.size();
    }

    public int getPaidAmountAvgByPayment(){
        if(payments.size() == 0) return 0;
        return paidAmountSum / payments.size();
    }

    private Person findPersonById(List<Person> persons, long id){
        for(Person p : persons)
            if(p.getId() == id)
                return p;
        return null;
    }
}
