package com.iok.gfweather.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by crismas on 2016. 7. 25..
 */
public class KeyGuardView extends View implements View.OnTouchListener {

    Paint mPaint;
    public float mX;
    public float mY;

    public KeyGuardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mX = mY = -100;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Setting the color of the circle
        mPaint.setColor(Color.GREEN);
        mPaint.setAlpha(50);

        // Draw the circle at (x,y) with radius 15
        canvas.drawCircle(mX, mY, 75, mPaint);

        // Redraw the canvas
//        Log.i("KKKK", "draw circle," +mX+"," + mY);
        invalidate();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch(event.getAction()) {
            // When user touches the screen
            case MotionEvent.ACTION_MOVE:
                // Getting X coordinate
                mX = event.getX();
                // Getting Y Coordinate
                mY = event.getY();


        }
//        Log.i("KKKK", ""+mX + "," + mY);
        return true;
    }
}

