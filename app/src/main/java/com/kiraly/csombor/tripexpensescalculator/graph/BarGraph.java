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
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * Created by Király Csombor on 2017. 11. 29..
 */

public class BarGraph extends View implements  HoloGraphAnimate {

    private ArrayList<Bar> mBars = new ArrayList<>();
    private Paint mPaint = new Paint();
    private Rect rect = new Rect();

    float w, h;

    private int mDuration = 300;
    private Interpolator mInterpolator;
    private Animator.AnimatorListener mAnimationListener;
    private ValueAnimator mValueAnimator;

    public BarGraph(Context context) {
        this(context, null);
    }

    public BarGraph(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BarGraph(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
    }

    public void calculateHeight(){
        if(w < 1) return;

        setTextSize(w);
        h = w/6.2f + (textHeight + w/48) * mBars.size();

        this.setLayoutParams(new LinearLayout.LayoutParams((int)w, (int)h));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        w = MeasureSpec.getSize(widthMeasureSpec);
        setTextSize(w);
        h = w/6 + (textHeight + w/48) * mBars.size();

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

        boolean allZero = true;
        for(Bar bar: mBars)
            if(bar.getGoalValue() > 0.0001)
                allZero = false;
        if(allZero) return;

        mPaint.setColor(Color.BLACK);

        canvas.drawLine(0+3, 0+3, w-3, 0+3, mPaint);
        canvas.drawLine(0+3, 0+3, 0+3, h-3, mPaint);
        canvas.drawLine(w-3, 0+3, w-3, h-3, mPaint);
        canvas.drawLine(0+3, h-3, w-3, h-3, mPaint);

        float graphCenter, graphWidth, maxValue;
        float padding = w/18;

        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(textSize);

        int longestLabelSize = 0;
        for(Bar b : mBars) {
            mPaint.getTextBounds(b.getTitle(), 0, b.getTitle().length(), rect);
            if (rect.width() > longestLabelSize)
                longestLabelSize = rect.width();
        }

        graphWidth = (w - longestLabelSize - 3*padding) / 2;
        graphCenter = longestLabelSize + 2*padding + graphWidth;

        maxValue = 1;
        for (Bar bar : mBars)
            if(Math.abs(bar.getValue()) > maxValue)
                maxValue = Math.abs(bar.getValue());

        float startY = w/36;
        float currentY = startY;

        //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
        //Draw numbers

        mPaint.setTextSize(textSize*0.65f);
        mPaint.setColor(Color.BLACK);

        currentY += w/24;
        mPaint.getTextBounds("0", 0, 1, rect);
        canvas.drawText("0", graphCenter - rect.width()/2, currentY, mPaint);

        String number = ((Integer)(int)maxValue).toString();
        mPaint.getTextBounds(number, 0, number.length(), rect);
        canvas.drawText(number, graphCenter + graphWidth - rect.width()/2, currentY, mPaint);
        canvas.drawText("-" + number, graphCenter - graphWidth - rect.width()/2, currentY, mPaint);

        //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
        //Draw lines

        currentY += w/24;
        mPaint.setColor(Color.BLACK);

        canvas.drawLine(graphCenter - graphWidth, currentY-w/72, graphCenter + graphWidth, currentY-w/72, mPaint);
        canvas.drawLine(graphCenter - graphWidth, currentY-w/36, graphCenter - graphWidth, currentY     , mPaint);
        canvas.drawLine(graphCenter + graphWidth, currentY-w/36, graphCenter + graphWidth, currentY     , mPaint);

        //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
        //Draw bars

        mPaint.setTextSize(textSize);

        float currentHeight = currentY + w/48;
        float barHeight = textHeight;
        float barSpace = w/48;

        for (Bar bar : mBars) {

            float width = bar.getValue() / maxValue * graphWidth;
            if(width < 0){
                mPaint.setColor(Color.RED);
                canvas.drawRect(graphCenter + width, currentHeight, graphCenter, currentHeight + barHeight, mPaint);
            } else {
                mPaint.setColor(Color.GREEN);
                canvas.drawRect(graphCenter, currentHeight, graphCenter + width, currentHeight + barHeight, mPaint);
            }

            mPaint.setColor(Color.BLACK);
            mPaint.setTextSize(textSize);
            canvas.drawText(bar.getTitle(), padding, currentHeight + textHeight, mPaint);
            Integer val = (int)bar.getValue();
            mPaint.setTextSize(textSize*0.65f);
            canvas.drawText(val.toString(), graphCenter + padding / 5, currentHeight + textHeight - textHeight*0.18f, mPaint);

            currentHeight += barHeight + barSpace;
        }
        mPaint.setColor(Color.BLACK);
        canvas.drawLine(graphCenter, w/12, graphCenter, currentHeight - barSpace + w/72, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    public ArrayList<Bar> getBars() {
        return mBars;
    }

    public void addBar(Bar bar) {
        mBars.add(bar);
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
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1){
            Log.e("KCS", "Animation not supported on api level 12 and below. Returning without animating.");
            return;
        }

        if (mValueAnimator != null)
            mValueAnimator.cancel();

        for (Bar b : mBars)
            b.setOldValue(b.getValue());
        ValueAnimator va = ValueAnimator.ofFloat(0,1);
        mValueAnimator = va;
        va.setDuration(getDuration());
        if (mInterpolator == null) mInterpolator = new LinearInterpolator();
        va.setInterpolator(mInterpolator);
        if (mAnimationListener != null) va.addListener(mAnimationListener);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = Math.max(animation.getAnimatedFraction(), 0.01f);
                for (Bar b : mBars) {
                    float x = b.getGoalValue() - b.getOldValue();
                    b.setValue(b.getOldValue() + (x * f));
                }
                postInvalidate();
            }});
        va.start();

    }

    @Override
    public void setAnimationListener(Animator.AnimatorListener animationListener) { mAnimationListener = animationListener;}
}
