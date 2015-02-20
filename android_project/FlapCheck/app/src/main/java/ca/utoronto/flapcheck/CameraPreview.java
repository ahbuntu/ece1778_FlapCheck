package ca.utoronto.flapcheck;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

/**
 * Created by kmurray on 17/02/15.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "CameraPreview";
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private int mCameraId;

    public CameraPreview(Context context, Camera camera, int cameraId) {
        super(context);

        if(camera == null) {
            throw new RuntimeException("Null mCamera in CameraPreview constructor");
        }

        mCamera = camera;
        mCameraId = cameraId;

        //Set up callback for surface creation/destruction
        mHolder = getHolder();
        mHolder.addCallback(this);
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

    //Touch event
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            //User tapped the preview
            float x = event.getX();
            float y = event.getY();

            Log.d(TAG, String.format("User tapped preview at (%f,%f)", x, y));


        }

        return true; //We handled the event
    }
}


class CameraFocusOverlay extends View {
    private Paint mCirclePaint;
    private float mDiameter;
    private float mCenterX;
    private float mCenterY;

    public CameraFocusOverlay(Context context) {
        super(context);

        init();
    }

    public CameraFocusOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    private void init() {
        mCirclePaint = new Paint();
        mCirclePaint.setColor(0xff101010);
    }

    @Override
    protected void onSizeChanged(int w, int h , int oldw, int oldh) {
        float xpad = (float)(getPaddingLeft() + getPaddingRight());
        float ypad = (float)(getPaddingTop()  + getPaddingBottom());

        float padded_w = w - xpad;
        float padded_h = h - ypad;

        mDiameter = Math.min(padded_w, padded_h);
        mCenterX = (float) w / 2;
        mCenterY = (float) h / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(mCenterX, mCenterY, mDiameter/2, mCirclePaint);
    }


}