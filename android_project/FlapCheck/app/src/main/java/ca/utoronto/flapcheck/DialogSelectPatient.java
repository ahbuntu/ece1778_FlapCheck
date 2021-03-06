package ca.utoronto.flapcheck;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class DialogSelectPatient extends DialogFragment {



    interface DialogSelectPatientListener {
        void onAddNewPatient();
        void onDismissSelectPatient();
        void onSetActivePatientId(long patientId);
    }

    private DialogSelectPatientListener mListenerCallback;
    private Patient mActivePatient = null;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListenerCallback = (DialogSelectPatientListener) activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //We use a custom layout for the dialog, so we must inflate it manually
        LayoutInflater inflater = getActivity().getLayoutInflater();

        //The view representing the dialog
        View dialogView = inflater.inflate(R.layout.dialog_select_patient, null);

        //Get the data to populate the spinner
        DBLoaderPatient dbHelper = new DBLoaderPatient(getActivity().getApplicationContext());
        List<Patient> patientList = dbHelper.getAllPatients();
        ArrayAdapter<Patient> patientArrayAdapter = new ArrayAdapter<Patient>(getActivity(), android.R.layout.simple_spinner_dropdown_item, patientList);

        //Set up the spinner
        Spinner patientSpinner = (Spinner) dialogView.findViewById(R.id.select_patient_spinner);
        patientSpinner.setAdapter(patientArrayAdapter);

        //Record which patient is currently selected on the spinner
        patientSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mActivePatient = (Patient) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mActivePatient = null;
            }
        });

        builder.setView(dialogView);

        //Implement the buttons
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListenerCallback.onSetActivePatientId(mActivePatient.getPatientId());
            }
        });

        //Neutral button allows us to add patients
        builder.setNeutralButton(R.string.dialog_select_patient_new_patient, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                Intent intent = new Intent(getActivity(), PatientEntryActivity.class);
                //Note we must get the result passed to the activity, since this dialog fragment will
                //likely be gone by the time the result arrives
//                getActivity().startActivityForResult(intent, Constants.ADD_PATIENT_REQUEST);

                mListenerCallback.onAddNewPatient();
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListenerCallback.onDismissSelectPatient();
            }
        });

        return builder.create();
    }

}
