package ca.utoronto.flapcheck;


import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.VideoView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReviewVideoPageFragment extends Fragment {
    private static final String TAG = "ReviewVideoPageFragment";
    public static final String ARG_POST_OP_DELTA_TIME = "post_op_time_delta";
    public static final String ARG_RAW_VIDEO_PATH = "raw_video_path";
    public static final String ARG_PROCESSED_VIDEO_PATH = "processed_video_path";

    public ReviewVideoPageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_review_video_page, container, false);

        final VideoView rawVideoView = (VideoView) view.findViewById(R.id.video_view_raw);
//        final VideoView processedVideoView = (VideoView) view.findViewById(R.id.video_view_processed);
        TextView text_overlay = (TextView) view.findViewById(R.id.video_page_time);



        Bundle args = getArguments();
        text_overlay.setText(args.getString(ARG_RAW_VIDEO_PATH));

        rawVideoView.setVideoPath(args.getString(ARG_RAW_VIDEO_PATH));
//        processedVideoView.setVideoPath(args.getString(ARG_PROCESSED_VIDEO_PATH));

        view.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Clicked page");
                rawVideoView.start();
            }
        });

        return view;
    }


}
