package ca.utoronto.flapcheck;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;


public class ReviewActivity extends FragmentActivity implements
        ReviewPhotosFragment.ReviewPhotoFragmentListener,
        ReviewVideoFragment.ReviewVideoFragmentListener,
        ReviewThermaFragment.ReviewThermaFragmentListener
{
    public static String ARG_MEASUREMENT_TYPE = "measurement_type";
    public static String ARG_PATIENT_ID = "patient_id";

    private long mPatientId;
    private String mMeasurementType;

    private ReviewPhotosFragment mReviewPhotosFragment;
    private ReviewVideoFragment mReviewVideoFragment;
    private ReviewThermaFragment mReviewThermaFragment;
//    private mReviewChromaFragment mReviewChromaFragment;

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
            } else if (mMeasurementType.equals(Constants.MEASUREMENT_TEMP)) {
                mReviewThermaFragment = new ReviewThermaFragment();
                frag = mReviewThermaFragment;
            } else if (mMeasurementType.equals(Constants.MEASUREMENT_COLOUR)) {
                //TODO: launch chroma fragment
//                mReviewChromaFragment = new ReviewChromaFragment();
//                frag = mReviewChromaFragment;
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
