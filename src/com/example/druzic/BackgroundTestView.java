package com.example.druzic;

import android.content.Context;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: choeseung-il
 * Date: 13. 7. 22.
 * Time: 오후 5:22
 * To change this template use File | Settings | File Templates.
 */
public class BackgroundTestView extends View {

    private Context mContext;

    private MediaPlayer bgPlayer;
    private MediaPlayer.OnCompletionListener completionListener;

    private MediaPlayer melodyPlayer;

    private String[] CONSTANT_MELODY = { "c_1", "c_1s", "d_1", "d_1s", "e_1", "f_1", "f_1s", "g_1", "g_1s", "a_1", "a_1s",
            "b_1", "c_2", "c_2s", "d_2", "d_2s", "e_2", "f_2", "f_2s", "g_2", "g_2s", "a_2", "a_2s", "b_2", "c_3" };


    private String[] CONSTANT_BG = { "bg_1", "bg_2", "bg_space" };
    private int[] BG_SEQUENCE = { 0, 2, 1, 2, 1, 2, 1, 2 };
    private int bgCodeIdx = 0;

    private int[] CONST_C_CODE = { 0, 4, 7, 12, 16, 19, 24 };
    private int[] CONST_AM_CODE = { 9, 12, 16, 21, 24 };
    private int[] CONST_F_CODE = { 5, 9, 12, 17, 21, 24 };
    private int[] CONST_G_CODE = { 7, 11, 14, 19, 23 };
    private int[][] CODE_ARRAY = { CONST_C_CODE, CONST_AM_CODE, CONST_F_CODE, CONST_G_CODE };
    private int nowCodeListIdx = 0;

    private int[] nowCode = CONST_C_CODE;
    private int nowCodeIdx = 0;

    private Stack<Point> pointStack;

    public BackgroundTestView(Context context) {
        super(context);

        mContext = context;

        completionListener = new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                //To change body of implemented methods use File | Settings | File Templates.

                if(bgCodeIdx % BG_SEQUENCE.length == 0) {
                    nowCodeListIdx++;
                    nowCodeIdx = 0;
                }

                nowCode = CODE_ARRAY[nowCodeListIdx%CODE_ARRAY.length];

                if(nowCode.length >= nowCodeIdx)
                    nowCodeIdx++;

                melodyPlayer.release();
                melodyPlayer = MediaPlayer.create(mContext, getResources().getIdentifier(CONSTANT_MELODY[nowCode[nowCodeIdx%nowCode.length]], "raw", "com.example.druzic"));
                melodyPlayer.start();

                bgPlayer.release();
                bgPlayer = MediaPlayer.create(mContext, getResources().getIdentifier(CONSTANT_BG[BG_SEQUENCE[bgCodeIdx%BG_SEQUENCE.length]], "raw", "com.example.druzic"));
                bgPlayer.setOnCompletionListener(completionListener);
                bgPlayer.start();

                bgCodeIdx++;
            }
        };

        bgPlayer = MediaPlayer.create(mContext, getResources().getIdentifier(CONSTANT_BG[BG_SEQUENCE[bgCodeIdx%BG_SEQUENCE.length]], "raw", "com.example.druzic"));
        bgPlayer.setOnCompletionListener(completionListener);
        bgPlayer.start();
        bgCodeIdx++;

        melodyPlayer = MediaPlayer.create(mContext, getResources().getIdentifier(CONSTANT_MELODY[nowCode[nowCodeIdx%nowCode.length]], "raw", "com.example.druzic"));

        pointStack = new Stack<Point>();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        pointStack.push(new Point((int)event.getX(), (int)event.getY()));

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                pointStack.clear();
                return true;
            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_UP:

                pointStack.clear();
                break;
            default:
                return false;
        }

        // Schedules a repaint.
        invalidate();
        return true;
    }

    public int getCodeId(Stack<Point> pointStack) {

        Point end = pointStack.pop();
        Point start = pointStack.pop();

        while(!pointStack.isEmpty()) {
            start = pointStack.pop();
        }

        if(end.y < start.y) {

        }

        return 0;
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
}
