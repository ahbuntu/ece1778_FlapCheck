package ca.utoronto.flapcheck;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class TapSelectOverlay extends View {
    private static String TAG = "TapSelectOverlay";
    private Paint mCirclePaint;
    private Paint mSelectedPaint;
    private float mDiameter;
    private List<Point> mPointList;
    private Set<Integer> mSelection;
    private static float mSelectionMargin = 1.5f;

    private TapSelectOverlayListener mTapListener;

    public interface TapSelectOverlayListener {
        public void onTap(float x, float y);
    }

    public TapSelectOverlay(Context context, TapSelectOverlayListener tapListener) {
        super(context);

        mTapListener = tapListener;
        mPointList = new ArrayList<Point>();
        mSelection = new HashSet<Integer>();
        init();
    }

    public TapSelectOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public void setPointList(List<Point> pointList) {
        mPointList = pointList;
    }

    public List<Point> getPointList() {
        return mPointList;
    }

    public void addPoint(float x, float y) {
        Point pnt = new Point();
        pnt.set((int)x, (int)y);

        mPointList.add(pnt);
    }

    public int findPointIndex(float x, float y) {
        for(int i = 0; i < mPointList.size(); i++) {
            Point pnt = mPointList.get(i);
            float xdist = x - (float) pnt.x;
            float ydist = y - (float) pnt.y;
            float xpow = (float) Math.pow(xdist, 2);
            float ypow = (float) Math.pow(ydist, 2);
            float dist = (float) Math.sqrt( xpow + ypow );
            if(dist < mSelectionMargin*(mDiameter/2.0)) {
                Log.d(TAG, String.format("Found overlapping point at index %d", i));
                return i;
            }
        }
        Log.d(TAG, String.format("Found no overlapping point", mPointList.size()));
        return -1; //Invalid
    }

    public void clearSelection() {
        mSelection.clear();
    }

    public void addSelection(int idx) {
        mSelection.add(new Integer(idx));
    }

    private void init() {
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setColor(Color.RED);
        DashPathEffect dashPathEffect = new DashPathEffect(new float[]{5,5}, 0.0f);
        mCirclePaint.setPathEffect(dashPathEffect);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(5);

        mSelectedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSelectedPaint.setColor(Color.GREEN);
        mSelectedPaint.setPathEffect(dashPathEffect);
        mSelectedPaint.setStyle(Paint.Style.STROKE);
        mSelectedPaint.setStrokeWidth(5);
    }

    @Override
    protected void onSizeChanged(int w, int h , int oldw, int oldh) {
        float xpad = (float)(getPaddingLeft() + getPaddingRight());
        float ypad = (float)(getPaddingTop()  + getPaddingBottom());

        float padded_w = w - xpad;
        float padded_h = h - ypad;

        mDiameter = 200;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mPointList != null) {
            for (Integer i = 0; i < mPointList.size(); i++) {
                Point pnt = mPointList.get(i);

                if(mSelection.contains(i)) {
                    canvas.drawCircle(pnt.x, pnt.y, mDiameter / 2, mSelectedPaint);
                } else {
                    canvas.drawCircle(pnt.x, pnt.y, mDiameter / 2, mCirclePaint);
                }
            }
        }
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            //User tapped the preview
            float x = event.getX();
            float y = event.getY();

            Log.d(TAG, String.format("User tapped overlay at (%f,%f)", x, y));
            mTapListener.onTap(x, y);
        }

        return true; //We handled the event
    }
}
