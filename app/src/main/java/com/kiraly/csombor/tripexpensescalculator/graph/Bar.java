package com.kiraly.csombor.tripexpensescalculator.graph;

/**
 * Created by Király Csombor on 2017. 11. 29..
 */

public class Bar {
    private float mValue;
    private float mOldValue;
    private float mGoalValue;
    private String mTitle;

    public long personId;

    public Bar(){}

    public Bar(float value){
        mValue = value;
        mGoalValue = value;
        personId = -1;
    }

    public Bar(float value, long id){
        mValue = value;
        mGoalValue = value;
        personId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public float getValue() {
        return mValue;
    }

    public void setValue(float value) {
        mValue = value;
    }

    public float getOldValue() {
        return mOldValue;
    }

    public void setOldValue(float oldValue) { mOldValue = oldValue; }

    public float getGoalValue() {
        return mGoalValue;
    }

    public void setGoalValue(float goalValue) { mGoalValue = goalValue; }

}
