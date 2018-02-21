package com.kiraly.csombor.tripexpensescalculator.model.solver;

import com.kiraly.csombor.tripexpensescalculator.model.data.Person;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Király Csombor on 2017. 08. 17..
 */

public class Debit {

    public Person from;
    public Person to;
    public int amount;

    public Debit(Person f, Person t, int a) {
        from = f;
        to = t;
        amount = a;
    }
}

