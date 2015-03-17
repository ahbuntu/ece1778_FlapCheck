package ca.utoronto.flapcheck;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainMeasurementFragment extends android.support.v4.app.Fragment {
    private static final String TAG = "MeasureFragment";

    public interface MainMeasurementSensorListener {
        void onMeasurePhoto();
        void onMeasureCapRefill();
        void onMeasurePulse();
    }

    public interface MainMeasurementNODEListener {
        void onMeasureTemperature();
        void onMeasureColour();
    }

    public MainMeasurementFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_measurement, container, false);

        /*
         * Register each of the buttons
         */

        //Temperature
        Button takeTemp = (Button) view.findViewById(R.id.temperature_button);
        takeTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainMeasurementNODEListener activity = (MainMeasurementNODEListener) getActivity();
                activity.onMeasureTemperature();
            }
        });

        //Colour
        Button takeColour = (Button) view.findViewById(R.id.colour_button);
        takeColour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainMeasurementNODEListener activity = (MainMeasurementNODEListener) getActivity();
                activity.onMeasureColour();
            }
        });

        //Cap Refill
        Button takeCapRefill = (Button) view.findViewById(R.id.cap_refill_button);
        takeCapRefill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainMeasurementSensorListener activity = (MainMeasurementSensorListener) getActivity();
                activity.onMeasureCapRefill();
            }
        });

        //Pulse
        Button takePulse = (Button) view.findViewById(R.id.pulse_button);
        takePulse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainMeasurementSensorListener activity = (MainMeasurementSensorListener) getActivity();
                activity.onMeasurePulse();
            }
        });

        //Photo
        Button takePicture = (Button) view.findViewById(R.id.photo_button);
        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainMeasurementSensorListener activity = (MainMeasurementSensorListener) getActivity();
                activity.onMeasurePhoto();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
