package ca.utoronto.flapcheck;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
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
            implements SplashScreenFragment.SplashScreenFragmentListener,
                       MainFragment.MainFragmentListener,
                       MeasurementFragment.MeasurementFragmentListener
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
    public void exitSplashScreen() {

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

    @Override
    public void onMeasurePhoto() {
        Intent intent = new Intent(this, MeasurementActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(MeasurementActivity.ARG_MEASUREMENT_TYPE, MeasurementActivity.PHOTO_MEASUREMENT);
        intent.putExtras(bundle);
        startActivity(intent);
    }


    @Override
    public void onMeasureTemperature() {
        NodeDevice node = ((FlapCheckApplication) getApplication()).getActiveNode();

        if(!isNodeConnected(node))
        {
            //TODO: need to determine when/where the check for connected node devices should take place
            Toast.makeText(this, "No Connected NODE device.", Toast.LENGTH_SHORT).show();
            //need to display dialog to pick paired node
            Intent intent = new Intent(this, NodeActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(NodeActivity.ARG_NODE_ACTION, NodeActivity.ESTABLISH_CONNECTION);
            intent.putExtras(bundle);
            startActivity(intent);
        } else {
            //TODO: determine if the checkforsensor should take place here
            if(checkForSensor(node, NodeEnums.ModuleType.THERMA, true)) {
                Intent intent = new Intent(this, NodeActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(NodeActivity.ARG_NODE_ACTION, NodeActivity.NODE_THERMA);
                intent.putExtras(bundle);
                startActivity(intent);
//                animateToFragment(new ThermaFragment(), ThermaFragment.TAG);
            }
        }
    }


    /**
     * Determines if the node is connected. Null is permitted.
     * @param node
     * @return
     */
    private boolean isNodeConnected(NodeDevice node) { return node != null && node.isConnected(); }

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

    /**
     * Checks for a specific sensor on a node.
     * @param node - the node
     * @param type - the module type to check for on the node parameter.
     * @param displayIfNotFound - allows toasting a message if module is not found on node.
     * @return true, if the node contains the module
     */
    private boolean checkForSensor(NodeDevice node, NodeEnums.ModuleType type, boolean displayIfNotFound){
        BaseSensor sensor = node.findSensor(type);
        if(sensor == null && displayIfNotFound){
            Toast.makeText(MainActivity.this, type.toString() + " not found on " + node.getName(), Toast.LENGTH_SHORT).show();
        }
        return sensor != null;
    }


    public class MainPagerAdapter extends FragmentPagerAdapter {

        public MainPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment frag = null;
            if(position == 0) {
                frag = new SplashScreenFragment();
            } else if (position == 1) {
                frag = new MeasurementFragment();
            } else if (position == 2) {
                frag = new ReviewFragment();
            } else if (position == 3) {
                frag = new MainFragment();
            }
            return frag;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title = null;
            if(position == 0) {
                title = "Splash";
            } else if (position == 1) {
                title = "Measure";
            } else if (position == 2) {
                title = "Review";
            } else if (position == 3) {
                title = "Old main fragment";
            }
            return title;
        }
    }
}
