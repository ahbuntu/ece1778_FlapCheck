package ca.utoronto.flapcheck;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;


public class ReviewActivity extends FragmentActivity implements
        ReviewPhotosFragment.ReviewPhotoFragmentListener,
        ReviewVideoFragment.ReviewVideoFragmentListener
{
    public static String ARG_MEASUREMENT_TYPE = "measurement_type";
    public static String ARG_PATIENT_ID = "patient_id";

    private long mPatientId;
    private String mMeasurementType;

    private ReviewPhotosFragment mReviewPhotosFragment;
    private ReviewVideoFragment mReviewVideoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);



        if(savedInstanceState == null) {
            Bundle bundle = getIntent().getExtras();
            mPatientId = bundle.getLong(ARG_PATIENT_ID);
            mMeasurementType = bundle.getString(ARG_MEASUREMENT_TYPE);

            Fragment frag = null;

            if(mMeasurementType.equals(Constants.MEASUREMENT_PHOTO)) {
                mReviewPhotosFragment = new ReviewPhotosFragment();
                frag = mReviewPhotosFragment;
            } else if (mMeasurementType.equals(Constants.MEASUREMENT_CAP_REFILL)) {
                mReviewVideoFragment = new ReviewVideoFragment();
                frag = mReviewVideoFragment;
            } else if (mMeasurementType.equals(Constants.MEASUREMENT_PULSE)) {
                mReviewVideoFragment = new ReviewVideoFragment();
                frag = mReviewVideoFragment;
            }

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.review_container, frag)
                    .commit();
        }
    }

    @Override
    public Patient getPatient() {
        DBLoaderPatient dbHelper = new DBLoaderPatient(getApplicationContext());

        return dbHelper.getPatient(mPatientId);
    }


}
