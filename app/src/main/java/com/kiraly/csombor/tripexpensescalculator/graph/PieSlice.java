/*
 * 	   Created by Daniel Nadeau
 * 	   daniel.nadeau01@gmail.com
 * 	   danielnadeau.blogspot.com
 *
 * 	   Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
 */

package com.kiraly.csombor.tripexpensescalculator.graph;

public class PieSlice {
    private int mColor = 0xFF33B5E5;
    private float mValue;
    private float mOldValue;
    private float mGoalValue;
    private String mTitle;

    public long personId;

    public PieSlice(){}

    public PieSlice(float value){
        mValue = value;
        mGoalValue = value;
        personId = -1;
    }

    public PieSlice(float value, long id){
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

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        mColor = color;
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
