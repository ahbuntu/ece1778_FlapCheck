package ca.utoronto.flapcheck;


import android.app.Activity;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import ca.utoronto.flapcheck.MeasurementInterface.MeasurementFragmentListener;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class MeasureOverlayFragment extends Fragment
{
    private static String TAG ="MeasureOverlayFragment";

    private MeasurementFragmentListener mMeasureOverlayFragmentListener;
    private File lastPhoto = null;

    private Patient mPatient;
    private List<File> photoReadings = new ArrayList<>();

    ImageView imagePhotoOverlay;
    Button actionButton;
    TextView textOverlayHeading;

    private int resumeCounter = -1;

    private PhotoMissingListener mPhotoMissingListener = null;
    public interface PhotoMissingListener {
        public void onPhotoMissing();
    }

    /**
     * this method is invoked during the fragment's onAttach, and set by the activity
     * @param mPatientID the database row ID of the patient to retrieve
     */
    public void setmPatient(long mPatientID) {
        Log.d(TAG, "setmPatient called");
        DBLoaderPatient dbLoader = new DBLoaderPatient(getActivity());
        this.mPatient = dbLoader.getPatient(mPatientID);
        //TODO: need to detect whether this is a new patient that was created
        setImagePhotoOverlay();
    }

    /**
     *
     */
    private void setImagePhotoOverlay() {
        File sourceDir = new File(mPatient.getPatientPhotoPath());
        //taken from here - http://stackoverflow.com/questions/6320639/how-to-sort-files-from-a-directory-by-date-in-java
        File[] files = sourceDir.listFiles();
        Arrays.sort(files, new Comparator() {
            public int compare(Object o1, Object o2) {
                if (((File) o1).lastModified() > ((File) o2).lastModified()) {
                    return -1;
                } else if (((File) o1).lastModified() < ((File) o2).lastModified()) {
                    return +1;
                } else {
                    return 0;
                }
            }
        });
        if (files.length > 0) {
            //there are images associated with this patient
            photoReadings.add(files[0]);
            imagePhotoOverlay.setImageURI(Uri.fromFile(photoReadings.get(0))); //using the very first picture
            imagePhotoOverlay.setVisibility(View.VISIBLE);
            setActionToCaptureMeasurement();
        } else {
            //no images - prompt to take a picture
            setActionToTakePhoto();
            if (resumeCounter == 0) {
                //automatically trigger the photo picker only for the very first time
                mPhotoMissingListener.onPhotoMissing();
            }
        }
    }

    public MeasureOverlayFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(TAG, "onAttach called");
        mMeasureOverlayFragmentListener = (MeasurementFragmentListener) activity;
        mMeasureOverlayFragmentListener.requestActivePatientId();
        mPhotoMissingListener = (PhotoMissingListener) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_measure_overlay, container, false);

        textOverlayHeading = (TextView) view.findViewById(R.id.image_overlay_heading);
        actionButton = (Button) view.findViewById(R.id.action_button);
        imagePhotoOverlay = (ImageView) view.findViewById(R.id.image_overlay_photo);

        return view;
    }


    @Override
    public void onResume() {
        Log.d(TAG, "onResume called");
        super.onResume();
        resumeCounter++;
        if (mPatient == null) {
            setActionToSelectPatient();
        }
        else {
            setImagePhotoOverlay();
            // can only have a patient
            if (imagePhotoOverlay.getVisibility() == View.VISIBLE) {
                //means photo already exists - take measurement
                setActionToCaptureMeasurement();

            } else {
                //means photo missing - take photo
                setActionToTakePhoto();
            }

        }
    }

    /**
     * changes the text and action button to trigger the select patient dialog
     */
    private void setActionToSelectPatient() {
        textOverlayHeading.setText(R.string.image_overlay_heading_patient);
        actionButton.setText("Select Patient");
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMeasureOverlayFragmentListener.requestActivePatientId();
            }
        });
    }

    /**
     * changes the text and action button to take a photo
     */
    private void setActionToTakePhoto() {
        textOverlayHeading.setText(R.string.image_overlay_heading_photo);
        actionButton.setText("Take Photo");
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhotoMissingListener.onPhotoMissing();
            }
        });
    }

    /**
     * change the text and action button to capture the desired measurement
     */
    private void setActionToCaptureMeasurement() {
        textOverlayHeading.setText(R.string.image_overlay_heading_measure);
        actionButton.setText("Take Measurement");
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"need to figure out what to do here");
            }
        });
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause called");
        super.onPause();
    }

}
