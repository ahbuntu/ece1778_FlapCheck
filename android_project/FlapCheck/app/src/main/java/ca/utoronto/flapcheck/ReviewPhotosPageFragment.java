package ca.utoronto.flapcheck;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReviewPhotosPageFragment extends Fragment {
    public static String ARG_POST_OP_DELTA_TIME = "post_op_delta_time";
    public static String ARG_IMAGE_PATH = "image_path";

    public ReviewPhotosPageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_review_photos_page, container, false);

        ImageView imageView = (ImageView) view.findViewById(R.id.photo_page_image);
        TextView textView = (TextView) view.findViewById(R.id.photo_page_time);

        Bundle args = getArguments();
        if(args.containsKey(ARG_POST_OP_DELTA_TIME)) {
            long postOpTimeDeltaMs = args.getLong(ARG_POST_OP_DELTA_TIME);
            long postOpTimeDeltaHrs = TimeUnit.MILLISECONDS.toHours(postOpTimeDeltaMs);
            long postOpTimeDeltaMin = TimeUnit.MILLISECONDS.toMinutes(postOpTimeDeltaMs) - TimeUnit.HOURS.toMinutes(postOpTimeDeltaHrs);
            long postOpTimeDeltaSec = TimeUnit.MILLISECONDS.toSeconds(postOpTimeDeltaMs) - TimeUnit.MINUTES.toSeconds(postOpTimeDeltaMin) - TimeUnit.HOURS.toSeconds(postOpTimeDeltaHrs);

            float hrsPostOp = postOpTimeDeltaHrs + postOpTimeDeltaMin / 60f + postOpTimeDeltaSec / 3600f;
            textView.setText(String.format("%+.2f hrs post-op", hrsPostOp));
        } else {
            textView.setText("Unknown time post-op");
        }

        imageView.setImageURI(Uri.fromFile(new File(getArguments().getString(ARG_IMAGE_PATH))));

        return view;
    }


}
