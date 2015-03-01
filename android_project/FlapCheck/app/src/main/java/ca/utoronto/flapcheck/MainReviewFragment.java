package ca.utoronto.flapcheck;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainReviewFragment extends Fragment {
    private long mPatientId;

    public MainReviewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_review, container, false);

        Spinner spinner = (Spinner) view.findViewById(R.id.review_patient_spinner);

        PatientOpenDBHelper dbHelper = new PatientOpenDBHelper(getActivity().getApplicationContext());
        List<Patient> patientList = dbHelper.getAllPatients();
        ArrayAdapter<Patient> patientArrayAdapter = new ArrayAdapter<Patient>(getActivity(), android.R.layout.simple_spinner_dropdown_item, patientList);
        spinner.setAdapter(patientArrayAdapter);

        //Register what patient has been selected
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Patient patient = (Patient) parent.getItemAtPosition(position);
                mPatientId = patient.getPatientId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mPatientId = Patient.INVALID_ID;
            }
        });

        Button photoButton = (Button) view.findViewById(R.id.photo_review_button);
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ReviewActivity.class);
                Bundle args = new Bundle();
                args.putLong(ReviewActivity.ARG_PATIENT_ID, mPatientId);
                args.putString(ReviewActivity.ARG_MEASUREMENT_TYPE, Constants.MEASUREMENT_PHOTO);

                intent.putExtras(args);

                startActivity(intent);
            }
        });

        return view;
    }


}
