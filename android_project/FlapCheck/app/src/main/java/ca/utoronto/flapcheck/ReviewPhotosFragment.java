package ca.utoronto.flapcheck;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReviewPhotosFragment extends Fragment {
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

        mPager1 = (ViewPager) view.findViewById(R.id.image_pager_1);
        mPager2 = (ViewPager) view.findViewById(R.id.image_pager_2);

        mPagerAdapter = new PhotoPagerAdapter(getActivity().getSupportFragmentManager());

        mPager1.setAdapter(mPagerAdapter);
        mPager2.setAdapter(mPagerAdapter);

        return view;
    }

    private class PhotoPagerAdapter extends FragmentPagerAdapter {
        private List<File> mImageFiles;

        public PhotoPagerAdapter(FragmentManager fm) {
            super(fm);

            mImageFiles = new ArrayList<File>();

            Patient patient = mListenerCallback.getPatient();
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

            args.putFloat(ReviewPhotosPageFragment.ARG_HOURS_POST_OP, 0.5f);
            args.putString(ReviewPhotosPageFragment.ARG_IMAGE_PATH, mImageFiles.get(position).getPath());

            frag.setArguments(args);

            return frag;
        }

        @Override
        public int getCount() {
            return mImageFiles.size();
        }
    }
}
