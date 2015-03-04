package ca.utoronto.flapcheck;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

/**
 * Created by ahmadul.hassan on 2015-03-04.
 */
public class ReviewRecycleAdapter extends RecyclerView.Adapter<ReviewRecycleAdapter.ViewHolder> {
    private static final String  TAG = "ReviewRecyleAdapter";
    private String[] mDataset;
    private View rootView;

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
    public ReviewRecycleAdapter(String[] myDataset) {
        mDataset = myDataset;
//        rootView = rView;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ReviewRecycleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        View v = null;

        switch (viewType) {
            case R.id.card_review_temp:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_review_temperature, parent, false);
                GraphView graph = (GraphView) v.findViewById(R.id.graph_temp_summary);
                LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
                        new DataPoint(0, 1),
                        new DataPoint(1, 5),
                        new DataPoint(2, 3),
                        new DataPoint(3, 2),
                        new DataPoint(4, 6)
                });
                graph.addSeries(series);
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
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
//        holder.mTextView.setText(mDataset[position]);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onBindViewHolder: itemView type " + holder.getItemViewType());
                //TODO: implement switch-case statement to launch appropriate review fragment
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }

    @Override
    public int getItemViewType(int position) {
        //Assume that cards will be displayed in the following order
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
}
