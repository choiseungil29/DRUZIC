package com.example.druzic;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Stack;

public class TestView2 extends View {

    private Context mContext;

    private Stack<Stack<Point>> lineStack;
    private Stack<Point> pointStack;

    private MediaPlayer mPlayer;

    private MediaPlayer.OnCompletionListener completionListener;

    private String[] CONSTANT_BEAT = { "short_c", "mid_c", "long_c" };

    public TestView2(Context context) {
        super(context);

        mContext = context;

        lineStack = new Stack<Stack<Point>>();
        pointStack = new Stack<Point>();

        mPlayer = null;

        completionListener = new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                //To change body of implemented methods use File | Settings | File Templates.
                int standard = 718933/30;
                int distance = getLineDistance(pointStack);
                pointStack.clear();

                mPlayer.release();
                mPlayer = MediaPlayer.create(mContext, getCodeId(standard, distance));
                mPlayer.setOnCompletionListener(completionListener);
                mPlayer.start();

            }
        };

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.BLACK, PorterDuff.Mode.CLEAR);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(mPlayer != null)
            pointStack.push(new Point((int)event.getX(), (int)event.getY()));

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                return true;
            case MotionEvent.ACTION_MOVE:

                if(mPlayer == null) {
                    mPlayer = MediaPlayer.create(this.getContext(), R.raw.mid_c);
                    mPlayer.setOnCompletionListener(completionListener);
                    mPlayer.start();
                }

                break;
            case MotionEvent.ACTION_UP:

                lineStack.push((Stack<Point>) pointStack.clone());
                pointStack.clear();

                break;
            default:
                return false;
        }

        // Schedules a repaint.
        invalidate();
        return true;
    }

    public int getPointDistance(Point a, Point b) {
        int distance = (a.x - b.x)* (a.x - b.x) + (a.y - b.y)* (a.y - b.y);
        return distance;
    }

    public int getLineDistance(Stack<Point> pointStack) {

        int distance = 0;

        Point n = pointStack.pop();
        Point p = n;

        while(!pointStack.isEmpty()) {
            p = pointStack.pop();
            distance += getPointDistance(p, n);
            n = p;
        }

        return distance;
    }

    public int getCodeId(int standard, int distance) {

        for(int i=1;i<=3;i++) {
            if(distance >= 0 &&
                    distance < standard * i) {

                Log.i("DRUZIC", "CONSTANTBEAT : " + CONSTANT_BEAT[CONSTANT_BEAT.length-i]);
                Log.i("DRUZIC", "DISTANCE     : " + distance);

                return getResources().getIdentifier(CONSTANT_BEAT[CONSTANT_BEAT.length-i], "raw", "com.example.druzic");
            }
        }

        return getResources().getIdentifier(CONSTANT_BEAT[0], "raw", "com.example.druzic");

    }

}
