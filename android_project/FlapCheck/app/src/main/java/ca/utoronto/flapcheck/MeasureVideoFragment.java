package ca.utoronto.flapcheck;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class MeasureVideoFragment extends Fragment {

    private static final int VIDEO_RECORD_DURATION = 20;
    private static String TAG = "MeasureVideoFragment";


    private MeasurementInterface.MeasurementFragmentListener mMeasureFragmentListener;
    private File lastVideo = null;

    public MeasureVideoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_measure_video, container, false);

        //This is just a placeholder fragment that launches the real video recorder
        Button record = (Button) view.findViewById(R.id.button_take_video);
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Launch the video camera intent
                Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                videoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, VIDEO_RECORD_DURATION);


                File tempDir = getActivity().getExternalCacheDir();

                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                File videoFile = new File(tempDir, "VID_" + timestamp + ".mp4");



                videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(videoFile));


                if(lastVideo != null) {
                    Log.d(TAG, "Leaking video file: " + lastVideo.getPath());
                }
                Log.d(TAG, "Saving video to " + videoFile.getPath());
                lastVideo = videoFile;
                getActivity().startActivityForResult(videoIntent, Constants.RECORD_VIDEO_REQUEST);
            }
        });

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mMeasureFragmentListener = (MeasurementInterface.MeasurementFragmentListener) activity;
    }

    public void videoRecorded(Uri videoFile) {
        mMeasureFragmentListener.requestActivePatientId();
    }

    public void moveLastVideoToPatientDirectory(long patientId) {
        if(patientId != Patient.INVALID_ID) {
            if (lastVideo != null) {
                //Look up tht patient so we know where to put the video
                // ok to do these calls synchronously because we do want the user to be blocked if the
                // photo cannot be saved
                DBLoaderPatient dbHelper = new DBLoaderPatient(getActivity().getApplicationContext());
                Patient patient = dbHelper.getPatient(patientId);

                File patientVidDir = new File(patient.getPatientVidPath());

                File targetFileLocation = new File(patientVidDir, lastVideo.getName());
                //Move the image to the correct location
                lastVideo.renameTo(targetFileLocation);

                Toast.makeText(getActivity(), "Video saved", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Moved video to " + targetFileLocation);
                lastVideo = null;
            }
        }
    }
}
