package com.kiraly.csombor.tripexpensescalculator.model.solver;

import android.util.Log;

import com.kiraly.csombor.tripexpensescalculator.model.data.Person;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Király Csombor on 2017. 08. 17..
 */

public class SimpleSolver{

    public List<Debit> solve(List<Person> personList) {
        List<Person> persons = new ArrayList<>();
        for(Person p : personList)
            persons.add(new Person(p));

        List<Person> givers = findGivers(persons);
        List<Person> receivers = findReceivers(persons);

        sortGivers(givers);
        sortReceivers(receivers);

        List<Debit> transactions = new ArrayList<Debit>();

        while(!givers.isEmpty() && !receivers.isEmpty()){

            Person giver = givers.get(0);
            Person receiver = receivers.get(0);

            if(giver.AmountHasToPay() == receiver.AmountHasToBePaid()){
                transactions.add(new Debit(giver, receiver, giver.AmountHasToPay()));
                givers.remove(0);
                receivers.remove(0);
            } else if(giver.AmountHasToPay() > receiver.AmountHasToBePaid()){
                transactions.add(new Debit(giver, receiver, receiver.AmountHasToBePaid()));
                giver.give(receiver.AmountHasToBePaid());
                sortGivers(givers);
                receivers.remove(0);
            } else {  //(giver.AmountHasToPay() < receiver.AmountHasToBePaid())
                transactions.add(new Debit(giver, receiver, giver.AmountHasToPay()));
                receiver.receive(giver.AmountHasToPay());
                sortReceivers(receivers);
                givers.remove(0);
            }
        }
        return transactions;
    }

    private List<Person> findGivers(List<Person> persons){
        List<Person> givers = new ArrayList<Person>();

        for(Person p : persons){
            if(p.AmountHasToPay() > 0)
                givers.add(p);
        }

        return givers;
    }

    private List<Person> findReceivers(List<Person> persons){
        List<Person> receivers = new ArrayList<Person>();

        for(Person p : persons){
            if(p.AmountHasToBePaid() > 0)
                receivers.add(p);
        }

        return receivers;
    }

    private void sortGivers(List<Person> givers){
        Collections.sort(givers, new Comparator<Person>() {
            @Override
            public int compare(Person p1, Person p2) {
                return  (p1.AmountHasToPay() < p2.AmountHasToPay() ? 1 : (p1.AmountHasToPay() > p2.AmountHasToPay() ? -1 : 0));
            }
        });
    }

    private void sortReceivers(List<Person> receivers){
        Collections.sort(receivers, new Comparator<Person>() {
            @Override
            public int compare(Person p1, Person p2){
                return  (p1.AmountHasToPay() < p2.AmountHasToPay() ? -1 : (p1.AmountHasToPay() > p2.AmountHasToPay() ? 1 : 0));
            }
        });
    }
}