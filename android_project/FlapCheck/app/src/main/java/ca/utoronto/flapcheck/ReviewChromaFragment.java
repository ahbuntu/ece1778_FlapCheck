package ca.utoronto.flapcheck;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class ReviewChromaFragment extends Fragment {
    private static String TAG = "ReviewChromaFragment";

    private ReviewColourListAdapter mAdapter;

    public interface ReviewChromaFragmentListener {
        Patient getPatient();
    }

    private ReviewChromaFragmentListener mListenerCallback;

    public ReviewChromaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListenerCallback = (ReviewChromaFragmentListener) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_review_chroma, container, false);

        setHasOptionsMenu(true);

        Patient patient = mListenerCallback.getPatient();
        getActivity().setTitle(patient.getPatientName() + " " + "(" + patient.getPatientMRN() + ")");

        DBLoaderMeasurement dbLoaderMeasurement = new DBLoaderMeasurement(getActivity());
        List<MeasurementReading> colourReadings = dbLoaderMeasurement.getColoursForPatient(patient.getPatientId());

        mAdapter = new ReviewColourListAdapter(getActivity(),R.layout.review_chroma_list_item, colourReadings);
        AbsListView mListView = (AbsListView) view.findViewById(R.id.list_review_chroma);
        mListView.setAdapter(mAdapter);

        ProgressBar spinner = (ProgressBar) view.findViewById(R.id.progress_review_colour);
        spinner.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_review, menu);
        MenuItem item = menu.findItem(R.id.action_logo);
        item.setIcon(R.drawable.ic_colour);

        super.onCreateOptionsMenu(menu,inflater);
    }

    private class ReviewColourListAdapter extends BaseAdapter {
        private static final String TAG = "ReviewColourListAdapter";
        Context mContext;
        int layoutResourceId;
        List<MeasurementReading> readingsList = null;

        /**
         * constructor for the adapter
         * @param context - context for which it will be displayed
         * @param layoutResId - the layout to which the adapter will be bound
         */
        public ReviewColourListAdapter(Context context, int layoutResId, List<MeasurementReading> readings) {
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

            TextView textViewDate = (TextView) convertView.findViewById(R.id.text_review_chroma_list_date);
            TextView textViewTime = (TextView) convertView.findViewById(R.id.text_review_chroma_list_time);
            ImageView imageViewColour = (ImageView) convertView.findViewById(R.id.image_review_chroma_list_colour);

            // object item based on the position
//            Log.d(TAG, "pos to inflate: " + position);

            if (readingsList == null) {
                textViewDate.setText("");
                textViewTime.setText("");
            } else {
                MeasurementReading mReading = readingsList.get(position);
                textViewDate.setText(Utils.prettyDate(mReading.getMeas_timestamp()));
                textViewTime.setText(Utils.prettyTime(mReading.getMeas_timestamp()));
                imageViewColour.setBackgroundColor(Color.parseColor(mReading.getMeas_colour_hex()));
            }
            return convertView;
        }
    }
}
