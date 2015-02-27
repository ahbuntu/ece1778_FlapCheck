package ca.utoronto.flapcheck;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.app.Fragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PatientEntryArchiveFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PatientEntryArchiveFragment extends Fragment
                                implements PatientEntryArchiveInterac.OnPatientListRetrieved,
                                            AbsListView.OnItemClickListener{

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
        mListView.setOnItemClickListener(this);
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

        private PatientEntryArchiveInterac.OnPatientListRetrieved mCallback = null;
        private PatientOpenDBHelper patientsDB;

        public GetPatientsFromDB(PatientOpenDBHelper db, PatientEntryArchiveInterac.OnPatientListRetrieved ref) {
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SelectItemDialogFragment selectDlgFrag = new SelectItemDialogFragment();
        Bundle selectDlgBundle = new Bundle();
        selectDlgBundle.putInt("selectedPosition", position);
        selectDlgFrag.setArguments(selectDlgBundle);
        selectDlgFrag.show(getActivity().getFragmentManager(), "Select");
    }

    /**
     * A simple {@link android.app.Fragment} subclass.
     */
    public static class SelectItemDialogFragment extends DialogFragment {

        private static final String ARG_POSITION = "selectedPosition";
        PatientEntryArchiveInterac.OnArchiveItemSelected mCallback = null;
        int selectedPos = 0;

        public SelectItemDialogFragment() {
            // Required empty public constructor
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            if (getArguments() != null) {
                selectedPos = getArguments().getInt(ARG_POSITION);
            }
            CharSequence options[] = new CharSequence[] {
                    getActivity().getString(R.string.dialog_select_option1),
                    getActivity().getString(R.string.dialog_select_option2)};

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.dialog_select_instr);
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int opt) {
                    mCallback.onArchiveItemSelected(selectedPos, opt);
                }
            });
            return builder.create();
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            try {
                mCallback = (PatientEntryArchiveInterac.OnArchiveItemSelected) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement OnArchiveItemSelected");
            }
        }

        @Override
        public void onDetach() {
            super.onDetach();
            mCallback = null;
        }

    }

}
