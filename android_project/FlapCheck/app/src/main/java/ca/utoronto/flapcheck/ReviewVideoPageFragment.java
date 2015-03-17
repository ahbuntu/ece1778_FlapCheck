package ca.utoronto.flapcheck;


import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReviewVideoPageFragment extends Fragment {
    private static final String TAG = "ReviewVideoPageFragment";
    public static final String ARG_POST_OP_DELTA_TIME = "post_op_time_delta";
    public static final String ARG_RAW_VIDEO_PATH = "raw_video_path";
    public static final String ARG_PROCESSED_VIDEO_PATH = "processed_video_path";

    private int video_ready_count;
    VideoView rawVideoView;
    VideoView processedVideoView;
    ProgressBar progressBar;
    TextView text_overlay;

    public ReviewVideoPageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_review_video_page, container, false);

        rawVideoView = (VideoView) view.findViewById(R.id.video_view_raw);
//        processedVideoView = (VideoView) view.findViewById(R.id.video_view_processed);
        progressBar = (ProgressBar) view.findViewById(R.id.video_load_progress_bar);
        text_overlay = (TextView) view.findViewById(R.id.video_page_time);



        Bundle args = getArguments();
        File rawVidFile = new File(args.getString(ARG_RAW_VIDEO_PATH));
        text_overlay.setText(rawVidFile.getName());

        video_ready_count = 0;
        rawVideoView.setVideoPath(args.getString(ARG_RAW_VIDEO_PATH));
        rawVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.d(TAG, "Raw Video Loaded");
                video_ready_count += 1;
                if(video_ready_count == 1) {
                    onVideosLoaded();
                }
            }
        });

//        processedVideoView.setVideoPath(args.getString(ARG_PROCESSED_VIDEO_PATH));
//        processedVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                Log.d(TAG, "Processed Video Loaded");
//                video_ready_count += 1;
//                if(video_ready_count == 2) {
//                    onVideosLoaded();
//                }
//            }
//        });

        view.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Clicked page");
                if(video_ready_count == 1) {
                    Log.d(TAG, "Starting videos");
                    rawVideoView.start();
//                    processedVideoView.start();
                }
            }
        });

        return view;
    }

    public void onVideosLoaded() {
        progressBar.setVisibility(View.INVISIBLE);
        rawVideoView.setVisibility(View.VISIBLE);
//        processedVideoView.setVisibility(View.VISIBLE);
    }


}
