package ca.utoronto.flapcheck;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

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

    private ReviewTemperatureListAdapter mAdapter;

    public interface ReviewThermaFragmentListener {
        Patient getPatient();
    }

    private ReviewThermaFragmentListener mListenerCallback;

    public ReviewThermaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        setHasOptionsMenu(true);

        Patient patient = mListenerCallback.getPatient();
        getActivity().setTitle(patient.getPatientName() + " " + "(" + patient.getPatientMRN() + ")");

        DBLoaderMeasurement dbLoaderMeasurement = new DBLoaderMeasurement(getActivity());
        List<MeasurementReading> tempReadings = dbLoaderMeasurement.getTemperaturesForPatient(patient.getPatientId());

        ProgressBar spinner = (ProgressBar) view.findViewById(R.id.progress_review_temp);
        GraphView graphTemp = (GraphView) view.findViewById(R.id.graph_review_therma);
        // construct graph here
        LineGraphSeries<DataPoint> lineSeries = new LineGraphSeries<DataPoint>();
        PointsGraphSeries<DataPoint> pointSeries = new PointsGraphSeries<DataPoint>();
        int i = 0;
        for (MeasurementReading mReading : tempReadings) {
            i++;
//                    Log.d(TAG, "Patient ID: " + mReading.getMeas_patientID() + " Temp: " + mReading.getMeas_temperature());
            //assume that temperature is returned in ascending timestamp order
            DataPoint point = new DataPoint(i, mReading.getMeas_temperature());
            lineSeries.appendData(point, false, tempReadings.size());
            pointSeries.appendData(point, false, tempReadings.size());
        }
        graphTemp.addSeries(lineSeries);
        graphTemp.addSeries(pointSeries);
        graphTemp.setTitleColor(getResources().getColor(R.color.fc_dark_gray));
        graphTemp.setTitleTextSize(64);
        GridLabelRenderer gridStyler =  graphTemp.getGridLabelRenderer();
        gridStyler.setGridStyle(GridLabelRenderer.GridStyle.BOTH);
        spinner.setVisibility(View.GONE);

        mAdapter = new ReviewTemperatureListAdapter(getActivity(),R.layout.review_therma_list_item, tempReadings);
        AbsListView mListView = (AbsListView) view.findViewById(R.id.list_review_therma);
        mListView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_review, menu);
        MenuItem item = menu.findItem(R.id.action_logo);
        item.setIcon(R.drawable.ic_temperature);

        super.onCreateOptionsMenu(menu,inflater);
    }

    private class ReviewTemperatureListAdapter extends BaseAdapter {
        private static final String TAG = "ReviewTempListAdapter";
        Context mContext;
        int layoutResourceId;
        List<MeasurementReading> readingsList = null;

        /**
         * constructor for the adapter
         * @param context - context for which it will be displayed
         * @param layoutResId - the layout to which the adapter will be bound
         */
        public ReviewTemperatureListAdapter(Context context, int layoutResId, List<MeasurementReading> readings) {
//            super(context, layoutResId);
            mContext = context;
            layoutResourceId = layoutResId;
            readingsList = readings;
        }

        @Override
        public int getCount () {
            return readingsList.size();
        }

        @Override
        public long getItemId (int position) {
            return position;
        }

        @Override
        public Object getItem (int position) {
            return readingsList.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView==null){
                // inflate the layout
                LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                convertView = inflater.inflate(layoutResourceId, parent, false);
            }

            TextView textViewDate = (TextView) convertView.findViewById(R.id.text_review_therma_list_date);
            TextView textViewTime = (TextView) convertView.findViewById(R.id.text_review_therma_list_time);
            TextView textViewTemp = (TextView) convertView.findViewById(R.id.text_review_therma_list_temp);

            // object item based on the position
//            Log.d(TAG, "pos to inflate: " + position);

            if (readingsList == null) {
                textViewDate.setText("");
                textViewTime.setText("");
                textViewTemp.setText("");
            } else {
                MeasurementReading mReading = readingsList.get(position);
                textViewDate.setText(Utils.prettyDate(mReading.getMeas_timestamp()));
                textViewTime.setText(Utils.prettyTime(mReading.getMeas_timestamp()));
                textViewTemp.setText(Utils.prettyTempCelsius(mReading.getMeas_temperature()));
            }
            return convertView;
        }
    }
}
