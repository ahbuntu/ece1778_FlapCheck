package ca.utoronto.flapcheck;


import android.app.Activity;
import android.app.DialogFragment;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class MeasurePhotoFragment extends Fragment
            implements CameraPreview.CameraPreviewOnTapListener
{
    private static String TAG ="MeasurePhotoFragment";

    public interface TakePhotoFragmentListener {
        long requestActivePatientId();
    }

    private TakePhotoFragmentListener mTakePhotoFragmentListener;
    private File lastPhoto = null;

    private int mCameraId;
    private Camera mCamera;
    private CameraPreview mPreview;
    private CameraFocusOverlay mPreviewOverlay;

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            TakePhotoFragmentListener activity = (TakePhotoFragmentListener) getActivity();



            File pictureDir = getActivity().getFilesDir();
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File pictureFile = new File(pictureDir, "IMG_" + timestamp + ".jpg"); //TODO generate a real filename

            Toast.makeText(getActivity(), "Saving Image to " + pictureFile, Toast.LENGTH_SHORT).show();

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data); //Save the actual data
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch(IOException e) {
                e.printStackTrace();
            }

            if(lastPhoto != null) {
                Toast.makeText(getActivity(), "Leaking photo" + lastPhoto, Toast.LENGTH_SHORT).show();
            }

            lastPhoto = pictureFile;

            //Restart the preview once the image has been saved
            camera.startPreview();
        }
    };

    public MeasurePhotoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mTakePhotoFragmentListener = (TakePhotoFragmentListener) activity;
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
        //Take the picture
        mCamera.takePicture(null, null, mPicture);

        mTakePhotoFragmentListener.requestActivePatientId();
    }

    public void onReceiveActivePatientId(long patientId) {
        moveLastPhotoToPatientDirectory(patientId);
    }

    public void moveLastPhotoToPatientDirectory(long patientId) {
        if(patientId != Patient.INVALID_ID) {
            if(lastPhoto != null) {
                //Look up tht patient so we know where to put the photo
                PatientOpenDBHelper dbHelper = new PatientOpenDBHelper(getActivity().getApplicationContext());
                Patient patient = dbHelper.getPatient(patientId);

                //TODO: query the DB to find the path to stick the photo in
                //    e.g.    File patientPicDir = patient.getImageDir();
                File patientPicDir = new File(getActivity().getFilesDir(), "patient_id");
                File targetFileLocation = new File(patientPicDir, lastPhoto.getName());

                //Move the image to the correct location
                lastPhoto.renameTo(targetFileLocation);

                Toast.makeText(getActivity(), "Moved Image to " + targetFileLocation, Toast.LENGTH_SHORT).show();

                lastPhoto = null;
            } else {
                Log.e(TAG, "Attempted to move last photo to patient directory, but last photo was null!");
            }
        }
    }

    protected void acquireCamera() {
        mCamera = Camera.open(mCameraId);
        if(mCamera == null) {
            throw new RuntimeException("Got null Camera object");
        }

        //Create the preview
        mPreview = new CameraPreview(getActivity(), this, mCamera, mCameraId);
        FrameLayout preview = (FrameLayout) getView().findViewById(R.id.photo_preview);
        preview.addView(mPreview);

        //Add the overlay on top of it
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

    public void onCameraPreviewTap(float x, float y) {
        //Perform auto-focus

        //First update the overlay
        mPreviewOverlay.setCentreX(x);
        mPreviewOverlay.setCentreY(y);
        mPreviewOverlay.invalidate();

        //Now set autofocus
    }

}
