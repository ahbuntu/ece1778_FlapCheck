package ca.utoronto.flapcheck;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainReviewFragment extends Fragment {
    private long mPatientId = Patient.INVALID_ID;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayAdapter<Patient> patientArrayAdapter;

    public MainReviewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_review, container, false);

        Spinner spinner = (Spinner) view.findViewById(R.id.review_patient_spinner);

        DBLoaderPatient dbHelper = new DBLoaderPatient(getActivity().getApplicationContext());
        List<Patient> patientList = dbHelper.getAllPatients();
        patientArrayAdapter = new ArrayAdapter<Patient>(getActivity(), android.R.layout.simple_spinner_dropdown_item, patientList);
        spinner.setAdapter(patientArrayAdapter);

        //Register what patient has been selected
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Patient patient = (Patient) parent.getItemAtPosition(position);
                mPatientId = patient.getPatientId();
                mAdapter = new ReviewRecycleAdapter(mPatientId, getActivity().getBaseContext());
                mRecyclerView.swapAdapter(mAdapter,false);
                mAdapter.notifyDataSetChanged();
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

        mRecyclerView = (RecyclerView) view.findViewById(R.id.review_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
//        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        //TODO: determine if these tags are even required
//        String[] recyclerCardTags = {"TEMPERATURE", "COLOUR", "CAPILLARY REFILL", "PULSE", "PICTURE"};
        if (patientArrayAdapter != null) {
            if(spinner.getAdapter().getCount() > 0) {
                mPatientId = ((Patient) spinner.getAdapter().getItem(0)).getPatientId();
                mAdapter = new ReviewRecycleAdapter(mPatientId, getActivity().getBaseContext());
                mRecyclerView.setAdapter(mAdapter);
            }
        }
        return view;
    }


}
