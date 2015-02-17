package ca.utoronto.flapcheck;

import android.content.Context;
import android.hardware.Camera;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by kmurray on 17/02/15.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
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
}
