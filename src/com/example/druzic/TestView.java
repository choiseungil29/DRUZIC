package com.example.druzic;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.SoundPool;
import android.os.Debug;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class TestView extends View {
	private Paint paint = new Paint();
	private Path path = new Path();

    private static Stack<Point> pointStack;
	
	//private final int LONG_SOUND_DISTANCE = 8986;
	//private final int MIDDLE_SOUND_DISTNACE = 11982;
	//private final int SHORT_SOUND_DISTANCE = 17973;

    private static MediaPlayer s_mp;
    private static MediaPlayer m_mp;
    private static MediaPlayer l_mp;

    private static OnCompletionListener completeListener;
	
	private static Context mContext;
	
	boolean isTouching = false;
	boolean isFlag = false;

	public TestView(Context context) {
		super(context);

        mContext = this.getContext();

        pointStack = new Stack<Point>();

        s_mp = MediaPlayer.create(mContext, R.raw.short_c);
        m_mp = MediaPlayer.create(mContext, R.raw.mid_c);
        l_mp = MediaPlayer.create(mContext, R.raw.long_c);

        completeListener = new OnCompletionListener() {
                @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                //To change body of implemented methods use File | Settings | File Templates.
                if(isTouching == false) {
                    return;
                }

                Point nowPoint = pointStack.pop();
                Point prevPoint = pointStack.pop();

                if(getDistance(prevPoint, nowPoint) < 11982) {
                    l_mp.release();
                    l_mp = MediaPlayer.create(mContext, R.raw.long_c);
                    l_mp.setOnCompletionListener(completeListener);
                    l_mp.start();
                    Log.i("DRUZIC", "long : " + getDistance(prevPoint, nowPoint));
                } else if(getDistance(prevPoint, nowPoint) < 23964) {
                    m_mp.release();
                    m_mp = MediaPlayer.create(mContext, R.raw.mid_c);
                    m_mp.setOnCompletionListener(completeListener);
                    m_mp.start();
                    Log.i("DRUZIC", "middle : " + getDistance(prevPoint, nowPoint));
                } /*else if(getDistance(prevPoint, nowPoint) < SHORT_SOUND_DISTANCE) {
                    s_mp.release();
                    s_mp = MediaPlayer.create(mContext, R.raw.short_c);
                    s_mp.setOnCompletionListener(completeListener);
                    s_mp.start();
                    Log.i("DRUZIC", "short : " + getDistance(prevPoint, nowPoint));
                } */else {
                    Log.i("DRUZIC", "touch move event else!");
                    Log.i("DRUZIC", "s_mp");
                    s_mp.release();
                    s_mp = MediaPlayer.create(mContext, R.raw.short_c);
                    s_mp.setOnCompletionListener(completeListener);
                    s_mp.start();
                    Log.i("DRUZIC", "short : " + getDistance(prevPoint, nowPoint));
                }
            }
        };

        s_mp.setOnCompletionListener(completeListener);
        m_mp.setOnCompletionListener(completeListener);
        l_mp.setOnCompletionListener(completeListener);

		paint.setAntiAlias(true);
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeWidth(5f);
	}

	private Paint boxPaint = new Paint();
	
	@Override
	protected void onDraw(Canvas canvas) {
		boxPaint.setColor(Color.BLACK);
		canvas.drawColor(Color.BLACK, Mode.CLEAR);
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

            if(getDistance(prevPoint, nowPoint) < 11982) {
                Log.i("DRUZIC", "l_mp");
                l_mp.release();
                l_mp = MediaPlayer.create(mContext, R.raw.long_c);
                l_mp.setOnCompletionListener(completeListener);
                l_mp.start();
            } else if(getDistance(prevPoint, nowPoint) < 23964) {
                Log.i("DRUZIC", "m_mp");
                m_mp.release();
                m_mp = MediaPlayer.create(mContext, R.raw.mid_c);
                m_mp.setOnCompletionListener(completeListener);
                m_mp.start();
            } /*else if(getDistance(prevPoint, nowPoint) < SHORT_SOUND_DISTANCE) {
                Log.i("DRUZIC", "s_mp");
                s_mp.release();
                s_mp = MediaPlayer.create(mContext, R.raw.short_c);
                s_mp.setOnCompletionListener(completeListener);
                s_mp.start();
            } */else {
                Log.i("DRUZIC", "touch move event else!");
                Log.i("DRUZIC", "s_mp");
                s_mp.release();
                s_mp = MediaPlayer.create(mContext, R.raw.short_c);
                s_mp.setOnCompletionListener(completeListener);
                s_mp.start();
            }

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
		invalidate();
		return true;
	}
	
	public static int getDistance(Point a, Point b) {
		int distance = (a.x - b.x)* (a.x - b.x) + (a.y - b.y)* (a.y - b.y); 
		return distance;
	}
}