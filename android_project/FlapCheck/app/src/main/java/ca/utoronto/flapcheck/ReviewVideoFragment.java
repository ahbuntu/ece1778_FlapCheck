package ca.utoronto.flapcheck;


import android.app.Activity;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReviewVideoFragment extends Fragment {
    private static String TAG = "ReviewVideoFragment";

    private ViewPager mVideoPager;
    private PagerAdapter mPagerAdapter;

    public interface ReviewVideoFragmentListener {
        Patient getPatient();
    }

    private ReviewVideoFragmentListener mListenerCallback;

    public ReviewVideoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListenerCallback = (ReviewVideoFragmentListener) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_review_video, container, false);

        Patient patient = mListenerCallback.getPatient();
        TextView patientName = (TextView) view.findViewById(R.id.video_review_patient_name);
        TextView patientMrn = (TextView) view.findViewById(R.id.video_review_patient_mrn);
        patientName.setText(patient.getPatientName());
        patientMrn.setText(patient.getPatientMRN());

        mVideoPager = (ViewPager) view.findViewById(R.id.video_pager);

        mPagerAdapter = new PhotoPagerAdapter(getActivity().getSupportFragmentManager());

        mVideoPager.setAdapter(mPagerAdapter);

        return view;
    }

    private class PhotoPagerAdapter extends FragmentPagerAdapter {
        private List<File> mRawVideoFiles;
        private List<File> mProcessedVideoFiles;
        private long patientOpTime;

        public PhotoPagerAdapter(FragmentManager fm) {
            super(fm);

            mRawVideoFiles = new ArrayList<File>();
            mProcessedVideoFiles = new ArrayList<File>();

            Patient patient = mListenerCallback.getPatient();
            patientOpTime = patient.getPatientOpDateTime();

            //TODO get the real path...
            File pictureDir = new File(patient.getPatientVidPath());

            //Fill the paths into a list
            File[] files = pictureDir.listFiles();
            if(files == null) {
                files = new File[0];
            }
            for(File file : files) {
                if(file.isFile()) {
                    mRawVideoFiles.add(file);
                    mProcessedVideoFiles.add(file);
                }
            }


        }

        @Override
        public Fragment getItem(int position) {
            Fragment frag = new ReviewVideoPageFragment();

            //Args to fragment
            Bundle args = new Bundle();

            File rawVideo =  mRawVideoFiles.get(position);
            File processedVideo =  mProcessedVideoFiles.get(position);

            //Calculate image time relative to post-op
            //First load the date/time info from the image

            args.putLong(ReviewVideoPageFragment.ARG_POST_OP_DELTA_TIME, 0); //TODO get real time

            args.putString(ReviewVideoPageFragment.ARG_RAW_VIDEO_PATH, rawVideo.getPath());
            args.putString(ReviewVideoPageFragment.ARG_PROCESSED_VIDEO_PATH, processedVideo.getPath());

            frag.setArguments(args);

            return frag;
        }

        @Override
        public int getCount() {
//            return 1;
            return mRawVideoFiles.size();
        }
    }

}
