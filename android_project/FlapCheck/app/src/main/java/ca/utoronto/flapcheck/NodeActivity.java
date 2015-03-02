package ca.utoronto.flapcheck;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class NodeActivity extends ActionBarActivity
                implements NodeThermaFragment.NodeThermaFragmentListener,
                NodeConnectionFragment.NodeConnectionFragmentListener{

    private static final String TAG = NodeActivity.class.getName();

    static final String ARG_NODE_ACTION = "node_action";
    static final String ESTABLISH_CONNECTION = "establish_connection";
    static final String NODE_THERMA = "node_therma";
    static final String NODE_CHROMA = "node_chroma";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_node);

        FragmentManager mFragManager = getSupportFragmentManager();
        FragmentTransaction mFragTransaction = mFragManager.beginTransaction();
        if (savedInstanceState == null) {
            Bundle bundle = getIntent().getExtras();
            String action_type = bundle.getString(ARG_NODE_ACTION);
            switch (action_type) {
                case ESTABLISH_CONNECTION:
                    //TODO: move this establish connection inside as a check prior to measurement
                    NodeConnectionFragment fragConnect = NodeConnectionFragment.findOrCreate(getSupportFragmentManager());
                    mFragTransaction.add(R.id.node_container, fragConnect);
                    break;
                case NODE_THERMA:
                    NodeThermaFragment fragTherma = new NodeThermaFragment();
                    mFragTransaction.add(R.id.node_container, fragTherma);
                    break;
                case NODE_CHROMA:
                    break;
                default:
                    break;
            }
        }
        mFragTransaction.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_node_connection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    //region NODE Fragment listeners

    @Override
    public void onTemperatureRecorded(float tempVal) {
        //TODO: return this information to the MeasurementActivity as a MeasurementReading
        if (tempVal == Constants.TEMP_INVALID_MEAS) {
            //no measurement value returned; return null MeasurementReading
        } else {
            Log.d(TAG, "Temperature value recorded as " + tempVal);
        }
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onNodeConnected() {
        //TODO: need to do a check to see which node measurement needs to be displayed
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.node_container, new NodeThermaFragment())
                //DO NOT add the connection fragment as a backstack navigation
                .commit();
    }
    //endregion
}
