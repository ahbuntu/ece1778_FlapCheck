package ca.utoronto.flapcheck;

import android.content.Context;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.List;

/**
 * Created by ahmadul.hassan on 2015-03-04.
 */
public class ReviewRecycleAdapter extends RecyclerView.Adapter<ReviewRecycleAdapter.ViewHolder> {
    private static final String  TAG = "ReviewRecyleAdapter";
    private long mPatientId;
    private Context mContext;

    private ProgressBar progressTemp;
    private GraphView graphTemp;

    private List<MeasurementReading> tempReadings;
    private MeasurementReading colourReading;

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
    public ReviewRecycleAdapter(long patientID, Context context) {
        mPatientId = patientID;
        mContext = context;
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
                break;
            case R.id.card_review_colour:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_review_colour, parent, false);
                // set the view's size, margins, paddings and layout parameters
                //        ...
                break;
            case R.id.card_review_cap_refill:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_review_cap_refill, parent, false);
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
        int viewType = holder.getItemViewType();
        switch (viewType) {
            case R.id.card_review_temp:
                if (progressTemp == null || graphTemp == null) {
                    progressTemp = (ProgressBar) holder.itemView.findViewById(R.id.progress_card_temp);
                    graphTemp = (GraphView) holder.itemView.findViewById(R.id.graph_temp_summary);
                }
                updateCardTempProgress(false);
                RetrieveReviewData reviewDataHelper = new RetrieveReviewData(mPatientId);
                reviewDataHelper.execute(Constants.MEASUREMENT_TEMP);
                break;
            case R.id.card_review_colour:
                break;
            case R.id.card_review_cap_refill:
                break;
            case R.id.card_review_pulse:
                break;
            case R.id.card_review_photo:
                break;
            default:
                break;
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.d(TAG, "onBindViewHolder: itemView type " + holder.getItemViewType());
                //TODO: implement switch-case statement to launch appropriate review fragment
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
                    Log.d(TAG, "Patient ID: " + mReading.getMeas_patientID() + " Temp: " + mReading.getMeas_temperature());
                    DataPoint point = new DataPoint(i, mReading.getMeas_temperature());
                    series.appendData(point, false, tempReadings.size());
                }
                graphTemp.addSeries(series);
            }
        } else {
            if (progressTemp != null) {
                Log.d(TAG, "null progressTemp");
                progressTemp.setVisibility(View.VISIBLE);
                graphTemp.setVisibility(View.INVISIBLE);
                graphTemp.removeAllSeries();
            }
        }
    }

    private class RetrieveReviewData extends AsyncTask<String, Integer, List<MeasurementReading>> {

        private long mRetPatientId = -1;
        private String measType;
        public RetrieveReviewData(long patientID) {
            mRetPatientId = patientID;
        }

        @Override
        protected List<MeasurementReading> doInBackground(String... measurements) {
            measType = measurements[0];
            List<MeasurementReading> readings = null;
            switch (measType) {
                case Constants.MEASUREMENT_TEMP:
                    DBLoaderMeasurement dbLoaderMeasurement = new DBLoaderMeasurement(mContext);
                    readings = dbLoaderMeasurement.getTemperaturesForPatient(mRetPatientId);
                    break;
                case Constants.MEASUREMENT_COLOUR:
                    break;
                case Constants.MEASUREMENT_CAP_REFILL:
                    break;
                case Constants.MEASUREMENT_PULSE:
                    break;
                case Constants.MEASUREMENT_PHOTO:
//                    List<Patient> patients = patientsDB.getAllPatients();
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
}
