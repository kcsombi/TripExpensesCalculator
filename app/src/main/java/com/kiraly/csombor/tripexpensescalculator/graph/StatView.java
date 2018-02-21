package com.kiraly.csombor.tripexpensescalculator.graph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.kiraly.csombor.tripexpensescalculator.R;

/**
 * Created by Király Csombor on 2017. 12. 13..
 */

public class StatView extends View {

    private int paidAmountSum = 0;
    private int paidAmountAvgByPersons = 0;
    private int paidAmountAvgByPayments = 0;

    private Paint mPaint = new Paint();
    private Rect rect = new Rect();

    float w, h;

    public StatView(Context context) {
        this(context, null);
    }

    public StatView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StatView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        w = View.MeasureSpec.getSize(widthMeasureSpec);
        setTextSize(w);
        float textSpace = w/48;
        h = (textHeight + textSpace) * 3 + textSpace * 5;

        this.setMeasuredDimension((int) w, (int) h);
        this.setLayoutParams(new LinearLayout.LayoutParams((int) w, (int) h));

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    float textSize;
    float textHeight;

    private void setTextSize(float width) {
        textSize = width / 18;
        mPaint.setTextSize(textSize);
        mPaint.getTextBounds("ABCDEFGHIJKLMNOPRSTUVWXYZabcdefhiklmnorstuvwxz", 0, 46, rect);
        textHeight = rect.height();
    }

    public void onDraw(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);

        if (paidAmountSum == 0) return;

        mPaint.setColor(Color.BLACK);

        canvas.drawLine(0 + 3, 0 + 3, w - 3, 0 + 3, mPaint);
        canvas.drawLine(0 + 3, 0 + 3, 0 + 3, h - 3, mPaint);
        canvas.drawLine(w - 3, 0 + 3, w - 3, h - 3, mPaint);
        canvas.drawLine(0 + 3, h - 3, w - 3, h - 3, mPaint);

        float padding = w / 18;

        mPaint.reset();
        mPaint.setColor(Color.BLACK);
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(textSize);

        float textSpace = w / 48;
        float currentHeight = textSize + textSpace*2;

        String line = getContext().getString(R.string.paid_amount) + paidAmountSum;
        canvas.drawText(line, padding, currentHeight, mPaint);
        currentHeight += textSize + textSpace;
        line = getContext().getString(R.string.per_person_paid_amount) + paidAmountAvgByPersons;
        canvas.drawText(line, padding, currentHeight, mPaint);
        currentHeight += textSize + textSpace;
        line = getContext().getString(R.string.per_payment_paid_amount) + paidAmountAvgByPayments;
        canvas.drawText(line, padding, currentHeight, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    public void setData(int sum, int avgPerson, int avgPayment){
        paidAmountSum = sum;
        paidAmountAvgByPersons = avgPerson;
        paidAmountAvgByPayments = avgPayment;

        postInvalidate();
    }

}
