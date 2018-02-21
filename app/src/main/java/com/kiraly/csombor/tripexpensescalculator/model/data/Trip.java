package com.kiraly.csombor.tripexpensescalculator.model.data;

import com.orm.SugarRecord;

import java.util.Date;

/**
 * Created by Király Csombor on 2017. 11. 24..
 */

public class Trip extends SugarRecord<Trip> {

    public long date;
    public String name;

    public Trip(){
        name = "Initial";
        date = new Date().getTime();
    }

    public Trip(String name, long date){
        this.date = date;
        this.name = name;
    }

    public Trip(String name){
        this.name = name;
        date = new Date().getTime();
    }

    public Trip(Trip t){
        name = t.name;
        date = t.date;
    }

    public void clone(Trip t){
        name = t.name;
        date = t.date;
    }
}
