package ca.utoronto.flapcheck;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.variable.framework.node.BaseSensor;
import com.variable.framework.node.NodeDevice;
import com.variable.framework.node.enums.NodeEnums;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;


public class NodeActivity extends ActionBarActivity
                implements NodeConnectionFragment.NodeConnectionFragmentListener,
                MeasurementInterface.MeasurementFragmentListener,
                DialogSelectPatient.DialogSelectPatientListener,
                PatientEntryNewFragment.PatientNewEntryListener{

    private static final String TAG = NodeActivity.class.getName();

    static final String ARG_NODE_ACTION = "node_action";
    static final String NODE_THERMA = "node_therma";
    static final String NODE_CHROMA = "node_chroma";

    private NodeThermaFragment mNodeThermaFragment = null;
//    private NodeChromaFragment mNodeChromaFragment = null;

    private static String waitingAction = null;
    private long mActivePatientId = Patient.INVALID_ID; //Set by dialog

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_node);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        FragmentManager mFragManager = getSupportFragmentManager();
        FragmentTransaction mFragTransaction = mFragManager.beginTransaction();
        if (savedInstanceState == null) {
            Bundle bundle = getIntent().getExtras();
            String action_type = bundle.getString(ARG_NODE_ACTION);
            switch (action_type) {
                case NODE_THERMA:
                    NodeDevice node = ((FlapCheckApplication) getApplication()).getActiveNode();
                    mNodeThermaFragment = new NodeThermaFragment();
                    if(!isNodeConnected(node))
                    {
                        Toast.makeText(this, "No Connected NODE device.", Toast.LENGTH_SHORT).show();
                        //need to display node establish connection fragment
                        waitingAction = NODE_THERMA;
                        NodeConnectionFragment fragConnect = NodeConnectionFragment.findOrCreate(getSupportFragmentManager());
                        mFragTransaction.add(R.id.node_container, fragConnect);
                    } else {
                        if(checkForSensor(node, NodeEnums.ModuleType.THERMA, true)) {
                            mFragTransaction.add(R.id.node_container, mNodeThermaFragment);
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

    //region Callbacks triggered from DialogSelectPatient

    @Override
    public void onDismissSelectPatient() {
        //The user didn't record a measurement, so do nothing
    }

    @Override
    public void onSetActivePatientId(long patientId) {
        mActivePatientId = patientId;
        if(mNodeThermaFragment != null) {

            Calendar cal = new GregorianCalendar();
            cal.setTimeZone(TimeZone.getTimeZone("UTC"));
            MeasurementReading reading = new MeasurementReading(patientId, cal.getTimeInMillis(),
                    mNodeThermaFragment.getRecordedTemperature(), "", "", "");

            // ok to do this synchronously because we want the user to be blocked if the measurement cannot be saved
            DBLoaderMeasurement measDbHelper = new DBLoaderMeasurement(this);
            long recordID = measDbHelper.addReading(reading);

            if (recordID == -1) {
                Toast.makeText(this, "Error while saving measurement", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Measurement saved", Toast.LENGTH_SHORT).show();
            }

        }
    }

    /**
     * Callback for when the "Add New Patient" button is selected from the DialogSelectPatient
     */
    @Override
    public void onAddNewPatient() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.node_container, new PatientEntryNewFragment())
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
        getSupportFragmentManager().popBackStack();
    }

    //endregion

    //region Callbacks triggered from NODE fragments

    /**
     * Callback for when any kind of measurement is taken.
     */
    @Override
    public void requestActivePatientId() {
        DialogSelectPatient frag = new DialogSelectPatient();
        frag.show(getSupportFragmentManager(), null);
    }


    /**
     * Callback when a node device is successfully connected and ready to be used
     */
    @Override
    public void onNodeConnected() {
        switch (waitingAction) {
            case NODE_THERMA:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.node_container, mNodeThermaFragment)
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
