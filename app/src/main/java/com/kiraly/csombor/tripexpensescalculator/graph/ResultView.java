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

import com.kiraly.csombor.tripexpensescalculator.model.solver.Debit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Király Csombor on 2017. 12. 13..
 */

public class ResultView extends View {
    private List<Debit> mDebits = new ArrayList<>();
    private Paint mPaint = new Paint();
    private Rect rect = new Rect();

    float w, h;

    public ResultView(Context context) {
        this(context, null);
    }

    public ResultView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ResultView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
    }

    public void calculateHeight() {
        if (w < 1) return;

        setTextSize(w);
        h = (textHeight + w / 48) * (mDebits.size() + 4);

        this.setLayoutParams(new LinearLayout.LayoutParams((int) w, (int) h));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        w = View.MeasureSpec.getSize(widthMeasureSpec);
        setTextSize(w);
        h = (textHeight + w / 48) * (mDebits.size() + 4);

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

        if (mDebits.size() == 0) return;

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
        float currentHeight = textSize + textSpace;

        for (Debit debit : mDebits) {
            String line = debit.from.name + " -> " + debit.to.name + ": " + debit.amount;
            canvas.drawText(line, padding, currentHeight, mPaint);

            currentHeight += textSize + textSpace;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    public void setDebits(List<Debit> debits) {
        mDebits = debits;
        postInvalidate();
    }

    public void removeDebits() {
        mDebits.clear();
        postInvalidate();
    }

}