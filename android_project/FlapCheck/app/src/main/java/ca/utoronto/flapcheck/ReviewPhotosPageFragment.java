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


/**
 * A simple {@link Fragment} subclass.
 */
public class ReviewPhotosPageFragment extends Fragment {
    public static String ARG_HOURS_POST_OP = "hours_post_op";
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

        textView.setText(String.format("%.2f hours post-op", getArguments().getFloat(ARG_HOURS_POST_OP)));
        imageView.setImageURI(Uri.fromFile(new File(getArguments().getString(ARG_IMAGE_PATH))));

        return view;
    }


}
