package ca.utoronto.flapcheck;


import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
public class ReviewThermaFragment extends Fragment implements
        RegionSelectImageView.TapListener

{
    private static String TAG = "ReviewThermaFragment";

    private ReviewTemperatureListAdapter mAdapter;
    private Patient mPatient;

    private RegionSelectImageView mRegionImage;
    private List<MeasurementReading> mTempReadings;
    private List<MeasurementReading> mListTempReadings = new ArrayList<>();
    private AbsListView mListView;
    private GraphView mGraphTemp;

    private int mDefaultRegionIdx = 0;

    TextView textHeadingDate;
    TextView textHeadingTime;
    TextView textHeadingTemp;
    TextView textThermaStatus;

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

    private void populateListTempReadings(){
        //the first two are always null
        if (mListTempReadings.size() > 0) {
            mListTempReadings.clear();
        }
            mListTempReadings.add(null);
            mListTempReadings.add(null);
            for (MeasurementReading reading : mTempReadings) {
                mListTempReadings.add(reading);
            }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_review_therma, container, false);

        setHasOptionsMenu(true);

        mPatient = mListenerCallback.getPatient();
        getActivity().setTitle(mPatient.getPatientName() + " " + "(" + mPatient.getPatientMRN() + ")");

        DBLoaderMeasurement dbLoaderMeasurement = new DBLoaderMeasurement(getActivity());
        mTempReadings = dbLoaderMeasurement.getTemperaturesForPatientAtIndex(mPatient.getPatientId(), mDefaultRegionIdx);

        /*
         * The region image is stateful, and must not be re-created or else selection is screwed up!
         */
        mRegionImage = (RegionSelectImageView) inflater.inflate(R.layout.review_therma_image_item, container, false);
        mRegionImage.setTapListener(ReviewThermaFragment.this);

        //Load the correct image
        File pictureDir = new File(mPatient.getPatientPhotoPath());

        //Fill the paths into a list
        File[] imageFiles = pictureDir.listFiles();
        if(imageFiles.length > 0) {
            mRegionImage.setImageURI(Uri.fromFile(imageFiles[0]));
        }

        //Add the measurement points to the overlay
        DBLoaderPointToMeasure dbPointsLoader = new DBLoaderPointToMeasure(getActivity());
        final List<PointToMeasure> pointsOverlayList =  dbPointsLoader.getPointsToMeasureForPatient(mPatient.getPatientId());
        final List<PointFloat> pointsToDraw = new ArrayList<PointFloat>();

        for (PointToMeasure pointOverlay : pointsOverlayList) {
            PointFloat p = new PointFloat(pointOverlay.getPointX(), pointOverlay.getPointY());

            pointsToDraw.add(p);
            //pointList is the location of the regions of interest on the image
            //A circle is drawn at each point in the list, which can then be selected by tapping
            mRegionImage.setPointList(pointsToDraw);
        }
        mRegionImage.addSelection(mDefaultRegionIdx);

        /*
         * Load the temperatures
         */
        populateListTempReadings();

        mAdapter = new ReviewTemperatureListAdapter(getActivity(),R.layout.review_therma_list_item, mListTempReadings);
        mListView = (AbsListView) view.findViewById(R.id.list_review_therma);
        mListView.setAdapter(mAdapter);

        return view;
    }

    private void redraw_graph() {
        if(mGraphTemp == null) {
            return; //Nothing to do
        }
        if(mTempReadings.size() > 0 ) {
            textThermaStatus.setVisibility(View.GONE);
            mGraphTemp.setVisibility(View.VISIBLE);
            textHeadingDate.setVisibility(View.VISIBLE);
            textHeadingTime.setVisibility(View.VISIBLE);
            textHeadingTemp.setVisibility(View.VISIBLE);

            //Reset the graph
            mGraphTemp.removeAllSeries();

            //Draw the graph
            LineGraphSeries<DataPoint> lineSeries = new LineGraphSeries<DataPoint>();
            PointsGraphSeries<DataPoint> pointSeries = new PointsGraphSeries<DataPoint>();
            int i = 1;
            for (MeasurementReading mReading : mTempReadings) {
                Log.d(TAG, "Patient ID: " + mReading.getMeas_patientID() + "MeasureIdx:" + i + " Temp: " + mReading.getMeas_temperature());
                //assume that temperature is returned in ascending timestamp order
                DataPoint point = new DataPoint(i, mReading.getMeas_temperature());
                lineSeries.appendData(point, false, mTempReadings.size());
                pointSeries.appendData(point, false, mTempReadings.size());
                i++;
            }
            mGraphTemp.addSeries(lineSeries);
            mGraphTemp.addSeries(pointSeries);

            //Labels
            mGraphTemp.setTitleColor(getResources().getColor(R.color.fc_dark_gray));
            mGraphTemp.setTitleTextSize(64);
            GridLabelRenderer gridStyler = mGraphTemp.getGridLabelRenderer();
            gridStyler.setGridStyle(GridLabelRenderer.GridStyle.BOTH);
            if(i > 1) {
                mGraphTemp.getViewport().setXAxisBoundsManual(true);
                mGraphTemp.getViewport().setMinX(1);
                mGraphTemp.getViewport().setMaxX(i - 1);
            }

            //sexiness
            mListView.smoothScrollToPosition(1); //display the graph
            mListView.setSelection(1);

        } else {
            textThermaStatus.setVisibility(View.VISIBLE);
            mGraphTemp.setVisibility(View.GONE);
            textHeadingDate.setVisibility(View.GONE);
            textHeadingTime.setVisibility(View.GONE);
            textHeadingTemp.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_review, menu);
        MenuItem item = menu.findItem(R.id.action_logo);
        item.setIcon(R.drawable.ic_temperature_grey);

        super.onCreateOptionsMenu(menu,inflater);
    }

    private void updateSelectedRegion(int region_idx) {
        Log.d(TAG, String.format("Re-loading data for Region %d", region_idx));
        //Update the temperature readings
        DBLoaderMeasurement dbLoaderMeasurement = new DBLoaderMeasurement(getActivity());
        mTempReadings = dbLoaderMeasurement.getTemperaturesForPatientAtIndex(mPatient.getPatientId(), region_idx);

        populateListTempReadings();

        //Mark the list to be updated
        mAdapter = new ReviewTemperatureListAdapter(getActivity(),R.layout.review_therma_list_item, mListTempReadings);
        mListView.setAdapter(mAdapter);

        //Update the graph
        redraw_graph();
    }

    @Override
    public void onTap(float x, float y) {

        if (mRegionImage != null) {
            int mPointIdx = mRegionImage.findPointIndex(x, y);


            if (mPointIdx != -1) {
                //Only clear if we found a new point
                mRegionImage.clearSelection();

                //Found a close region, visually mark it
                mRegionImage.addSelection(mPointIdx);

                updateSelectedRegion(mPointIdx);
            }
            mRegionImage.invalidate(); //Re-draw
        }
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
        public int getItemViewType(int position) {
            int viewType = layoutResourceId;
            switch (position) {
                case 0:
                    viewType = R.layout.review_therma_image_item;
                    break;
                case 1:
                    viewType = R.layout.review_therma_graph_item;
                    break;
                default:
                    break;
            }
            return viewType;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();

            int viewType = getItemViewType(position);
            switch (viewType) {
                case R.layout.review_therma_image_item:
                    convertView = mRegionImage; //Re-use directly so we don't loose selection state!
                    //TODO: this currently resets the list scroll, don't know how to fix that
                    break;
                case R.layout.review_therma_graph_item:
                    convertView = inflater.inflate(R.layout.review_therma_graph_item, parent, false);
                    mGraphTemp = (GraphView) convertView.findViewById(R.id.graph_review_therma);
                    textHeadingDate = (TextView) convertView.findViewById(R.id.text_review_therma_heading_date);
                    textHeadingTime = (TextView) convertView.findViewById(R.id.text_review_therma_heading_time);
                    textHeadingTemp = (TextView) convertView.findViewById(R.id.text_review_therma_heading_temp);
                    textThermaStatus = (TextView) convertView.findViewById(R.id.text_therma_status);


                    // construct graph here
                    redraw_graph(); //Draw it the first time
                    break;
                default:
                    convertView = inflater.inflate(layoutResourceId, parent, false);

                    TextView textViewDate = (TextView) convertView.findViewById(R.id.text_review_therma_list_date);
                    TextView textViewTime = (TextView) convertView.findViewById(R.id.text_review_therma_list_time);
                    TextView textViewTemp = (TextView) convertView.findViewById(R.id.text_review_therma_list_temp);

                    if (readingsList == null) {
                        textViewDate.setText("");
                        textViewTime.setText("");
                        textViewTemp.setText("");
                    } else {
                        MeasurementReading mReading = readingsList.get(position);
                        textViewDate.setText(Utils.prettyDate(mReading.getMeas_timestamp()));
                        textViewTime.setText(Utils.prettyTimeDiffHrs(mPatient.getPatientOpDateTime(), mReading.getMeas_timestamp()));
                        textViewTemp.setText(Utils.prettyTempCelsius(mReading.getMeas_temperature()));
                    }
                    break;
            }
            return convertView;
        }
    }

}
