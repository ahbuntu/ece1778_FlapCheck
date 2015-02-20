package ca.utoronto.flapcheck;


import android.hardware.Camera;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
public class TakePhotoFragment extends Fragment {
    public interface TakePhotoFragmentListener {
        File getImageFileDir();
    }


    private int mCameraId;
    private Camera mCamera;
    private CameraPreview mPreview;
    private CameraFocusOverlay mPreviewOverlay;

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            TakePhotoFragmentListener activity = (TakePhotoFragmentListener) getActivity();

            File pictureDir = activity.getImageFileDir();
            File pictureFile = new File(pictureDir, "test.jpg"); //TODO generate a real filename

            Toast.makeText(getActivity(), "Saving Image to " + pictureFile, Toast.LENGTH_SHORT).show();

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch(IOException e) {
                e.printStackTrace();
            }

            //Restart the preview once the image has been saved
            camera.startPreview();
        }
    };

    public TakePhotoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_take_photo, container, false);

        mCameraId = 0; //TODO pick this properly

        Button captureButton = (Button) view.findViewById(R.id.capture_photo_button);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

        acquireCamera();
    }

    @Override
    public void onPause() {
        super.onPause();

        releaseCamera();
    }

    public void takePicture() {
        mCamera.takePicture(null, null, mPicture);
    }

    protected void acquireCamera() {
        mCamera = Camera.open(mCameraId);
        if(mCamera == null) {
            throw new RuntimeException("Got null Camera object");
        }

        //Create the preview
        mPreview = new CameraPreview(getActivity(), mCamera, mCameraId);
        FrameLayout preview = (FrameLayout) getView().findViewById(R.id.photo_preview);
        preview.addView(mPreview);

        //Add the overaly on top of it
        mPreviewOverlay = new CameraFocusOverlay(getActivity());
        preview.addView(mPreviewOverlay);
    }

    protected void releaseCamera() {
        if(mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

}
