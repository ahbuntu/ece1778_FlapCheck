package ca.utoronto.flapcheck;


import android.app.Activity;
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
import ca.utoronto.flapcheck.MeasurementInterface.MeasurementFragmentListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeasurePhotoFragment extends Fragment
            implements CameraPreview.CameraPreviewOnTapListener
{
    private static String TAG ="MeasurePhotoFragment";

    private MeasurementFragmentListener mMeasurePhotoFragmentListener;
    private File lastPhoto = null;

    private int mCameraId;
    private Camera mCamera;
    private CameraPreview mPreview;
    private CameraFocusOverlay mFocusOverlay;
    private CameraFlapOverlay mFlapOverlay;

    private String photoMissing_Requester = null;
    private long photoMissing_PatientID;

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            MeasurementFragmentListener activity = (MeasurementFragmentListener) getActivity();

            File pictureDir = getActivity().getExternalCacheDir();
            pictureDir.mkdirs();
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File pictureFile = new File(pictureDir, "IMG_" + timestamp + ".jpg"); //TODO generate a real filename

//            Toast.makeText(getActivity(), "Saving Image temporarily to " + pictureFile, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Saving Image temporarily to " + pictureFile);
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

        mMeasurePhotoFragmentListener = (MeasurementFragmentListener) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_take_photo, container, false);

        mCameraId = 0; //TODO pick this properly

        //default to null value if missing
        photoMissing_Requester = getArguments().getString(Constants.ARG_PHOTO_MISSING_REQUESTER, null);
        photoMissing_PatientID = getArguments().getLong(Constants.ARG_PHOTO_MISSING_PATIENT);

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

        if (photoMissing_Requester != null) {
            mMeasurePhotoFragmentListener.requestActivePatientId();
        }
    }

    public void onReceiveActivePatientId(long patientId) {
        moveLastPhotoToPatientDirectory(patientId);
    }

    /**
     * moves photo from temporary file location to the path specific for the selected patient
     * @param patientId
     */
    public void moveLastPhotoToPatientDirectory(long patientId) {
        if(patientId != Patient.INVALID_ID) {
            if(lastPhoto != null) {
                //Look up tht patient so we know where to put the photo
                // ok to do these calls synchronously because we do want the user to be blocked if the
                // photo cannot be saved
                DBLoaderPatient dbHelper = new DBLoaderPatient(getActivity().getApplicationContext());
                Patient patient = dbHelper.getPatient(patientId);

                File patientPicDir = new File(patient.getPatientPhotoPath());
                File targetFileLocation = new File(patientPicDir, lastPhoto.getName());
                //Move the image to the correct location
                lastPhoto.renameTo(targetFileLocation);

                Toast.makeText(getActivity(), "Picture saved", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Moved image to " + targetFileLocation);
                lastPhoto = null;

                if (photoMissing_Requester != null) {
                    // take back to flap measurement mapping screen
                    getFragmentManager().popBackStack();
                }
            } else {
                Log.e(TAG, "Attempted to move last photo to patient directory, but last photo was null!");
            }
        }
    }

    public void removeLastPhoto() {
        if(lastPhoto != null) {
            Toast.makeText(getActivity(), String.format("Deleting %s since no patient was added or selected.", lastPhoto.getPath()), Toast.LENGTH_SHORT).show();
            lastPhoto.delete();
            lastPhoto = null;
        }
    }

    protected void acquireCamera() {
        mCamera = Camera.open(mCameraId);
        if(mCamera == null) {
            throw new RuntimeException("Got null Camera object");
        }
        FrameLayout frame = (FrameLayout) getView().findViewById(R.id.photo_preview);
        mFlapOverlay = new CameraFlapOverlay(getActivity());
        mFocusOverlay = new CameraFocusOverlay(getActivity());

        //Create the preview
        mPreview = new CameraPreview(getActivity(), this, mCamera, mCameraId);

        //Add the main camera preview
        frame.addView(mPreview);

        //Add the focus overlay on top of it
        frame.addView(mFocusOverlay);

        //Add the flap overlay
        frame.addView(mFlapOverlay);

    }

    protected void releaseCamera() {
        if(mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }

        //Detach the overlays
        FrameLayout preview = (FrameLayout) getView().findViewById(R.id.photo_preview);
        if(mFlapOverlay != null) {
            preview.removeView(mFlapOverlay);
            mFlapOverlay = null;
        }
        if(mFocusOverlay != null) {
            preview.removeView(mFocusOverlay);
            mFocusOverlay = null;
        }
        if(mPreview != null) {
            preview.removeView(mPreview);
            mPreview = null;
        }
    }

    public void onCameraPreviewTap(float x, float y) {
        //First update the overlay
        mFocusOverlay.setCentreX(x);
        mFocusOverlay.setCentreY(y);
        mFocusOverlay.invalidate(); //Force re-draw
    }

}
