package ca.utoronto.flapcheck;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    public interface MainFragmentListener {
        void startMeasurementActivity();
        void startPatientEntryActivity();
    }

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);


        Button measureButton = (Button) view.findViewById(R.id.measure_button);
        measureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start up the measure activity
                MainFragmentListener activity = (MainFragmentListener) getActivity();
                activity.startMeasurementActivity();
            }
        });

        Button patientEntryButton = (Button) view.findViewById(R.id.patient_entry_button);
        patientEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start up the patient entry activity
                MainFragmentListener activity = (MainFragmentListener) getActivity();
                activity.startPatientEntryActivity();
            }
        });

        return view;
    }


}