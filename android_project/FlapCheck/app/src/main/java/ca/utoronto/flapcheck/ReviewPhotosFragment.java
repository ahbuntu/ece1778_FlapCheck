package ca.utoronto.flapcheck;


import android.app.Activity;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReviewPhotosFragment extends Fragment {
    private static String TAG = "ReviewPhotosFragment";

    private ViewPager mPager1;
    private ViewPager mPager2;
    private PagerAdapter mPagerAdapter;

    public interface ReviewPhotoFragmentListener {
        Patient getPatient();
    }

    private ReviewPhotoFragmentListener mListenerCallback;

    public ReviewPhotosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListenerCallback = (ReviewPhotoFragmentListener) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_review_photos, container, false);

        Patient patient = mListenerCallback.getPatient();
        TextView patientName = (TextView) view.findViewById(R.id.photo_review_patient_name);
        TextView patientMrn = (TextView) view.findViewById(R.id.photo_review_patient_mrn);
        patientName.setText(patient.getPatientName());
        patientMrn.setText(patient.getPatientMRN());

        mPager1 = (ViewPager) view.findViewById(R.id.image_pager_1);
        mPager2 = (ViewPager) view.findViewById(R.id.image_pager_2);

        mPagerAdapter = new PhotoPagerAdapter(getActivity().getSupportFragmentManager());

        mPager1.setAdapter(mPagerAdapter);
        mPager2.setAdapter(mPagerAdapter);

        return view;
    }

    private class PhotoPagerAdapter extends FragmentPagerAdapter {
        private List<File> mImageFiles;
        private long patientOpTime;

        public PhotoPagerAdapter(FragmentManager fm) {
            super(fm);

            mImageFiles = new ArrayList<File>();

            Patient patient = mListenerCallback.getPatient();
            patientOpTime = patient.getPatientOpDateTime();

            //TODO get the real path...
            File pictureDir = new File(getActivity().getFilesDir(), "patient_id");

            //Fill the paths into a list
            File[] files = pictureDir.listFiles();
            for(File file : files) {
                if(file.isFile()) {
                    mImageFiles.add(file);
                }
            }


        }

        @Override
        public Fragment getItem(int position) {
            Fragment frag = new ReviewPhotosPageFragment();
            Bundle args = new Bundle();

            File image =  mImageFiles.get(position);

            //Calculate image time relative to post-op
            //First load the date/time info from the image
            ExifInterface exifData = null;
            try {
                exifData = new ExifInterface(image.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(exifData != null) {
                String dateTime = exifData.getAttribute(ExifInterface.TAG_DATETIME);
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd kk:mm:ss");
                try {
                    cal.setTime(dateFormat.parse(dateTime)); //Assume default timezone
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                long photoTime = cal.getTimeInMillis();

                long postOpDeltaTimeMs = photoTime - patientOpTime;

                args.putLong(ReviewPhotosPageFragment.ARG_POST_OP_DELTA_TIME, postOpDeltaTimeMs);

                int orientation = exifData.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                args.putInt(ReviewPhotosPageFragment.ARG_IMAGE_ROTATION, orientation);
            }



            args.putString(ReviewPhotosPageFragment.ARG_IMAGE_PATH, image.getPath());

            frag.setArguments(args);

            return frag;
        }

        @Override
        public int getCount() {
            return mImageFiles.size();
        }
    }
}
