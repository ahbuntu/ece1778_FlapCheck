package ca.utoronto.flapcheck;


import android.app.Activity;
import android.content.Context;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class ReviewThermaFragment extends Fragment {
    private static String TAG = "ReviewThermaFragment";

//    private ReviewTemperatureListAdapter mAdapter;

    public interface ReviewThermaFragmentListener {
        Patient getPatient();
    }

    private ReviewThermaFragmentListener mListenerCallback;

    public ReviewThermaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListenerCallback = (ReviewThermaFragmentListener) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_review_therma, container, false);

        Patient patient = mListenerCallback.getPatient();
        TextView patientHeader = (TextView) view.findViewById(R.id.text_review_therma_patient);
        patientHeader.setText(patient.getPatientName() +
                                "(" + patient.getPatientMRN() + ")");

        DBLoaderMeasurement dbLoaderMeasurement = new DBLoaderMeasurement(getActivity());
        List<MeasurementReading> tempReadings = dbLoaderMeasurement.getTemperaturesForPatient(patient.getPatientId());

        GraphView graphTemp = (GraphView) view.findViewById(R.id.graph_review_therma);
        // construct graph here
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();
        int i = 0;
        for (MeasurementReading mReading : tempReadings) {
            i++;
//                    Log.d(TAG, "Patient ID: " + mReading.getMeas_patientID() + " Temp: " + mReading.getMeas_temperature());
            //assume that temperature is returned in ascending timestamp order
            DataPoint point = new DataPoint(i, mReading.getMeas_temperature());
            series.appendData(point, false, tempReadings.size());
        }
        graphTemp.addSeries(series);
        GridLabelRenderer gridStyler =  graphTemp.getGridLabelRenderer();
        gridStyler.setGridStyle(GridLabelRenderer.GridStyle.NONE);

//        mAdapter = new PatientListAdapter(getActivity(),R.layout.patient_list_item, patients);

        return view;
    }

//    private class ReviewTemperatureListAdapter extends ArrayAdapter<Patient> {
//        private static final String TAG = "PatientEntryAdapter";
//        Context mContext;
//        int layoutResourceId;
//        List<Patient> patients = null;
//
//        /**
//         * constructor for the adapter
//         * @param context - context for which it will be displayed
//         * @param layoutResId - the layout to which the adapter will be bound
//         * @param list - the list of patients
//         */
//        public ReviewTemperatureListAdapter(Context context, int layoutResId, List<Patient> list) {
//            super(context, layoutResId, list);
//            mContext = context;
//            layoutResourceId = layoutResId;
//            patients = list;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            if(convertView==null){
//                // inflate the layout
//                LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
//                convertView = inflater.inflate(layoutResourceId, parent, false);
//            }
//
//            TextView textViewName  = (TextView) convertView.findViewById(R.id.text_PE_arch_name);
//            TextView textViewMRN = (TextView) convertView.findViewById(R.id.text_PE_arch_mrn);
//            TextView textViewOpDateTime  = (TextView) convertView.findViewById(R.id.text_PE_arch_opDateTime);
//
//            // object item based on the position
////        Log.d(TAG, "position of the view to inflate: " + position);
//            if (patients == null) {
//                textViewName.setText("");
//                textViewMRN.setText("");
//                textViewOpDateTime.setText("");
//            } else {
//                Patient item = patients.get(position);
//                textViewName.setText(item.getPatientName());
//                textViewMRN.setText(item.getPatientMRN());
//
//                String formatStyle = "MMM dd, yyyy";
//                SimpleDateFormat dateTimeFormat = new SimpleDateFormat(formatStyle);
//                Calendar cal = new GregorianCalendar();
//                dateTimeFormat.setTimeZone(cal.getTimeZone());
////            edit_opDate.setHint(dateTimeFormat.format(cal.getTime()));
//                textViewOpDateTime.setText(dateTimeFormat.format(cal.getTime()));
//            }
//            return convertView;
//        }
//    }
}
