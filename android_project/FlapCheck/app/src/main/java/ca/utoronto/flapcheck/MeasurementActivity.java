package ca.utoronto.flapcheck;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class MeasurementActivity extends FragmentActivity
        implements
        MeasurementInterface.MeasurementFragmentListener,
        DialogSelectPatient.DialogSelectPatientListener,
        PatientEntryNewFragment.PatientNewEntryListener,
        MeasureOverlayFragment.PhotoMissingListener,
        MeasureOverlayFragment.PointMeasurementListener
{
    private static final String TAG = MeasurementActivity.class.getName();
    static final String ARG_MEASUREMENT_TYPE = "measurement_type";

    private long mActivePatientId = Patient.INVALID_ID; //Set by dialog

    private MeasurePhotoFragment mMeasurePhotoFragment = null;
    private MeasureVideoFragment mMeasureVideoFragment = null;
    private MeasureOverlayFragment mMeasureOverlayFragment = null;
    private NodeThermaFragment mNodeThermaFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement);

        if(savedInstanceState == null) {
            Bundle bundle = getIntent().getExtras();
            Fragment frag = null;

            String measurement_type = bundle.getString(ARG_MEASUREMENT_TYPE);

            if(measurement_type.equals(Constants.MEASUREMENT_PHOTO)) {
                mMeasurePhotoFragment = new MeasurePhotoFragment();
                frag = mMeasurePhotoFragment;
            } else if (measurement_type.equals(Constants.MEASUREMENT_PULSE) || measurement_type.equals(Constants.MEASUREMENT_CAP_REFILL)) {
                mMeasureVideoFragment = new MeasureVideoFragment();
                frag = mMeasureVideoFragment;
            } else if (measurement_type.equals(Constants.MEASUREMENT_TEMP) || measurement_type.equals(Constants.MEASUREMENT_COLOUR)) {
                mMeasureOverlayFragment = new MeasureOverlayFragment();
                Bundle overlayBundle = new Bundle();
                overlayBundle.putString(MeasurementActivity.ARG_MEASUREMENT_TYPE, measurement_type);
                mMeasureOverlayFragment.setArguments(overlayBundle);
                frag = mMeasureOverlayFragment;
            }

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.measure_container, frag)
                    .commit();
        }

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_measurement, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();
        if(fm.getBackStackEntryCount() > 0) {
            fm.popBackStack(); //Return to previous fragment
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Callback for when the "Add New Patient" button is selected from the DialogSelectPatient
     */
    @Override
    public void onAddNewPatient() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.measure_container, new PatientEntryNewFragment())
                .addToBackStack(null)
                .commit();
    }

    /**
     * implementation of PatientEntryNewFragment.PatientNewEntryListener.onAddPatientButtonClicked()
     * @param patientId
     */
    @Override
    public void onAddPatientButtonClicked(long patientId) {
        mActivePatientId = patientId;
        //Same as on positive button selection
        onSetActivePatientId(patientId);
        if (mMeasureOverlayFragment == null) {
            //only do this for video, pulse and picture NOT overlay
            getSupportFragmentManager().popBackStack();
        }
    }

    /**
     * implementation of MeasurementInterface.MeasurementListener.requestActivePatientId()
     */
    @Override
    public void requestActivePatientId() {
        DialogSelectPatient frag = new DialogSelectPatient();
        frag.show(getSupportFragmentManager(), null);
//        return mActivePatientId;
    }

    @Override
    public void onDismissSelectPatient() {
        if(mMeasurePhotoFragment != null) {
            //The user didn't want to select a patient so drop the photo
            mMeasurePhotoFragment.removeLastPhoto();
        }
    }

    @Override
    public void onSetActivePatientId(long patientId) {
        mActivePatientId = patientId;

        if(mMeasurePhotoFragment != null) {
            mMeasurePhotoFragment.moveLastPhotoToPatientDirectory(patientId);
        }

        if(mMeasureVideoFragment != null) {
            mMeasureVideoFragment.moveLastVideoToPatientDirectory(patientId);
        }

        if(mMeasureOverlayFragment != null) {
            mMeasureOverlayFragment.setmPatient(mActivePatientId);
        }
    }


    /**
     * implementation of MeasureOverlayFragment.PhotoMissingListener.onPhotoMissing()
     */
    @Override
    public void onPhotoMissing() {
        Log.d(TAG, "onPhotoMissing called");
        mMeasurePhotoFragment = new MeasurePhotoFragment();
        Bundle args = new Bundle();
        args.putString(Constants.ARG_PHOTO_MISSING_REQUESTER, Constants.MEASUREMENT_TEMP);
        args.putLong(Constants.ARG_PHOTO_MISSING_PATIENT, mActivePatientId);
        mMeasurePhotoFragment.setArguments(args);

        getSupportFragmentManager().popBackStack();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.measure_container, mMeasurePhotoFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Constants.ADD_PATIENT_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle results = data.getExtras();
                long addedPatientId = results.getLong(Constants.PATIENT_ENTRY_KEY_ADDED_PATIENT_ID);

                //Same as on positive button selection
                onSetActivePatientId(addedPatientId);
            }
        } else if (requestCode == Constants.RECORD_VIDEO_REQUEST) {
            if(resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "Recorded video " + data.getDataString());
                mMeasureVideoFragment.videoRecorded(data.getData());
            } else {
                Toast.makeText(this, "No video was recorded!", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to record video!");
            }
        } else if (requestCode == Constants.TAKE_PHOTO_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {

            } else {

            }
        }

    }

    @Override
    public void onPointMeasure(String measureTypeNODE, int location_idx) {
        Bundle bundle = new Bundle();
        bundle.putInt(NodeActivity.ARG_NODE_POINT_INDEX, location_idx);
        bundle.putLong(NodeActivity.ARG_NODE_PATIENT_ID, mActivePatientId);

        switch (measureTypeNODE) {
            case Constants.MEASUREMENT_TEMP:
                Intent tempIntent = new Intent(this, NodeActivity.class);
                bundle.putString(NodeActivity.ARG_NODE_ACTION, NodeActivity.NODE_THERMA);
                tempIntent.putExtras(bundle);
                startActivity(tempIntent);
                break;
            case Constants.MEASUREMENT_COLOUR:
                Intent colourIntent = new Intent(this, NodeActivity.class);
                bundle.putString(NodeActivity.ARG_NODE_ACTION, NodeActivity.NODE_CHROMA);
                colourIntent.putExtras(bundle);
                startActivity(colourIntent);
                break;
            default:
                break;
        }
    }
}
