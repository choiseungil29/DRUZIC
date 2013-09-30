package dif.clogic.druzic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.*;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.HashMap;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created with IntelliJ IDEA.
 * User: choeseung-il
 * Date: 13. 7. 23.
 * Time: 오후 2:40
 * To change this template use File | Settings | File Templates.
 */
public class HandlerView extends View {

    private String[] CONSTANT_BG = { "bg_1", "bg_2", "bg_space" };
    private int[] BG_SEQUENCE = { 0, 2, 1, 2, 1, 2, 1, 2 };
    private int bgCodeIdx = 0;

    private HashMap<String, int[][]> ColorTable;

    private int nowCodeListIdx = 0;

    private int[] nowCode;
    private int nowCodeIdx = 0;

    private HashMap<String, Integer> SoundTable;
    private SoundPool sPool;

    private boolean isTouching = false;

    private Stack<Point> pointStack;

    private long a = 0, b = 0;

    private Context mContext;

    private WindowManager wMgr;
    private Point outSize;

    public static Timer timer;

    ////////

    private Paint paint = new Paint();
    private Path path = new Path();

    private Point point = new Point();

    private float lastTouchX;
    private float lastTouchY;
    private final RectF dirtyRect = new RectF();

    private static final float STROKE_WIDTH = 5f;

    public static String melodySequenceList = "";

    private Bitmap bg;

    /** Need to track this so the dirty region can accommodate the stroke. **/
    private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;

    public HandlerView(Context context, final String color) {
        super(context);

        bg = BitmapFactory.decodeResource(getResources(), R.drawable.background);

        mContext = context;

        outSize = new Point();
        wMgr = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        wMgr.getDefaultDisplay().getSize(outSize);

        ColorTable = new HashMap<String, int[][]>();
        ColorTable.put("RED", ChordReference.redPackage);
        ColorTable.put("MINT", ChordReference.cyanPackgage);
        ColorTable.put("ORANGE", ChordReference.yellowPackage);
        ColorTable.put("BLUE", ChordReference.bluePackage);

        SoundTable = new HashMap<String, Integer>();
        SoundTable.get(ChordReference.melodyList[0]);

        sPool = new SoundPool(256, AudioManager.STREAM_MUSIC, 0);

        pointStack = new Stack<Point>();

        for(int i=0; i<ChordReference.melodyList.length; i++) {
            SoundTable.put(ChordReference.melodyList[i], sPool.load(context, getResources().getIdentifier(ChordReference.melodyList[i], "raw", "dif.clogic.druzic"), 0));
        }

        for(int i=0; i<CONSTANT_BG.length; i++) {
            SoundTable.put(CONSTANT_BG[i], sPool.load(context, getResources().getIdentifier(CONSTANT_BG[i], "raw", "dif.clogic.druzic"), 0));
        }

        melodySequenceList = new String();

        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {

                if(sPool == null)
                    return;

                a = System.currentTimeMillis();

                long c = a - b;

                if(c < 250) {
                    try {
                        Thread.sleep(250 - c);
                    } catch (InterruptedException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }

                if((bgCodeIdx != 0) &&
                        (bgCodeIdx % BG_SEQUENCE.length == 0)) {
                    //nowCode = CODE_ARRAY[nowCodeListIdx%CODE_ARRAY.length];
                    nowCodeListIdx++;
                }

                nowCode = ColorTable.get(color)[nowCodeListIdx%ColorTable.get(color).length];

                sPool.play(SoundTable.get(CONSTANT_BG[BG_SEQUENCE[bgCodeIdx%BG_SEQUENCE.length]]), 1, 1, 0, 0, 1);

                String raw = "";
                if(isTouching == true) {

                    if(isUp(pointStack)) {
                        if(nowCodeIdx < (nowCode.length - 1)) {
                            nowCodeIdx++;
                        }
                    }
                    else {
                        if(nowCodeIdx > 0) {
                            nowCodeIdx--;
                        }
                    }

                    sPool.play(SoundTable.get(ChordReference.melodyList[nowCode[nowCodeIdx%nowCode.length]]), 1, 1, 0, 0, 1);
                    raw = ChordReference.melodyList[nowCode[nowCodeIdx%nowCode.length]];
                    raw.replaceAll("_", "");
                    raw = raw.toUpperCase();
                    //raw += "q";
                } else {
                    //raw = "Rq";
                    raw = "R";
                }

                bgCodeIdx++;

                melodySequenceList += " " + raw;

                b = System.currentTimeMillis();
            }
        };

        timer.schedule(timerTask, 250, 250);
        b = System.currentTimeMillis();

        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(STROKE_WIDTH);

        if(color.equals("RED")) {
            paint.setColor(Color.rgb(135, 47, 47));
        } else if(color.equals("MINT")) {
            paint.setColor(Color.rgb(18, 165, 143));
        } else if(color.equals("ORANGE")) {
            paint.setColor(Color.rgb(212, 123, 0));
        } else if(color.equals("BLUE")) {
            paint.setColor(Color.rgb(40, 138, 181));
        } else {
            paint.setColor(Color.BLACK);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float eventX = event.getX();
        float eventY = event.getY();
        pointStack.push(new Point((int)event.getX(), (int)event.getY()));

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                path.moveTo(eventX, eventY);
                lastTouchX = eventX;
                lastTouchY = eventY;

                isTouching = true;

                for(int i=0; i<nowCode.length; i++) {
                    if((outSize.y/nowCode.length * i <= event.getY()) &&
                            (outSize.y/nowCode.length * (i+1) > event.getY())) {
                        nowCodeIdx = nowCode.length - (i+1);
                    }
                }

                return true;
            case MotionEvent.ACTION_MOVE:

                resetDirtyRect(eventX, eventY);

                // When the hardware tracks events faster than they are delivered, the
                // event will contain a history of those skipped points.
                int historySize = event.getHistorySize();
                for (int i = 0; i < historySize; i++) {
                    float historicalX = event.getHistoricalX(i);
                    float historicalY = event.getHistoricalY(i);
                    expandDirtyRect(historicalX, historicalY);
                    path.lineTo(historicalX, historicalY);
                }

                // After replaying history, connect the line to the touch point.
                path.lineTo(eventX, eventY);

                if(bgCodeIdx%BG_SEQUENCE.length == 0) {

                    for(int i=0; i<nowCode.length; i++) {
                        if((outSize.y/nowCode.length * i <= event.getY()) &&
                                (outSize.y/nowCode.length * (i+1) > event.getY())) {
                            nowCodeIdx = nowCode.length - (i+1);
                        }
                    }
                }

                break;
            case MotionEvent.ACTION_UP:

                resetDirtyRect(eventX, eventY);

                // When the hardware tracks events faster than they are delivered, the
                // event will contain a history of those skipped points.
                historySize = event.getHistorySize();
                for (int i = 0; i < historySize; i++) {
                    float historicalX = event.getHistoricalX(i);
                    float historicalY = event.getHistoricalY(i);
                    expandDirtyRect(historicalX, historicalY);
                    path.lineTo(historicalX, historicalY);
                }

                // After replaying history, connect the line to the touch point.
                path.lineTo(eventX, eventY);

                isTouching = false;
                break;
            default:
                return false;
        }

        invalidate(
                (int) (dirtyRect.left - HALF_STROKE_WIDTH),
                (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

        lastTouchX = eventX;
        lastTouchY = eventY;

        return false;
    }

    public boolean isUp(Stack<Point> pointStack) {
        Point n = null;
        Point p = null;
        try {
            n = pointStack.pop();
            p = pointStack.pop();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(n.y < p.y)
            return true;

        return false;
    }

    public void clear() {
        path.reset();

        // Repaints the entire view.
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(bg, 0, 0, paint);
        canvas.drawPath(path, paint);
    }

    private void expandDirtyRect(float historicalX, float historicalY) {
        if (historicalX < dirtyRect.left) {
            dirtyRect.left = historicalX;
        } else if (historicalX > dirtyRect.right) {
            dirtyRect.right = historicalX;
        }
        if (historicalY < dirtyRect.top) {
            dirtyRect.top = historicalY;
        } else if (historicalY > dirtyRect.bottom) {
            dirtyRect.bottom = historicalY;
        }
    }

    /**
     * Resets the dirty region when the motion event occurs.
     */
    private void resetDirtyRect(float eventX, float eventY) {

        // The lastTouchX and lastTouchY were set when the ACTION_DOWN
        // motion event occurred.
        dirtyRect.left = Math.min(lastTouchX, eventX);
        dirtyRect.right = Math.max(lastTouchX, eventX);
        dirtyRect.top = Math.min(lastTouchY, eventY);
        dirtyRect.bottom = Math.max(lastTouchY, eventY);
    }

    public float getX() {
        return this.point.x;
    }

    public float getY() {
        return this.point.y;
    }

    /**
     * Created with IntelliJ IDEA.
     * User: choeseung-il
     * Date: 13. 7. 25.
     * Time: 오전 8:41
     * To change this template use File | Settings | File Templates.
     */
    public static class ColorActivity extends Activity {

        private int term = 0;

        private ImageView view;

        private RelativeLayout selectView;

        private ImageButton nextButton;
        private ImageButton beforeButton;

        String[] color = {"RED", "MINT", "ORANGE", "BLUE"};

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            nextButton = (ImageButton)findViewById(R.id.nextButton);
            beforeButton = (ImageButton)findViewById(R.id.beforeButton);

            nextButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    //To change body of implemented methods use File | Settings | File Templates.
                    Intent intent = new Intent(ColorActivity.this, MainActivity.class);
                    intent.putExtra("color", color[term%color.length]);
                    startActivity(intent);
                }
            });

            beforeButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    //To change body of implemented methods use File | Settings | File Templates.
                    Intent intent = new Intent(ColorActivity.this, StartActivity.class);
                    startActivity(intent);
                }
            });

        }

        @Override
        public void onWindowFocusChanged(boolean hasFocus) {
            selectView = (RelativeLayout)findViewById(R.id.selectView);
            selectView.setBackgroundResource(R.drawable.red);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {

            if(event.getAction() == MotionEvent.ACTION_UP)
            {
                term++;
                switch(term%color.length)
                {
                    case 0:
                        selectView.setBackgroundResource(R.drawable.red);
                        break;
                    case 1:
                        selectView.setBackgroundResource(R.drawable.mint);
                        break;
                    case 2:
                        selectView.setBackgroundResource(R.drawable.orange);
                        break;
                    case 3:
                        selectView.setBackgroundResource(R.drawable.blue);
                        break;
                }
            }
            return false;
        }
    }
}
