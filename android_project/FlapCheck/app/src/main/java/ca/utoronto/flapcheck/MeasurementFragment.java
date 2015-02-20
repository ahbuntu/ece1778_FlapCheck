package ca.utoronto.flapcheck;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class MeasurementFragment extends Fragment {
    public interface MeasurementFragmentListener {
        void onMeasurePhoto();
    }

    public MeasurementFragment() {
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
                Toast.makeText(getActivity(), "Temperature measurement not yet implemented!", Toast.LENGTH_SHORT).show();
            }
        });

        //Colour
        Button takeColour = (Button) view.findViewById(R.id.colour_button);
        takeColour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Colour measurement not yet implemented!", Toast.LENGTH_SHORT).show();
            }
        });

        //Cap Refill
        Button takeCapRefill = (Button) view.findViewById(R.id.cap_refill_button);
        takeCapRefill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Capillary refill measurement not yet implemented!", Toast.LENGTH_SHORT).show();
            }
        });

        //Pulse
        Button takePulse = (Button) view.findViewById(R.id.pulse_button);
        takePulse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Pulse measurement not yet implemented!", Toast.LENGTH_SHORT).show();
            }
        });

        //Photo
        Button takePicture = (Button) view.findViewById(R.id.photo_button);
        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MeasurementFragmentListener activity = (MeasurementFragmentListener) getActivity();
                activity.onMeasurePhoto();
            }
        });

        return view;
    }


}
