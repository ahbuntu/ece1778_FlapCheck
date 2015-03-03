package ca.utoronto.flapcheck;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.variable.framework.node.BaseSensor;
import com.variable.framework.node.NodeDevice;
import com.variable.framework.node.enums.NodeEnums;


public class MainActivity extends FragmentActivity
            implements MainFragment.MainFragmentListener,
        MainMeasurementFragment.MainMeasurementSensorListener,
        MainMeasurementFragment.MainMeasurementNODEListener
{
    MainPagerAdapter mViewPagerAdapter;
    ViewPager mViewPager;
    private static final String TAG = "MainActivtiy";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState == null) {
            mViewPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
            mViewPager = (ViewPager) findViewById(R.id.main_pager);
            mViewPager.setAdapter(mViewPagerAdapter);
        }

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    public void startMeasurementActivity() {
        Intent intent = new Intent(this, MeasurementActivity.class);
        startActivity(intent);
    }

    @Override
    public void startPatientEntryActivity() {
        Intent intent = new Intent(this, PatientEntryActivity.class);
        startActivity(intent);
    }

    /**
     * implementation of  MainMeasurementFragment.MainMeasurementSensorListener.onMeasurePhoto()
     */
    @Override
    public void onMeasurePhoto() {
        Intent intent = new Intent(this, MeasurementActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(MeasurementActivity.ARG_MEASUREMENT_TYPE, Constants.MEASUREMENT_PHOTO);
        intent.putExtras(bundle);
        startActivity(intent);
    }


    /**
     * implementation of  MainMeasurementFragment.MainMeasurementSensorListener.onMeasureTemperature()
     */
    @Override
    public void onMeasureTemperature() {
        Intent intent = new Intent(this, NodeActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(NodeActivity.ARG_NODE_ACTION, NodeActivity.NODE_THERMA);
        intent.putExtras(bundle);
        startActivity(intent);
    }



    /**
     *
     * @return
     */
    private boolean getOrCreateNodeConnection() {
        boolean btStatus = ensureBluetoothIsOn();
        Log.d(TAG, "bluetoothON - " + Boolean.toString(btStatus));
        return  btStatus;
    }

    /**
     * Invokes a new intent to request to start the bluetooth, if not already on.
     */
    private boolean ensureBluetoothIsOn(){
        if(!BluetoothAdapter.getDefaultAdapter().isEnabled()){
            Intent btIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            btIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //requestCode (200) - this code will be returned in onActivityResult() when the activity exits.
            startActivityForResult(btIntent, 200);
            return false;
        }
        return true;
    }



    //region FragmentPagerAdapter implementation

    public class MainPagerAdapter extends FragmentPagerAdapter {

        public MainPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment frag = null;
            if(position == 0) {
                frag = new MainMeasurementFragment();
            } else if (position == 1) {
                frag = new MainReviewFragment();
            }
            else if (position == 2) {
                frag = new MainFragment();
            }
            return frag;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title = null;
            if(position == 0) {
                title = "Measure";
            } else if (position == 1) {
                title = "Review";
            }
            return title;
        }
    }
    //endregion
}
