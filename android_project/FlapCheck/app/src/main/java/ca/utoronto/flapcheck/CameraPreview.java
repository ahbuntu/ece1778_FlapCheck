package ca.utoronto.flapcheck;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kmurray on 17/02/15.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "CameraPreview";
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private int mCameraId;
    private CameraPreviewOnTapListener mTapListener;
    private Camera.AutoFocusCallback mAutoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            if(success) {
                camera.cancelAutoFocus();
            }
        }
    };

    public interface CameraPreviewOnTapListener {
        void onCameraPreviewTap(float x, float y);
    }

    public CameraPreview(Context context, CameraPreviewOnTapListener tapListener, Camera camera, int cameraId) {
        super(context);

        if(camera == null) {
            throw new RuntimeException("Null mCamera in CameraPreview constructor");
        }

        mTapListener = tapListener;
        mCamera = camera;
        mCameraId = cameraId;

        //Set up callback for surface creation/destruction
        mHolder = getHolder();
        mHolder.addCallback(this);

        //Add the tap listener
    }

    public void surfaceCreated(SurfaceHolder holder) {
        //Set the preview drawing on the surface
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        //Release of preview handled in activity
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        //Handles rotation and resize events

        if(mCamera == null) {
            throw new RuntimeException("Null mCamera in CameraPreview.surfaceChanged");
        }

        if(mHolder.getSurface() == null) {
            //No surface yet
            return;
        }

        //Need to stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            e.printStackTrace(); //Should ignore?
        }

        //TODO: apply changes to preview
        //Note: Must follow results fo getSupportedPreviewSizes()

        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(mCameraId, info);

        int rotation = getDisplay().getRotation();

        int degrees = 0;
        switch(rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;

        }

        int result;
        if(info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; //Mirror result
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        mCamera.setDisplayOrientation(result);

        //Restart preview
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void doAutoFocus(Rect rect) {
            List<Camera.Area> focusRegions = new ArrayList<Camera.Area>();

            int meteringWeight = 1000;
            Camera.Area focusRegion = new Camera.Area(rect, meteringWeight);
            focusRegions.add(focusRegion);

            Camera.Parameters params = mCamera.getParameters();

            if(params.getMaxNumFocusAreas() <= focusRegions.size()) {
                params.setFocusAreas(focusRegions);
            } else {
                Toast.makeText(getContext(), String.format("Could not set %d focus areas. Max supported %d", focusRegions.size(), params.getMaxNumFocusAreas()), Toast.LENGTH_SHORT).show();
            }

            if(params.getMaxNumMeteringAreas() <= focusRegions.size()) {
                params.setMeteringAreas(focusRegions);
            } else {
                Toast.makeText(getContext(), String.format("Could not set %d metering areas. Max supported %d", focusRegions.size(), params.getMaxNumMeteringAreas()), Toast.LENGTH_SHORT).show();
            }

            //Temporarily disable auto-focus for Spiral 2 demo
//            mCamera.setParameters(params);
//
//            mCamera.autoFocus(mAutoFocusCallback);
    }

    //Touch event
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            //User tapped the preview
            float x = event.getX();
            float y = event.getY();

            Log.d(TAG, String.format("User tapped preview at (%f,%f)", x, y));
            mTapListener.onCameraPreviewTap(x, y);

            //Do auto-focus
            int rect_height = 100;
            int rect_width = 100;
            Rect rect = new Rect(
                                (int)x - rect_width/2,
                                (int) y - rect_height/2,
                                (int) x + rect_width/2,
                                (int) y + rect_height/2);
            doAutoFocus(rect);


        }

        return true; //We handled the event
    }
}


class CameraFocusOverlay extends View {
    private Paint mCirclePaint;
    private float mDiameter;
    private float mCentreX;
    private float mCentreY;

    public CameraFocusOverlay(Context context) {
        super(context);

        init();
    }

    public CameraFocusOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public void setCentreX(float x) {
        mCentreX = x;
    }

    public void setCentreY(float y) {
        mCentreY = y;
    }

    private void init() {
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setColor(Color.RED);
        DashPathEffect dashPathEffect = new DashPathEffect(new float[]{5,5}, 0.0f);
        mCirclePaint.setPathEffect(dashPathEffect);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(5);
    }

    @Override
    protected void onSizeChanged(int w, int h , int oldw, int oldh) {
        float xpad = (float)(getPaddingLeft() + getPaddingRight());
        float ypad = (float)(getPaddingTop()  + getPaddingBottom());

        float padded_w = w - xpad;
        float padded_h = h - ypad;

        mDiameter = 200;
        mCentreX = (float) w / 2;
        mCentreY = (float) h / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(mCentreX, mCentreY, mDiameter / 2, mCirclePaint);
        super.onDraw(canvas);
    }
}

class CameraFlapOverlay extends View {
    private Paint mBorderPaint;
    private float mDiameter;
    private float mCentreX;
    private float mCentreY;

    private float mLeft;
    private float mRight;
    private float mBottom;
    private float mTop;

    public CameraFlapOverlay(Context context) {
        super(context);

        init();
    }

    public CameraFlapOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public void setCentreX(float x) {
        mCentreX = x;
    }

    public void setCentreY(float y) {
        mCentreY = y;
    }

    private void init() {
        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setColor(Color.GREEN);
//        DashPathEffect dashPathEffect = new DashPathEffect(new float[]{5,5}, 0.0f);
//        mBorderPaint.setPathEffect(dashPathEffect);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(5);
    }

    @Override
    protected void onSizeChanged(int w, int h , int oldw, int oldh) {
        float xpad = (float)(getPaddingLeft() + getPaddingRight());
        float ypad = (float)(getPaddingTop()  + getPaddingBottom());

        float padded_w = w - xpad;
        float padded_h = h - ypad;

        mDiameter = 300;
        mCentreX = (float) w / 2;
        mCentreY = (float) h / 2;

        mLeft = 0.1f*w;
        mRight = 0.9f*w;
        mBottom = 0.1f*h;
        mTop = 0.9f*h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(mLeft, mBottom, mRight, mTop, mBorderPaint);
        super.onDraw(canvas);
    }
}
