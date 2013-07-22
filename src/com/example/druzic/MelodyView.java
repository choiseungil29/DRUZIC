package com.example.druzic;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import java.util.Stack;

import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;

import android.util.Log;

/**
 * Created with IntelliJ IDEA.
 * User: choeseung-il
 * Date: 13. 7. 20.
 * Time: 오후 10:42
 * To change this template use File | Settings | File Templates.
 */
public class MelodyView extends View {

    private Stack<Point> pointStack;

    private MediaPlayer mPlayer;

    private boolean isTouching = false;
    private boolean isFlag = false;

    private final String[] CONSTANT_CODE = {"c_1", "c_1s", "d_1", "d_1s", "e_1", "f_1", "f_1s", "g_1", "g_1s", "a_1", "a_1s", "b_1", "c_2"};
    private int nowCodeidx = 5;
    private String nowCode = CONSTANT_CODE[nowCodeidx];

    private Context mContext;

    private MediaPlayer.OnCompletionListener completionListener;

    public MelodyView(Context context) {
        super(context);

        mContext = context;

        pointStack = new Stack<Point>();
        mPlayer = new MediaPlayer();

        completionListener = new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                //To change body of implemented methods use File | Settings | File Templates.
                Point now = pointStack.pop();
                Point prev = pointStack.pop();

                mediaPlayer.release();

                if(isTouching == false)
                    return;

                mediaPlayer = MediaPlayer.create(mContext, getCodeId(prev, now));
                mediaPlayer.setOnCompletionListener(completionListener);
                mediaPlayer.start();
            }
        };
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.BLACK, PorterDuff.Mode.CLEAR);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        pointStack.push(new Point((int) event.getX(), (int) event.getY()));

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //path.moveTo(this.nowPoint.x, this.nowPoint.y);
                isTouching = true;

                return true;
            case MotionEvent.ACTION_MOVE:

                if(isFlag == false)
                {
                    isFlag = true;
                } else {
                    break;
                }

                Point nowPoint = pointStack.pop();
                Point prevPoint = pointStack.pop();

                mPlayer.release();
                mPlayer = MediaPlayer.create(mContext, getCodeId(prevPoint, nowPoint));
                mPlayer.setOnCompletionListener(completionListener);
                mPlayer.start();

                break;
            case MotionEvent.ACTION_UP:
                //path.lineTo(this.nowPoint.x, this.nowPoint.y);
                isFlag = false;
                isTouching = false;
                break;
            default:
                return false;
        }

        // Schedules a repaint.
        // invalidate();
        return true;
    }

    public int getDistance(Point a, Point b) {
        int distance = (a.x - b.x)* (a.x - b.x) + (a.y - b.y)* (a.y - b.y);
        return distance;
    }

    public int getCodeId(Point prev, Point now) {

        int standard = 11982/20;

        for(int i=1;i<=3;i++) {
            if((getDistance(prev, now) < standard * i) &&
                (getDistance(prev, now) >= standard * (i-1))) {

                if(now.y < prev.y) {
                    nowCodeidx += i;
                    if(nowCodeidx >= 12)
                        nowCodeidx = 12;
                } else if(now.y > prev.y) {
                    nowCodeidx -= i;
                    if(nowCodeidx <= 0)
                        nowCodeidx = 0;

                }

            }
        }
        Log.i("DRUZIC", "NODE CODE IDX IS " + nowCodeidx);

        nowCode = CONSTANT_CODE[nowCodeidx];
        Log.i("DRUZIC", "NOW CODE IS " + nowCode);
        return getResources().getIdentifier(nowCode, "raw", "com.example.druzic");
    }
}