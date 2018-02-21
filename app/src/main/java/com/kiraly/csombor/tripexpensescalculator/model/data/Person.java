package com.kiraly.csombor.tripexpensescalculator.model.data;

import com.orm.SugarRecord;

/**
 * Created by Király Csombor on 2017. 08. 17..
 */

public class Person extends SugarRecord<Person> {

    public Long tripId;

    public String name;
    public int payed;
    public int needToPay;

    public Person(){
        name = "";
        payed = 0;
        needToPay = 0;
    }

    public Person(String n){
        name = n;
        payed = 0;
        needToPay = 0;
    }

    public Person(String n, int p){
        name = n;
        payed = p;
        needToPay = 0;
    }

    public Person(Person p){
        name = p.name;
        payed = p.payed;
        needToPay = p.needToPay;
        setId(p.getId());
    }

    public void setTripId(long id){
        tripId = id;
    }

    public int AmountHasToPay(){
        return needToPay - payed;
    }

    public int AmountHasToBePaid(){
        return payed - needToPay;
    }

    public void give(int a){
        payed += a;
    }

    public void receive(int a){
        needToPay += a;
    }
}