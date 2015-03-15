package ca.utoronto.flapcheck;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class ReviewActivity extends ActionBarActivity implements
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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Patient details go here");

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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public Patient getPatient() {
        DBLoaderPatient dbHelper = new DBLoaderPatient(getApplicationContext());

        return dbHelper.getPatient(mPatientId);
    }


}
