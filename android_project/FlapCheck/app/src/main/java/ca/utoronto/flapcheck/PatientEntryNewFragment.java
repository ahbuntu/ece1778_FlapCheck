package ca.utoronto.flapcheck;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PatientEntryNewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PatientEntryNewFragment extends Fragment {

    Button button_addPatient;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PatientEntryNewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PatientEntryNewFragment  newInstance(String param1, String param2) {
        PatientEntryNewFragment fragment = new PatientEntryNewFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public PatientEntryNewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =inflater.inflate(R.layout.fragment_patient_entry_new, container, false);
        initWidgets(rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        button_addPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: add the patient to the database/list
                //TODO: display the archive fragment with sliding transition
            }
        });
    }

    /**
     * initializes the widgets on the activity/fragment
     */
    private void initWidgets(View view) {
        button_addPatient = (Button)  view.findViewById(R.id.button_addPatient);
    }
}
