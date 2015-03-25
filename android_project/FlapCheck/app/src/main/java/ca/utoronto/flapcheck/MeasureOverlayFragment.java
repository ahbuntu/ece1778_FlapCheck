package ca.utoronto.flapcheck;


import android.app.Activity;
import android.graphics.Point;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import ca.utoronto.flapcheck.MeasurementInterface.MeasurementFragmentListener;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class MeasureOverlayFragment extends Fragment implements
        TapSelectOverlay.TapSelectOverlayListener
{
    private static String TAG ="MeasureOverlayFragment";
    private static final int MAX_POINTS = 3;



    private MeasurementFragmentListener mMeasureOverlayFragmentListener;

    private Patient mPatient;
    private List<File> photoReadings = new ArrayList<>();

    ImageView imagePhotoOverlay;
    Button actionButton;
    TextView textOverlayHeading;
    TapSelectOverlay tapSelectOverlay;
    List<Point> pointToMeasureList = new ArrayList<>();

    private int resumeCounter = -1;

    private PhotoMissingListener mPhotoMissingListener = null;
    private PointMeasurementListener mPointMeasurementListener = null;

    private int mPointIdx = -1; //The index of the selected point on the image
    private String measurementType;

    public interface PointMeasurementListener {
        public void onPointMeasure(String measureTypeNODE, int location_idx);
    }

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
                if (((File) o1).lastModified() < ((File) o2).lastModified()) {
                    return -1;
                } else if (((File) o1).lastModified() > ((File) o2).lastModified()) {
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

            DBLoaderPointToMeasure dbPointsLoader = new DBLoaderPointToMeasure(getActivity());
            List<PointToMeasure> pointsOverlayList =  dbPointsLoader.getPointsToMeasureForPatient(mPatient.getPatientId());
            for (PointToMeasure pointOverlay : pointsOverlayList) {
                Point p = new Point(pointOverlay.getPointX(), pointOverlay.getPointY());
                pointToMeasureList.add(p);
//            pointList is the location of the regions of interest on the image
//            A circle is drawn at each point in the list, which can then be selected by tapping
                tapSelectOverlay.setPointList(pointToMeasureList);
            }
            tapSelectOverlay.invalidate();

            setActionToSavePointsToMeasure();
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
        mPointMeasurementListener = (PointMeasurementListener) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");

        Bundle bundle = getArguments();
        measurementType = bundle.getString(MeasurementActivity.ARG_MEASUREMENT_TYPE);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_measure_overlay, container, false);

        textOverlayHeading = (TextView) view.findViewById(R.id.image_overlay_heading);
        actionButton = (Button) view.findViewById(R.id.action_button);


        FrameLayout photoFrame = (FrameLayout) view.findViewById(R.id.image_overlay_photo);

        imagePhotoOverlay = new ImageView(getActivity());
        tapSelectOverlay = new TapSelectOverlay(getActivity(), this);
        photoFrame.addView(imagePhotoOverlay);
        photoFrame.addView(tapSelectOverlay);

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
                //means photo already exists - save the points
//                setActionToCaptureMeasurement();
                setActionToSavePointsToMeasure();
            } else {
                //means photo missing - take photo
                setActionToTakePhoto();
            }

        }
    }


    /*
     * Callback from region overlay when tapped
     */
    @Override
    public void onTap(float x, float y) {

        if (pointToMeasureList.size() < MAX_POINTS) {
            //can draw additional points

            Point p = new Point(new Float(x).intValue(), new Float(y).intValue());

            pointToMeasureList.add(p);
//            pointList is the location of the regions of interest on the image
//            A circle is drawn at each point in the list, which can then be selected by tapping
            tapSelectOverlay.setPointList(pointToMeasureList);
            //TODO: might be better to save to the db right away?
        } else {
            //cannot add more points

            //Are we near a region?
            mPointIdx = tapSelectOverlay.findPointIndex(x, y);

            tapSelectOverlay.clearSelection();
            if(mPointIdx != -1) {
                //Found a close region, visually mark it
                tapSelectOverlay.addSelection(mPointIdx);
            }
        }
        tapSelectOverlay.invalidate(); //Re-draw

        if (pointToMeasureList.size() == MAX_POINTS) {
            actionButton.setEnabled(true);
        } else {
            actionButton.setEnabled(false);
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
                if(mPointIdx != -1) {
                    //Launch the appropriate measurement passing in the index of the region we are interested in.
                    switch (measurementType) {
                        case Constants.MEASUREMENT_TEMP:
                        case Constants.MEASUREMENT_COLOUR:
                            mPointMeasurementListener.onPointMeasure(measurementType, mPointIdx);
                            break;
                        default:
                            break;
                    }
                } else {
                    Toast.makeText(getActivity(), "You must select a measurement region.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * change the text and action button to save the points to measure
     */
    private void setActionToSavePointsToMeasure() {
        if (pointToMeasureList.size() == MAX_POINTS) {
            setActionToCaptureMeasurement();
            return;
        }
        textOverlayHeading.setText(R.string.image_overlay_heading_points);
        actionButton.setText("Save Points");
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pointIndex = 0;
                for (Point point : pointToMeasureList) {
                    DBLoaderPointToMeasure dbLoader = new DBLoaderPointToMeasure(getActivity());
                    long pointToMeasureID = dbLoader.addPointToMeasure(
                            new PointToMeasure(mPatient.getPatientId(),pointIndex,point.x, point.y));
                    if (pointToMeasureID == PointToMeasure.INVALID_ID) {
                        Toast.makeText(getActivity(), "Error trying to save the points.", Toast.LENGTH_SHORT)
                                .show();
                        return;
                    }
                    pointIndex++;
                }
                Toast.makeText(getActivity(), "Points saved.", Toast.LENGTH_SHORT).show();
                setActionToCaptureMeasurement();
            }
        });
    }


    @Override
    public void onPause() {
        Log.d(TAG, "onPause called");
        super.onPause();
    }

}
