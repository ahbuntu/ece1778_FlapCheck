/* See http://variableinc.com/terms-use-license for the full license governing this code. */
package ca.utoronto.flapcheck;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.variable.framework.node.ChromaDevice;
import com.variable.framework.node.NodeDevice;
import com.variable.framework.node.enums.NodeEnums;

import java.text.DecimalFormat;

/**
* Created by Corey Mann on 8/28/13.
*/
public class NodeChromaFragment extends NodeChromaFragmentHelper {


    public static final String TAG = "NodeChromaFragment";
    private final DecimalFormat formatter = new DecimalFormat("###.##");
    private ChromaDevice chroma;
    private MeasurementInterface.MeasurementFragmentListener mMeasureListener = null;
    private boolean colourCaptured = false;

    private String colourRGB = "";
    private String colourLAB = "";
    private String colourHex = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup view, Bundle savedInstanced){
        super.onCreateView(inflater,view, savedInstanced);

        final View rootView = inflater.inflate(R.layout.fragment_node_chroma, null, false);

        rootView.findViewById(R.id.button_capture_chroma).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               chroma.requestChromaReading();
            }
        });

        rootView.findViewById(R.id.button_reset_chroma).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetWidgets();
            }
        });

        rootView.findViewById(R.id.button_cancel_chroma).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                colourCaptured = false;
                getActivity().finish();
            }
        });

        rootView.findViewById(R.id.button_done_chroma).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (colourCaptured) {
                    mMeasureListener = (MeasurementInterface.MeasurementFragmentListener) getActivity();
                    mMeasureListener.requestActivePatientId();
                } else {
                    Toast.makeText(getActivity(), "Please take a colour measurement.", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

//        rootView.findViewById(R.id.btnCalibrate).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (chroma.getChromaModuleInfo().getModel().equals("1.1"))
//                {
//                    Toast.makeText(getActivity(), "Calibration not available for Chroma Older than 1.1", Toast.LENGTH_LONG).show();
//                    return;
//                }
//                chroma.requestWhitePointCal();
//            }
//        });

        NodeDevice node  = ((FlapCheckApplication)getActivity().getApplication()).getActiveNode();
        chroma = node.findSensor(NodeEnums.ModuleType.CHROMA);

        if(chroma.getChromaModuleInfo().getCalibrationList().size() == 52 && chroma.getChromaModuleInfo().getModel().equals("2.0")){
            new AlertDialog.Builder(getActivity())
                    .setTitle("Warning")
                    .setMessage("Your chroma needs to be returned to Variable inc for recall. Please contact us. Chroma is not ensured to work properly until the recall has been satisfied.")
                    .setPositiveButton("OK", null)
                    .show();
        }

        return rootView;
    }

    @Override
    public void onColorUpdate(int color){
        super.onColorUpdate(color);
        getView().findViewById(R.id.imgScanColor).setBackgroundColor(color);
        colourCaptured = true;
    }

    @Override
    public void onRGBUpdate(float r, float g, float b){
        super.onRGBUpdate(r, g, b);
        String text = formatter.format(r) + " , " + formatter.format(g) + " , " + formatter.format(b);
        ((TextView) getView().findViewById(R.id.txtRGB)).setText(text);
        colourRGB = text;
    }

    @Override
    public void onLABUpdate(double l, double a, double b){
        super.onLABUpdate(l, a, b);
        String text = formatter.format(l) + " , " + formatter.format(a) + " , " + formatter.format(b);
        ((TextView) getView().findViewById(R.id.txtLab)).setText(text);
        colourLAB = text;
    }

    @Override
    public void onHexValue(String hex){
        super.onHexValue(hex);
        ((TextView) getView().findViewById(R.id.txtHex)).setText(hex);
        colourHex = hex;
    }

    private void resetWidgets() {
        getView().findViewById(R.id.imgScanColor).setBackgroundColor(getResources().getColor(R.color.fc_dark_gray));
        colourCaptured = false;
        ((TextView) getView().findViewById(R.id.txtRGB)).setText("");
        ((TextView) getView().findViewById(R.id.txtLab)).setText("");
        ((TextView) getView().findViewById(R.id.txtHex)).setText("");
    }

    /**
     * returns the recorded colour in RGB
     * @return
     */
    public String getRecordedColourRGB() {
        return  colourRGB;
    }

    /**
     * returns the recorded colour in LAB
     * @return
     */
    public String getRecordedColourLAB() {
        return  colourLAB;
    }

    /**
     * returns the recorded colour in HEX, if captured
     * @return
     */
    public String getRecordedColourHex() {
        return  colourHex;
    }
}
