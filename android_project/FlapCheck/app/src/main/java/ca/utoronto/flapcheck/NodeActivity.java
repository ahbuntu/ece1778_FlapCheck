package ca.utoronto.flapcheck;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.variable.framework.node.BaseSensor;
import com.variable.framework.node.NodeDevice;
import com.variable.framework.node.enums.NodeEnums;


public class NodeActivity extends ActionBarActivity
                implements NodeThermaFragment.NodeThermaFragmentListener,
                NodeConnectionFragment.NodeConnectionFragmentListener{

    private static final String TAG = NodeActivity.class.getName();

    static final String ARG_NODE_ACTION = "node_action";
    static final String NODE_THERMA = "node_therma";
    static final String NODE_CHROMA = "node_chroma";

    private static String waitingAction = null;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_node);

        FragmentManager mFragManager = getSupportFragmentManager();
        FragmentTransaction mFragTransaction = mFragManager.beginTransaction();
        if (savedInstanceState == null) {
            Bundle bundle = getIntent().getExtras();
            String action_type = bundle.getString(ARG_NODE_ACTION);
            switch (action_type) {
                case NODE_THERMA:
                    NodeDevice node = ((FlapCheckApplication) getApplication()).getActiveNode();
                    if(!isNodeConnected(node))
                    {
                        Toast.makeText(this, "No Connected NODE device.", Toast.LENGTH_SHORT).show();
                        //need to display node establish connection fragment
                        waitingAction = NODE_THERMA;
                        NodeConnectionFragment fragConnect = NodeConnectionFragment.findOrCreate(getSupportFragmentManager());
                        mFragTransaction.add(R.id.node_container, fragConnect);
                    } else {
                        if(checkForSensor(node, NodeEnums.ModuleType.THERMA, true)) {
                            mFragTransaction.add(R.id.node_container, new NodeThermaFragment());
                        }
                    }
                    break;
                case NODE_CHROMA:
//                    NodeChromaFragment fragChroma = new NodeChromaFragment();
//                    mFragTransaction.add(R.id.node_container, fragChroma);
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
        switch (waitingAction) {
            case NODE_THERMA:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.node_container, new NodeThermaFragment())
                                //DO NOT add the connection fragment as a backstack navigation
                        .commit();
                break;
            case NODE_CHROMA:
//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.node_container, new NodeChromaFragment())
//                                //DO NOT add the connection fragment as a backstack navigation
//                        .commit();
                break;
            default:
                getSupportFragmentManager().popBackStack();
                break;
        }
    }
    //endregion


    /**
     * Determines if the node is connected. Null is permitted.
     * @param node
     * @return
     */
    private boolean isNodeConnected(NodeDevice node) { return node != null && node.isConnected(); }

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
            Toast.makeText(this, type.toString() + " not found on " + node.getName(), Toast.LENGTH_SHORT).show();
        }
        return sensor != null;
    }
}
