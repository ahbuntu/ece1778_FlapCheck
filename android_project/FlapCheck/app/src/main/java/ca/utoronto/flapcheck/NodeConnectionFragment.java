package ca.utoronto.flapcheck;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.variable.framework.android.bluetooth.BluetoothBroadcastReceiver;
import com.variable.framework.android.bluetooth.BluetoothService;
import com.variable.framework.dispatcher.DefaultNotifier;
import com.variable.framework.node.AndroidNodeDevice;
import com.variable.framework.node.NodeDevice;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class NodeConnectionFragment extends Fragment implements
        NodeDevice.ConnectionListener,
        BluetoothBroadcastReceiver.DiscoveryStartedListener,
        BluetoothBroadcastReceiver.DiscoveryCompletedListener{


    public interface NodeConnectionFragmentListener {
        public void onNodeConnected();
    }
    private NodeConnectionFragmentListener mListener;

    public static final int REQUEST_ENABLE_BLUETOOTH = 2;
    public static final String FRAGMENT_TAG = NodeConnectionFragment.class.getName();
    private static final int REQUEST_USER_BLUETOOTH_PAIRING = 2000;


    public static NodeConnectionFragment findOrCreate(FragmentManager manager){
        Fragment f = manager.findFragmentByTag(FRAGMENT_TAG);
        if(f == null){
            NodeConnectionFragment dialog = new NodeConnectionFragment();
            DefaultNotifier.instance().addConnectionListener(dialog);
            return dialog;
        }else{
            return (NodeConnectionFragment) f;
        }
    }


    ListView nodeDeviceList;
    NodeDeviceAdapter mNodeDeviceAdapter;
    MenuItem action_scan_nodes;
    ProgressDialog mConnectionDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        setHasOptionsMenu(true);
        getActivity().setTitle("NODE Connect");

        View root = inflater.inflate(R.layout.fragment_node_connection_dialog, container, false);
        nodeDeviceList = (ListView) root.findViewById(R.id.listView);
        nodeDeviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NodeDevice node = ((NodeDeviceAdapter) parent.getAdapter()).getItem(position);

                if(ensureBluetoothIsOn()) {
                    mNodeDeviceAdapter.add(node);
                    changeNODEState(node);
                }
            }
        });

        mNodeDeviceAdapter = new NodeDeviceAdapter(getActivity());
        nodeDeviceList.setAdapter(mNodeDeviceAdapter);

        //Add all pre-existing connected nodes.
        List<AndroidNodeDevice> nodes = AndroidNodeDevice.getManager().findByConnectionStatus(true);
        for(AndroidNodeDevice node : nodes){
            mNodeDeviceAdapter.add(node);
        }
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(mNodeDeviceAdapter.getCount() == 0){
            startBluetoothDiscovery();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.scan_nodes, menu);
        action_scan_nodes = menu.findItem(R.id.action_scan_nodes);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_scan_nodes:
                startBluetoothDiscovery();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //region Click Handlers
    protected  void startBluetoothDiscovery(){
        mNodeDeviceAdapter.clear();
        BluetoothAdapter.getDefaultAdapter().startDiscovery();
    }

    protected void changeNODEState(NodeDevice node){
        List<AndroidNodeDevice> nodes = AndroidNodeDevice.getManager().findByConnectionStatus(true);
        for(AndroidNodeDevice n : nodes){ if(!n.equals(node)) n.disconnect(); }

        if(node.isConnected()){
            node.disconnect();
        }else {
            node.connect();
        }
    }

    private void showPairingInstructions(final NodeDevice node) {
        Dialog mExtraDialog = new AlertDialog.Builder(getActivity())
                .setTitle(node.getName() + " Needs Pairing")
                .setMessage("There was an attempt to automatically pair you device that failed. Please use settings to pair with your NODE")
                .setPositiveButton("Go To Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent bluetoothSettingsIntent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                        startActivityForResult(bluetoothSettingsIntent, REQUEST_USER_BLUETOOTH_PAIRING);
                    }
                }).setNegativeButton("Choose Different Device", null)
                .create();

        try {
            mExtraDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //endregion

    //region Lifecycle Callbacks


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Register for the Bluetooth Discovery Start and End
        BluetoothBroadcastReceiver.instance().setOnDiscoveryStartedListener(this);
        BluetoothBroadcastReceiver.instance().setOnDiscoveryCompletedListener(this);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (NodeConnectionFragmentListener) activity;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        //Stop Listening for the Bluetooth Discovery Start and End Messages.
        BluetoothBroadcastReceiver.instance().setOnDiscoveryStartedListener(null);
        BluetoothBroadcastReceiver.instance().setOnDiscoveryCompletedListener(null);

    }
    //endregion

    /**
     * Updates the Dialog. If not already presently visible it will create one.
     * @param message
     */
    public void updateDialog(String message){
        if(mConnectionDialog == null){
            mConnectionDialog = new ProgressDialog(getActivity());
            mConnectionDialog.setTitle("Bluetooth Connection");
            mConnectionDialog.setCancelable(false);
            mConnectionDialog.setIndeterminate(true);
            mConnectionDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Stop all Connections and attempts.
                            BluetoothService.killConnections();
                        }
                    });
        }

        mConnectionDialog.setMessage(message);

        if(mConnectionDialog.isShowing() == false){
            mConnectionDialog.show();
        }
    }

    private void dismissDialog() {
        if (mConnectionDialog != null) {
            mConnectionDialog.dismiss();
        }
    }

    /**
     * @return true, if bluetooth is already on.
     */
    protected boolean ensureBluetoothIsOn() {
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            Intent btIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            btIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivityForResult(btIntent, REQUEST_ENABLE_BLUETOOTH);
            return false;
        }

        return true;
    }

    //region Bluetooth Scanning


    @Override
    public void onDiscoveryCompleted() {
        if(isAdded() && action_scan_nodes != null) {
            action_scan_nodes.setEnabled(true);
        }
    }

    @Override
    public void onDiscoveryStarted() {
        if(isAdded() && action_scan_nodes  != null){
            action_scan_nodes.setEnabled(false);
        }
    }

    //endregion

    //region Bluetooth Callbacks

    @Override
    public void onDisconnect(NodeDevice node) {
        FlapCheckApplication.setActiveNode(null); //Clear out NODE
        Toast.makeText(FlapCheckApplication.getContext(), "Disconnected from " + node.getName(), Toast.LENGTH_SHORT).show();
        nodeDeviceList.invalidateViews();
    }

    @Override
    public void onConnectionFailed(NodeDevice nodeDevice, Exception e) {
        Log.d(FRAGMENT_TAG, "=======onConnectionFailed=======");
        nodeDeviceList.setVisibility(View.VISIBLE);
        Toast.makeText(FlapCheckApplication.getContext(), "Connection Failed to " + nodeDevice.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNodeDiscovered(NodeDevice nodeDevice) {
        if (isAdded()) {
            mNodeDeviceAdapter.add(nodeDevice);
        }
    }

    @Override
    public void onPairingFailure(NodeDevice device) {
        if(isAdded()) {
            showPairingInstructions(device);
        }
    }

    @Override
    public void nodeDeviceFailedToInit(NodeDevice nodeDevice) {
        Log.d(FRAGMENT_TAG, "=======nodeDeviceFailedToInit=======");
        Toast.makeText(getActivity(), "Failed to Initialize", Toast.LENGTH_SHORT).show();
        dismissDialog();
    }

    @Override
    public void onInitializationUpdate(String s) {
       updateDialog(s);
    }

    @Override
    public void onConnecting(NodeDevice node) {
        Log.d(FRAGMENT_TAG, "=======onConnecting=======");
        FlapCheckApplication.setActiveNode(node);
        updateDialog("Connecting to " + node.getName());
    }

    /**
     * Invoked when the Node device is ready for communication
     * @param nodeDevice
     */
    @Override
    public void onCommunicationInitCompleted(NodeDevice nodeDevice) {
        Log.d(FRAGMENT_TAG, "=======onCommunicationInitCompleted=======");
        this.mNodeDeviceAdapter.notifyDataSetChanged();
        Toast.makeText(getActivity(), nodeDevice.getName() + " is ready to use", Toast.LENGTH_SHORT).show();
        dismissDialog();

        mListener.onNodeConnected();
    }

    /**
     * Invoked when a connection attempt is in progress, and a communication channel has been established.
     * @param node
     */
    public void onConnected(NodeDevice node) {
        Log.d(FRAGMENT_TAG, "=======onConnected=======");
        updateDialog("Initializing " + node.getName());
    }

    //endregion

    public class NodeDeviceAdapter extends BaseAdapter {
        private final List<NodeDevice> nodeDevices = new LinkedList<NodeDevice>();
        private final Comparator<NodeDevice> mNodeComparator;

        Context context;

        public NodeDeviceAdapter(Context context){
            this.context = context;
            this.mNodeComparator = new Comparator<NodeDevice>() {
                @Override
                public int compare(NodeDevice lhs, NodeDevice rhs) {
                    if(lhs.isConnected() && rhs.isConnected() == false){
                        return -1;
                    }else if(lhs.isConnected() == false && rhs.isConnected() == true){
                        return 1;
                    }else {
                        //Both Connected
                        if (lhs.getName() != null && rhs.getName() != null) {
                            return lhs.getName().compareTo(rhs.getName());
                        } else if (lhs.getName() == null) {
                            return 1;
                        } else {
                            return -1;
                        }
                    }
                }
            };
        }


        public void add(NodeDevice node){
            if(nodeDevices.contains(node) != true) {
                nodeDevices.add(node);
                notifyDataSetChanged();
            }
        }

        public void clear(){
            nodeDevices.clear();
            notifyDataSetChanged();
        }

        @Override
        public void notifyDataSetChanged() {
            Collections.sort(this.nodeDevices, this.mNodeComparator);
            super.notifyDataSetChanged();
        }

        @Override
        public int getCount() { return nodeDevices.size(); }

        @Override
        public NodeDevice getItem(int position) {return nodeDevices.get(position);  }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            NodeDevice node = getItem(position);

            if(convertView == null){
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.node_list_item, parent, false);
            }

            ((TextView) convertView.findViewById(R.id.txtName)).setText(node.getName());
            String descr = buildDescriptionAndSetBackground(convertView, node);
            ((TextView) convertView.findViewById(R.id.txtDescription)).setText(descr);

            return convertView;
        }

        private String buildDescriptionAndSetBackground(View convertView, NodeDevice node) {
            StringBuilder description = new StringBuilder();
            if(node.isConnected() == true){
                description.append(node.getModel());
                description.append(" - ");
                description.append(node.getFirmwareVersion());

                if(node.getBatteryLevel() != -1) {
                    description.append(" - ");
                    description.append(node.getBatteryLevel());
                }
                convertView.setBackground(getResources().getDrawable(R.drawable.node_connect_background));
            }
            else{
                description.append(node.getAddress());
                convertView.setBackground(null);
            }
            return description.toString();
        }
    }
}