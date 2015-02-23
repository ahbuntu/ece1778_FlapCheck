package ca.utoronto.flapcheck;


import android.os.AsyncTask;
import android.os.Bundle;
//import android.app.Fragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PatientEntryArchiveFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PatientEntryArchiveFragment extends Fragment
                                            implements PatientEntryDBLoader.OnPatientListRetrieved{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    List<Patient> patients;
    private AbsListView mListView;
    private ProgressBar mProgressBar;
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

        patients = new ArrayList<Patient>();
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

        mProgressBar = (ProgressBar) fragView.findViewById(R.id.progressBar_PE_archive);
        return fragView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            PatientOpenDBHelper db = new PatientOpenDBHelper(getActivity());
            GetPatientsFromDB getPatients = new GetPatientsFromDB(db, this);
            getPatients.execute(0);
        }
    }

    /**
     * doesn't display actual progress, but makes the user feel as though something is happening
     * @param percent
     */
    public void setProgressPercent(int percent) {
        mProgressBar.setProgress(percent);
    }
    /**
     * this method is invoked when the patient list has been retrieved from the db
     * @param patientList list of patients stored in the db
     */
    public void onPatientListRetrieved(List<Patient> patientList) {
        // need to take this approach, instead of the commented line since this modifies
        // the list instead of reassigning it.
        patients.clear();
        patients.addAll(patientList);
//        patients = patientList;
        mAdapter.notifyDataSetChanged();
    }

    private class GetPatientsFromDB extends AsyncTask<Integer, Integer, List<Patient>> {

        private PatientEntryDBLoader.OnPatientListRetrieved mCallback = null;
        private PatientOpenDBHelper patientsDB;

        public GetPatientsFromDB(PatientOpenDBHelper db, PatientEntryDBLoader.OnPatientListRetrieved ref) {
            patientsDB = db;
            mCallback = ref;
        }

        @Override
        protected List<Patient> doInBackground(Integer... vals) {
            publishProgress(50);
            List<Patient> patients = patientsDB.getAllPatients();
            publishProgress(100);
            return patients;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            //won't show real progress since PatientOpenDBHelper.getAllPatients doesn't expose it
            setProgressPercent(progress[0]);
        }

        @Override
        protected void onPostExecute(List<Patient> result) {
            super.onPostExecute(result);
            mCallback.onPatientListRetrieved(result);
        }
    }

}
