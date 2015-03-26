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
        final View view = inflater.inflate(R.layout.fragment_review_therma, container, false);

        setHasOptionsMenu(true);

        mPatient = mListenerCallback.getPatient();
        getActivity().setTitle(mPatient.getPatientName() + " " + "(" + mPatient.getPatientMRN() + ")");

        DBLoaderMeasurement dbLoaderMeasurement = new DBLoaderMeasurement(getActivity());
        List<MeasurementReading> tempReadings = dbLoaderMeasurement.getTemperaturesForPatient(mPatient.getPatientId());

        ProgressBar spinner = (ProgressBar) view.findViewById(R.id.progress_review_temp);
        GraphView graphTemp = (GraphView) view.findViewById(R.id.graph_review_therma);

        FrameLayout regionFrame = (FrameLayout) view.findViewById(R.id.region_frame_review_therma);
        mRegionImage = (RegionSelectImageView) view.findViewById(R.id.region_image_review_therma);
        mRegionImage.setTapListener(this);
//        mRegionTapSelectOverlay = new TapSelectOverlay(getActivity(), this);

        //Load the correct image
        File pictureDir = new File(mPatient.getPatientPhotoPath());

        //Fill the paths into a list
        File[] imageFiles = pictureDir.listFiles();
        if(imageFiles.length > 0) {
            mRegionImage.setImageURI(Uri.fromFile(imageFiles[0]));
            mRegionImage.requestLayout();
        }

        //Add the measurement points to the overlay
        DBLoaderPointToMeasure dbPointsLoader = new DBLoaderPointToMeasure(getActivity());
        final List<PointToMeasure> pointsOverlayList =  dbPointsLoader.getPointsToMeasureForPatient(mPatient.getPatientId());
        final List<PointFloat> pointsToDraw = new ArrayList<PointFloat>();

        //We don't know the regionImage size until layout is complete, so set up a callback
        //to de-normalize the points and add then to the overlay once this information is known
        ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
        if(viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    //Deregister since we only need to do this once
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    for (PointToMeasure pointOverlay : pointsOverlayList) {
                        PointFloat p = new PointFloat(pointOverlay.getPointX(), pointOverlay.getPointY());

                        pointsToDraw.add(p);
                        //pointList is the location of the regions of interest on the image
                        //A circle is drawn at each point in the list, which can then be selected by tapping
                        mRegionImage.setPointList(pointsToDraw);
                    }
                }
            });

        }

        //Add the image and tap overlay to regionFrame
//        regionFrame.addView(mRegionTapSelectOverlay);




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
        item.setIcon(R.drawable.ic_temperature_grey);

        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public void onTap(float x, float y) {

        if (mRegionImage != null) {
            int mPointIdx = mRegionImage.findPointIndex(x, y);

            mRegionImage.clearSelection();
            if (mPointIdx != -1) {
                //Found a close region, visually mark it
                mRegionImage.addSelection(mPointIdx);


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
                textViewTime.setText(Utils.prettyTimeDiffHrs(mPatient.getPatientOpDateTime(), mReading.getMeas_timestamp()));
                textViewTemp.setText(Utils.prettyTempCelsius(mReading.getMeas_temperature()));
            }
            return convertView;
        }
    }

}
