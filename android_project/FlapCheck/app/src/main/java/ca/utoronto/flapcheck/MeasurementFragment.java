package ca.utoronto.flapcheck;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class MeasurementFragment extends Fragment {
    public interface MeasurementFragmentListener {
        void onTakePhoto();
    }

    public MeasurementFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_measurement, container, false);

        //Register each of the buttons
        Button takePicture = (Button) view.findViewById(R.id.photo_button);
        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MeasurementFragmentListener activity = (MeasurementFragmentListener) getActivity();
                activity.onTakePhoto();
            }
        });

        return view;
    }


}
