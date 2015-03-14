package ca.utoronto.flapcheck;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ahmadul.hassan on 2015-03-04.
 */
public class ReviewRecycleAdapter extends RecyclerView.Adapter<ReviewRecycleAdapter.ViewHolder> {
    private static final String  TAG = "ReviewRecyleAdapter";
    private long mPatientId;
    private Context mContext;
    private MainReviewFragment.MainReviewFragmentListener mReviewFragmentListener;

    private ProgressBar progressTemp;
    private GraphView graphTemp;

    private List<MeasurementReading> tempReadings;
    private List<MeasurementReading> colourReadings;
    private List<File> photoReadings;
    private List<Bitmap> pulseThumbnails;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public CardView mCardView;
        public ViewHolder(CardView v) {
            super(v);
            mCardView = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ReviewRecycleAdapter(long patientID, Context context, MainReviewFragment.MainReviewFragmentListener reviewFragmentListener) {
        mPatientId = patientID;
        mContext = context;
        mReviewFragmentListener = reviewFragmentListener;
    }

    //region ViewHolder Lifecycle callbacks

    // Create new views (invoked by the layout manager)
    @Override
    public ReviewRecycleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        View v = null;

        switch (viewType) {
            case R.id.card_review_temp:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_review_temperature, parent, false);
                progressTemp = (ProgressBar) v.findViewById(R.id.progress_card_temp);
                graphTemp = (GraphView) v.findViewById(R.id.graph_temp_summary);
//                setMargins(v, 0, R.dimen.card_vertical_margin, 0, R.dimen.card_vertical_margin);
                break;
            case R.id.card_review_colour:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_review_colour, parent, false);
                break;
            case R.id.card_review_cap_refill:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_review_cap_refill, parent, false);
                // set the view's size, margins, paddings and layout parameters
                //        ...

                break;
            case R.id.card_review_pulse:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_review_pulse, parent, false);
                break;
            case R.id.card_review_photo:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_review_photo, parent, false);

                break;
            default:
                break;
        }
        ViewHolder vh = new ViewHolder((CardView)v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final int viewType = holder.getItemViewType();
        switch (viewType) {
            case R.id.card_review_temp:
                if (progressTemp == null || graphTemp == null) {
                    progressTemp = (ProgressBar) holder.itemView.findViewById(R.id.progress_card_temp);
                    graphTemp = (GraphView) holder.itemView.findViewById(R.id.graph_temp_summary);
                }
                updateCardTempProgress(false);
                RetrieveReviewData reviewDataHelper = new RetrieveReviewData(mPatientId, holder.itemView);
                reviewDataHelper.execute(Constants.MEASUREMENT_TEMP);
                break;
            case R.id.card_review_colour:
                updateCardColourProgress(holder.itemView, false);
                RetrieveReviewData reviewColourHelper = new RetrieveReviewData(mPatientId, holder.itemView);
                reviewColourHelper.execute(Constants.MEASUREMENT_COLOUR);
                break;
            case R.id.card_review_cap_refill:
                break;
            case R.id.card_review_pulse:
                updateCardPulseProgress(holder.itemView, false);
                RetrieveReviewVideoThumbnails reviewPulseHelper = new RetrieveReviewVideoThumbnails(mPatientId, holder.itemView);
                reviewPulseHelper.execute(Constants.MEASUREMENT_PULSE);
                break;
            case R.id.card_review_photo:
                updateCardPhotoProgress(holder.itemView, false);
                RetrieveReviewFileData reviewPhotoHelper = new RetrieveReviewFileData(mPatientId, holder.itemView);
                reviewPhotoHelper.execute(Constants.MEASUREMENT_PHOTO);
                break;
            default:
                break;
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.d(TAG, "onBindViewHolder: itemView type " + holder.getItemViewType());
            switch(viewType) {
                case R.id.card_review_photo:
                    mReviewFragmentListener.onReview(mPatientId, Constants.MEASUREMENT_PHOTO);
                    break;
                case R.id.card_review_cap_refill:
                    mReviewFragmentListener.onReview(mPatientId, Constants.MEASUREMENT_CAP_REFILL);
                    break;
                case R.id.card_review_pulse:
                    mReviewFragmentListener.onReview(mPatientId, Constants.MEASUREMENT_PULSE);
                    break;
                default:
                    break;
            }
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        //        Assume that cards will be displayed in the following order
//        "TEMPERATURE", "COLOUR", "CAPILLARY REFILL", "PULSE", "PICTURE"
//              0            1             2              3         4
        return 5;
    }

    @Override
    public int getItemViewType(int position) {
//        Assume that cards will be displayed in the following order
//        "TEMPERATURE", "COLOUR", "CAPILLARY REFILL", "PULSE", "PICTURE"
//              0            1             2              3         4
        switch (position) {
            case 0:
                return R.id.card_review_temp;
            case 1:
                return R.id.card_review_colour;
            case 2:
                return R.id.card_review_cap_refill;
            case 3:
                return R.id.card_review_pulse;
            case 4:
                return R.id.card_review_photo;
            default:
                break;
        }
        return 0;
    }

    //endregion

    /**
     * method to hide the spinning progress bar on the temperature card when the reading is loaded
     * @param loadFinished - indicates whether the reading was successfully retrieved from the db
     */
    private void updateCardTempProgress(boolean loadFinished) {
        if (loadFinished) {
            if (progressTemp != null) {
                progressTemp.setVisibility(View.INVISIBLE);
                graphTemp.setVisibility(View.VISIBLE);

                // construct graph here
                LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();
                int i = 0;
                for (MeasurementReading mReading : tempReadings) {
                    i++;
//                    Log.d(TAG, "Patient ID: " + mReading.getMeas_patientID() + " Temp: " + mReading.getMeas_temperature());
                    DataPoint point = new DataPoint(i, mReading.getMeas_temperature());

                    //assume that temperature is returned in ascending timestamp order
                    if (i == tempReadings.size()) {
                        graphTemp.setTitle("Last recording " + mReading.getMeas_temperature() + " ºC");
                    }series.appendData(point, false, tempReadings.size());
                }
                graphTemp.addSeries(series);
                GridLabelRenderer gridStyler =  graphTemp.getGridLabelRenderer();
                gridStyler.setGridStyle(GridLabelRenderer.GridStyle.NONE);
            }
        } else {
            if (progressTemp != null) {
//                Log.d(TAG, "null progressTemp");
                progressTemp.setVisibility(View.VISIBLE);
                graphTemp.setVisibility(View.INVISIBLE);
                graphTemp.removeAllSeries();
            }
        }
    }

    public void setMargins (View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    /**
     * method to hide the spinning progress bars on the colour card when the reading is loaded
     *
     * @param loadFinished - indicates whether the reading was successfully retrieved from the db
     */
    private void updateCardColourProgress(View cardView, boolean loadFinished) {
        if (loadFinished) {
            Log.d(TAG, "# of colour readings = " + colourReadings.size() + " for patient: " + mPatientId);
            int i = 0;
            for (MeasurementReading mReading : colourReadings) {
                Log.d(TAG, "colour reading: " + mReading.getMeas_colour_hex());
                int colorVal = Color.parseColor(mReading.getMeas_colour_hex());
                i++;
                if (i == 1) {
                    TextView mTextDate =  (TextView) cardView.findViewById(R.id.text_first_colour_date);
                    mTextDate.setText(Utils.prettyDate(mReading.getMeas_timestamp()));
                    TextView mTextTime =  (TextView) cardView.findViewById(R.id.text_first_colour_time);
                    mTextTime.setText(Utils.prettyTime(mReading.getMeas_timestamp()));
                    cardView.findViewById(R.id.imageView_first_colour)
                            .setBackgroundColor(colorVal);
                }
                if (i == 2) {
                    TextView mTextDate =  (TextView) cardView.findViewById(R.id.text_second_colour_date);
                    mTextDate.setText(Utils.prettyDate(mReading.getMeas_timestamp()));
                    TextView mTextTime =  (TextView) cardView.findViewById(R.id.text_second_colour_time);
                    mTextTime.setText(Utils.prettyTime(mReading.getMeas_timestamp()));
                    cardView.findViewById(R.id.imageView_second_colour)
                            .setBackgroundColor(colorVal);
                }
                if (i == colourReadings.size()) {
                    cardView.findViewById(R.id.imageView_latest_colour)
                            .setBackgroundColor(colorVal);
                }
            }
            colour_progress_visibility(cardView, false);
            if (colourReadings.size() > 0) {
                colour_latest_visibility(cardView, true);
                colour_first_visibility(cardView, true);
                colour_second_visibility(cardView, true);
            }
        } else {
            colour_progress_visibility(cardView, true);
            colour_latest_visibility(cardView, false);
            colour_first_visibility(cardView, false);
            colour_second_visibility(cardView, false);
        }
    }

    /**
     * toggles the visibility of app spinning progress elements in the colour CardView.
     * don't use for anything else
     *
     * @param cardView
     * @param visible
     */
    private void colour_progress_visibility(View cardView, boolean visible) {
        if (visible) {
            cardView.findViewById(R.id.progress_card_latest_colour).setVisibility(View.VISIBLE);
            cardView.findViewById(R.id.progress_card_first_colour).setVisibility(View.VISIBLE);
            cardView.findViewById(R.id.progress_card_second_colour).setVisibility(View.VISIBLE);
        } else {
            cardView.findViewById(R.id.progress_card_latest_colour).setVisibility(View.INVISIBLE);
            cardView.findViewById(R.id.progress_card_first_colour).setVisibility(View.INVISIBLE);
            cardView.findViewById(R.id.progress_card_second_colour).setVisibility(View.INVISIBLE);
        }
    }

    /**
     * toggles the visibility of the latest reading elements in the CardView.
     * don't use for anything else
     *
     * @param cardView the card view corresponding to colour summary
     * @param visible true means visible; false is invisible
     */
    private void colour_latest_visibility(View cardView, boolean visible) {
        if (visible) {
            cardView.findViewById(R.id.text_latest_colour).setVisibility(View.VISIBLE);
            cardView.findViewById(R.id.imageView_latest_colour).setVisibility(View.VISIBLE);
        } else {
            cardView.findViewById(R.id.text_latest_colour).setVisibility(View.INVISIBLE);
            cardView.findViewById(R.id.imageView_latest_colour).setVisibility(View.INVISIBLE);
        }
    }

    /**
     * toggles the visibility of the first colour reading elements in the CardView.
     * don't use for anything else
     *
     * @param cardView the card view corresponding to colour summary
     * @param visible true means visible; false is invisible
     */
    private void colour_first_visibility(View cardView, boolean visible) {
        if (visible) {
            cardView.findViewById(R.id.text_first_colour_date).setVisibility(View.VISIBLE);
            cardView.findViewById(R.id.text_first_colour_time).setVisibility(View.VISIBLE);
            cardView.findViewById(R.id.imageView_first_colour).setVisibility(View.VISIBLE);
        } else {
            cardView.findViewById(R.id.text_first_colour_date).setVisibility(View.INVISIBLE);
            cardView.findViewById(R.id.text_first_colour_time).setVisibility(View.INVISIBLE);
            cardView.findViewById(R.id.imageView_first_colour).setVisibility(View.INVISIBLE);
        }
    }

    /**
     * toggles the visibility of the second colour reading elements in the CardView.
     * don't use for anything else
     *
     * @param cardView the card view corresponding to colour summary
     * @param visible true means visible; false is invisible
     */
    private void colour_second_visibility(View cardView, boolean visible) {
        if (visible) {
            cardView.findViewById(R.id.text_second_colour_date).setVisibility(View.VISIBLE);
            cardView.findViewById(R.id.text_second_colour_time).setVisibility(View.VISIBLE);
            cardView.findViewById(R.id.imageView_second_colour).setVisibility(View.VISIBLE);
        } else {
            cardView.findViewById(R.id.text_second_colour_date).setVisibility(View.INVISIBLE);
            cardView.findViewById(R.id.text_second_colour_time).setVisibility(View.INVISIBLE);
            cardView.findViewById(R.id.imageView_second_colour).setVisibility(View.INVISIBLE);
        }
    }

    private void updateCardPhotoProgress(View cardView, boolean loadFinished) {
        ImageView img1 = (ImageView) cardView.findViewById(R.id.img1_picture_card);
        ImageView img2 = (ImageView) cardView.findViewById(R.id.img2_picture_card);
        TextView info = (TextView) cardView.findViewById(R.id.text_picture_card_info);
        if(loadFinished) {
            info.setText(String.format("%d Measurement(s)", photoReadings.size()));
            if (photoReadings.size() < 1) {
                //No measurements

            } else {
                if (photoReadings.size() > 0) {
                    //One measurement
                    img1.setImageURI(Uri.fromFile(photoReadings.get(0)));
                    img1.setVisibility(View.VISIBLE);
                }

                if (photoReadings.size() > 1) {
                    //Two or more measurements

                    //Set the second to the last taken image
                    int last_idx = photoReadings.size() - 1;
                    img2.setImageURI(Uri.fromFile(photoReadings.get(last_idx)));

                    img2.setVisibility(View.VISIBLE);
                }
            }
        } else {
            img1.setVisibility(View.INVISIBLE);
            img2.setVisibility(View.INVISIBLE);
            info.setText("Loading...");
        }
    }

    private void updateCardPulseProgress(View cardView, boolean loadFinished) {
        ImageView img1 = (ImageView) cardView.findViewById(R.id.img1_pulse_card);
        ImageView img2 = (ImageView) cardView.findViewById(R.id.img2_pulse_card);
        TextView info = (TextView) cardView.findViewById(R.id.text_pulse_card_info);
        if(loadFinished) {
            info.setText(String.format("%d Measurement(s)", pulseThumbnails.size()));
            if (pulseThumbnails.size() < 1) {
                //No measurements

            } else {
                if (pulseThumbnails.size() > 0) {
                    //One measurement
                    img1.setImageBitmap(pulseThumbnails.get(0));
                    img1.setVisibility(View.VISIBLE);
                }

                if (pulseThumbnails.size() > 1) {
                    //Two or more measurements

                    //Set the second to the last taken image
                    int last_idx = pulseThumbnails.size() - 1;
                    img2.setImageBitmap(pulseThumbnails.get(last_idx));

                    img2.setVisibility(View.VISIBLE);
                }
            }
        } else {
            img1.setVisibility(View.INVISIBLE);
            img2.setVisibility(View.INVISIBLE);
            info.setText("Loading...");
        }
    }

    private class RetrieveReviewData extends AsyncTask<String, Integer, List<MeasurementReading>> {

        private long mRetPatientId = -1;
        private String measType;
        private View cardView;

        public RetrieveReviewData(long patientID, View view) {
            mRetPatientId = patientID;
            cardView = view;
        }

        @Override
        protected List<MeasurementReading> doInBackground(String... measurements) {
            measType = measurements[0];
            List<MeasurementReading> readings = null;
            DBLoaderMeasurement dbLoaderMeasurement = new DBLoaderMeasurement(mContext);
            switch (measType) {
                case Constants.MEASUREMENT_TEMP:
                    readings = dbLoaderMeasurement.getTemperaturesForPatient(mRetPatientId);
                    break;
                case Constants.MEASUREMENT_COLOUR:
                    readings = dbLoaderMeasurement.getColoursForPatient(mRetPatientId);
                    break;
                case Constants.MEASUREMENT_CAP_REFILL:
                    break;
                case Constants.MEASUREMENT_PULSE:
                    break;
                case Constants.MEASUREMENT_PHOTO:
                    break;
                default:
                    break;
            }
            return readings;
        }

        @Override
        protected void onPostExecute(List<MeasurementReading> result) {
            super.onPostExecute(result);
            switch (measType) {
                case Constants.MEASUREMENT_TEMP:
                    tempReadings = result;
                    updateCardTempProgress(true);
                    break;
                case Constants.MEASUREMENT_COLOUR:
                    colourReadings = result;
                    updateCardColourProgress(cardView, true);
                    break;
                case Constants.MEASUREMENT_CAP_REFILL:
                    break;
                case Constants.MEASUREMENT_PULSE:
                    break;
                case Constants.MEASUREMENT_PHOTO:
                    break;
                default:
                    break;
            }
        }
    }

    private class RetrieveReviewFileData extends AsyncTask<String, Integer, List<File>> {

        private long mRetPatientId = -1;
        private String measType;
        private View cardView;

        public RetrieveReviewFileData(long patientID, View view) {
            mRetPatientId = patientID;
            cardView = view;
        }

        @Override
        protected List<File> doInBackground(String... measurements) {
            measType = measurements[0];
            List<File> readings = new ArrayList<File>();
            DBLoaderPatient dbLoaderPatient = new DBLoaderPatient(mContext);
            Patient patient = dbLoaderPatient.getPatient(mRetPatientId);
            File sourceDir = null;
            switch (measType) {
                case Constants.MEASUREMENT_PHOTO:
                    sourceDir = new File(patient.getPatientPhotoPath());
                    for (File file : sourceDir.listFiles()) {
                        readings.add(file);
                    }
                    break;

                default:
                    break;
            }

            return readings;
        }

        @Override
        protected void onPostExecute(List<File> result) {
            super.onPostExecute(result);
            switch (measType) {
                case Constants.MEASUREMENT_PHOTO:
                    photoReadings = result;
                    updateCardPhotoProgress(cardView, true);
                    break;
                default:
                    break;
            }
        }
    }

    private class RetrieveReviewVideoThumbnails extends AsyncTask<String, Integer, List<Bitmap>> {

        private long mRetPatientId = -1;
        private String measType;
        private View cardView;

        public RetrieveReviewVideoThumbnails(long patientID, View view) {
            mRetPatientId = patientID;
            cardView = view;
        }

        @Override
        protected List<Bitmap> doInBackground(String... measurements) {
            measType = measurements[0];
            List<File> vidFiles = new ArrayList<File>();
            List<Bitmap> thumbnails = new ArrayList<Bitmap>();
            DBLoaderPatient dbLoaderPatient = new DBLoaderPatient(mContext);
            Patient patient = dbLoaderPatient.getPatient(mRetPatientId);
            File sourceDir = null;
            switch (measType) {
                case Constants.MEASUREMENT_CAP_REFILL:
                    //Explicit fallthrough

                case Constants.MEASUREMENT_PULSE:
                    sourceDir = new File(patient.getPatientVidPath());
                    break;

                default:
                    break;
            }

            if(sourceDir != null) {
                for (File vidFile : sourceDir.listFiles()) {
                    Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(vidFile.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
                    thumbnails.add(thumbnail);
                }
            }

            return thumbnails;
        }

        @Override
        protected void onPostExecute(List<Bitmap> result) {
            super.onPostExecute(result);
            switch (measType) {
                case Constants.MEASUREMENT_CAP_REFILL:
                    break;
                case Constants.MEASUREMENT_PULSE:
                    pulseThumbnails = result;
                    updateCardPulseProgress(cardView, true);
                    break;
                default:
                    break;
            }
        }
    }
}
