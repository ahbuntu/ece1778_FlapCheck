package ca.utoronto.flapcheck;


import android.os.Bundle;
//import android.app.Fragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PatientEntryArchiveFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PatientEntryArchiveFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;



    List<Patient> patients;
    private AbsListView mListView;
    private PatientListAdapter mAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PatientEntryArchiveFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PatientEntryArchiveFragment newInstance(String param1, String param2) {
        PatientEntryArchiveFragment fragment = new PatientEntryArchiveFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public PatientEntryArchiveFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        //TODO: <time-permitting> maintain a list of persons that gets instantiated at the time of activity creation

        patients = new ArrayList<Patient>();
        Patient test = new Patient("Ahmad", "839238", (long)3324);
        patients.add(test);
        //TODO: get patient list from database
        mAdapter = new PatientListAdapter(getActivity(),R.layout.patient_list_item, patients);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragView = inflater.inflate(R.layout.fragment_patient_entry_archive, container, false);
        // bind adapter to listview
        mListView = (AbsListView) fragView.findViewById(R.id.listView_patients);
        mListView.setAdapter(mAdapter);
        return fragView;
    }


}
