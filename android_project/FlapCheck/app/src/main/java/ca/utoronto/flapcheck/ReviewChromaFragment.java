    package ca.utoronto.flapcheck;


    import android.app.Activity;
    import android.content.Context;
    import android.graphics.Color;
    import android.net.Uri;
    import android.os.Bundle;
    import android.support.v4.app.Fragment;
    import android.util.Log;
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

    import java.io.File;
    import java.util.ArrayList;
    import java.util.List;


    /**
     * A simple {@link android.support.v4.app.Fragment} subclass.
     */
    public class ReviewChromaFragment extends Fragment implements
            RegionSelectImageView.TapListener
    {
        private static String TAG = "ReviewChromaFragment";

        private ReviewColourListAdapter mAdapter;
        private Patient mPatient;
        private AbsListView mListView;
        private RegionSelectImageView mRegionImage;
        private List<MeasurementReading> mColourReadings;
        private List<MeasurementReading> mListColourReadings = new ArrayList<>();

        private int mDefaultRegionIdx = 0;

        TextView textHeadingDate;
        TextView textHeadingTime;
        TextView textHeadingColour;
        TextView textChromaStatus;

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

        private void populateListColourReadings(){
            //the first two are always null
            if (mListColourReadings.size() > 0) {
                mListColourReadings.clear();
            }
            mListColourReadings.add(null);
            mListColourReadings.add(null);
            for (MeasurementReading reading : mColourReadings) {
                mListColourReadings.add(reading);
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            final View view = inflater.inflate(R.layout.fragment_review_chroma, container, false);

            setHasOptionsMenu(true);

            mPatient = mListenerCallback.getPatient();
            getActivity().setTitle(mPatient.getPatientName() + " " + "(" + mPatient.getPatientMRN() + ")");

            DBLoaderMeasurement dbLoaderMeasurement = new DBLoaderMeasurement(getActivity());
            mColourReadings = dbLoaderMeasurement.getColoursForPatientAtIndex(mPatient.getPatientId(), mDefaultRegionIdx);

            /*
             * The region image is stateful, and must not be re-created or else selection is screwed up!
             */
            mRegionImage = (RegionSelectImageView) inflater.inflate(R.layout.review_chroma_image_item, container, false);
            mRegionImage.setTapListener(ReviewChromaFragment.this);

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
             * Load the colours
             */
            populateListColourReadings();

            mAdapter = new ReviewColourListAdapter(getActivity(),R.layout.review_chroma_list_item, mListColourReadings);
            mListView = (AbsListView) view.findViewById(R.id.list_review_chroma);
            mListView.setAdapter(mAdapter);

            return view;
        }

        private void update_headings(){
            if(mColourReadings.size() > 0 ) {
                textChromaStatus.setVisibility(View.GONE);
                textHeadingDate.setVisibility(View.VISIBLE);
                textHeadingTime.setVisibility(View.VISIBLE);
                textHeadingColour.setVisibility(View.VISIBLE);

                mListView.smoothScrollToPosition(1); //display the graph
                mListView.setSelection(1);
            } else {
                textChromaStatus.setVisibility(View.VISIBLE);
                textHeadingDate.setVisibility(View.GONE);
                textHeadingTime.setVisibility(View.GONE);
                textHeadingColour.setVisibility(View.GONE);
            }
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.menu_review, menu);
            MenuItem item = menu.findItem(R.id.action_logo);
            item.setIcon(R.drawable.ic_colour_grey);

            super.onCreateOptionsMenu(menu,inflater);
        }

        private void updateSelectedRegion(int region_idx) {
            Log.d(TAG, String.format("Re-loading data for Region %d", region_idx));
            //Update the colour readings
            DBLoaderMeasurement dbLoaderMeasurement = new DBLoaderMeasurement(getActivity());
            mColourReadings = dbLoaderMeasurement.getColoursForPatientAtIndex(mPatient.getPatientId(), region_idx); //TODO get the correct value for the region idx

            populateListColourReadings();

            //Mark the list to be updated
            mAdapter = new ReviewColourListAdapter(getActivity(),R.layout.review_chroma_list_item, mListColourReadings);
            mListView.setAdapter(mAdapter);

            //update the colour heading
            update_headings();
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
            public int getItemViewType(int position) {
                int viewType = layoutResourceId;
                switch (position) {
                    case 0:
                        viewType = R.layout.review_chroma_image_item;
                        break;
                    case 1:
                        viewType = R.layout.review_chroma_list_heading_item;
                        break;
                    default:
                        break;
                }
                return viewType;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Log.d(TAG, "getView called");
                LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                int viewType = getItemViewType(position);
                switch (viewType) {
                    case R.layout.review_chroma_image_item:
                        convertView = mRegionImage; //Re-use directly so we don't loose selection state!
                        //TODO: this currently resets the list scroll, don't know how to fix that
                        break;
                    case R.layout.review_chroma_list_heading_item:
                        convertView = inflater.inflate(R.layout.review_chroma_list_heading_item, parent, false);
                        textHeadingDate = (TextView) convertView.findViewById(R.id.text_review_chroma_heading_date);
                        textHeadingTime = (TextView) convertView.findViewById(R.id.text_review_chroma_heading_time);
                        textHeadingColour = (TextView) convertView.findViewById(R.id.text_review_chroma_heading_colour);
                        textChromaStatus = (TextView) convertView.findViewById(R.id.text_chroma_status);

                        update_headings(); //do it the first time
                        break;
                    default:
                        // inflate the layout
                        convertView = inflater.inflate(layoutResourceId, parent, false);

                        TextView textViewDate = (TextView) convertView.findViewById(R.id.text_review_chroma_list_date);
                        TextView textViewTime = (TextView) convertView.findViewById(R.id.text_review_chroma_list_time);
                        ImageView imageViewColour = (ImageView) convertView.findViewById(R.id.image_review_chroma_list_colour);

                        // object item based on the position
                        if (readingsList == null) {
                            textViewDate.setText("");
                            textViewTime.setText("");
                        } else {
                            MeasurementReading mReading = readingsList.get(position);
                            textViewDate.setText(Utils.prettyDate(mReading.getMeas_timestamp()));
                            textViewTime.setText(Utils.prettyTimeDiffHrs(mPatient.getPatientOpDateTime(), mReading.getMeas_timestamp()));
                            imageViewColour.setBackgroundColor(Color.parseColor(mReading.getMeas_colour_hex()));
                        }
    //                  Log.d(TAG, "pos to inflate: " + position);
                        break;
                }

                return convertView;
            }
        }
    }
