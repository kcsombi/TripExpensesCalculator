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

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class PieGraph extends View implements  HoloGraphAnimate {

    private ArrayList<PieSlice> mSlices = new ArrayList<PieSlice>();
    private Paint mPaint = new Paint();
    private RectF mRectF = new RectF();
    private Rect rect = new Rect();

    private float w, h;

    private int mDuration = 300;
    private Interpolator mInterpolator;
    private Animator.AnimatorListener mAnimationListener;
    private ValueAnimator mValueAnimator;

    public PieGraph(Context context) {
        this(context, null);
    }

    public PieGraph(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PieGraph(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        init();
    }

    void init(){
        PieSlice s = new PieSlice(100000);
        s.setTitle("");
        s.setGoalValue(0);
        s.setColor(Color.TRANSPARENT);
        addSlice(s);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.w = w;
        this.h = h;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        w = MeasureSpec.getSize(widthMeasureSpec);
        setTextSize(w);
        h = ((w / 4.0f) + (textHeight*2 + ((w/4.0f)*0.2f))) * 2.0f;

        this.setMeasuredDimension((int)w, (int)h);
        this.setLayoutParams(new LinearLayout.LayoutParams((int)w, (int)h));
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    float textSize;
    float textHeight;

    private void setTextSize(float width){
        textSize = width/18;
        mPaint.setTextSize(textSize);
        mPaint.getTextBounds("ABCDEFGHIJKLMNOPRSTUVWXYZabcdefhiklmnorstuvwxz", 0, 46, rect);
        textHeight = rect.height();
    }

    public void onDraw(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);

        float sum = 0;
        for(int i = 1; i < mSlices.size(); i++){
            sum += mSlices.get(i).getGoalValue();
        }
        if(sum < 0.0001) return;

        mPaint.setColor(Color.BLACK);

        canvas.drawLine(0+3, 0+3, w-3, 0+3, mPaint);
        canvas.drawLine(0+3, 0+3, 0+3, h-3, mPaint);
        canvas.drawLine(w-3, 0+3, w-3, h-3, mPaint);
        canvas.drawLine(0+3, h-3, w-3, h-3, mPaint);

        float midX, midY, radius;

        mPaint.reset();
        mPaint.setAntiAlias(true);

        float currentAngle = 270;
        float currentSweep = 0;
        float totalValue = 0;

        midX = w / 2;
        midY = h / 2;
        radius = midX - midX/2;

        for (PieSlice slice : mSlices)
            totalValue += slice.getValue();

        if(totalValue < 0.0001) {
            mSlices.get(0).setValue(1);
            totalValue = 1;
        }

        for (PieSlice slice : mSlices) {
            mPaint.setColor(slice.getColor());
            currentSweep = (slice.getValue() / totalValue) * (360);

            mRectF.set(midX - radius, midY - radius, midX + radius, midY + radius);
            canvas.drawArc(mRectF, currentAngle, currentSweep, true, mPaint);

            if(slice.getValue() > 0.01) {
                String title = slice.getTitle();

                mPaint.setTextSize(textSize);
                mPaint.setColor(Color.BLACK);
                mPaint.getTextBounds(title, 0, title.length(), rect);

                float alpha = (currentAngle + currentSweep/2 + 90) * (float)Math.PI / 180;
                float sin_a = (float)Math.sin(alpha);
                float cos_a = (float)Math.cos(alpha);
                float x = sin_a * radius * 1.2f + midX;
                float y =-cos_a * radius * 1.2f + midY;
                float xOffset = rect.width() / 2 * sin_a - rect.width() / 2;
                float yOffset = rect.height() / 2 * (1 - cos_a);

                canvas.drawText(title, x + xOffset, y + yOffset, mPaint);
            }
            currentAngle = currentAngle + currentSweep;
        }
    }

    public List<PieSlice> getSlices() {
        return mSlices.subList(1, mSlices.size());
    }

    public void addSlice(PieSlice slice) {
        mSlices.add(slice);
        postInvalidate();
    }

    @Override
    public int getDuration() {
        return mDuration;
    }

    @Override
    public void setDuration(int duration) {mDuration = duration;}

    @Override
    public Interpolator getInterpolator() {
        return mInterpolator;
    }

    @Override
    public void setInterpolator(Interpolator interpolator) {mInterpolator = interpolator;}


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    @Override
    public boolean isAnimating() {
        if(mValueAnimator != null)
            return mValueAnimator.isRunning();
        return false;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    @Override
    public boolean cancelAnimating() {
        if (mValueAnimator != null)
            mValueAnimator.cancel();
        return false;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    @Override
    public void animateToGoalValues() {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
            Log.e("KCS", "Animation not supported on api level 12 and below. Returning without animating.");
            return;
        }

        if (mSlices.get(0).getValue() > 0.0001) {
            int sum = 0;
            for (PieSlice p : mSlices)
                sum += p.getGoalValue();
            mSlices.get(0).setValue(sum);
        }

        if (mValueAnimator != null)
            mValueAnimator.cancel();

        for (PieSlice s : mSlices)
            s.setOldValue(s.getValue());
        ValueAnimator va = ValueAnimator.ofFloat(0, 1);
        mValueAnimator = va;
        va.setDuration(getDuration());
        if (mInterpolator == null) mInterpolator = new LinearInterpolator();
        va.setInterpolator(mInterpolator);
        if (mAnimationListener != null) va.addListener(mAnimationListener);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = Math.max(animation.getAnimatedFraction(), 0.01f);
                for (PieSlice s : mSlices) {
                    float x = s.getGoalValue() - s.getOldValue();
                    s.setValue(s.getOldValue() + (x * f));
                }
                postInvalidate();
            }
        });
        va.start();
    }

    @Override
    public void setAnimationListener(Animator.AnimatorListener animationListener) { mAnimationListener = animationListener;}
}
