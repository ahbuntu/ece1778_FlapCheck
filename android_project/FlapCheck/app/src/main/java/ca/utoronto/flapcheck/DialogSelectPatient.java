package ca.utoronto.flapcheck;


import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class DialogSelectPatient extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        PatientOpenDBHelper dbHelper = new PatientOpenDBHelper(getActivity().getApplicationContext());

        List<Patient> patientList = dbHelper.getAllPatients();
        PatientAdapter patientAdapter = new PatientAdapter(getActivity(), 0, patientList);

        builder.setTitle(R.string.select_patient)
               .setAdapter(patientAdapter, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       Toast.makeText(getActivity(), String.format("Selected Dialog element %d", which), Toast.LENGTH_SHORT).show();
                   }
               });
        return builder.create();
    }

    public class PatientAdapter extends ArrayAdapter<Patient> {
        public PatientAdapter(Context context, int resource, List<Patient> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if(convertView == null) {
                //Create
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                view = inflater.inflate(R.layout.row_select_patient_dialog, null);
            }

            Patient patient = getItem(position);
            if(patient != null) {
                //Update
                TextView patientText = (TextView) view.findViewById(R.id.patient_info);

                patientText.setText(String.format("%s (%s)", patient.getPatientName(), patient.getPatientMRN()));
            }

            return view;
        }

    }
}
