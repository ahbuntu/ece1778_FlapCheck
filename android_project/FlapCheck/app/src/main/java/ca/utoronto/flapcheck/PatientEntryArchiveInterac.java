package ca.utoronto.flapcheck;

import android.view.View;

import java.util.List;

/**
 * Created by ahmadul.hassan on 2015-02-23.
 */
public class PatientEntryArchiveInterac {
    public interface OnPatientListRetrieved {
        public void onPatientListRetrieved(List<Patient> patientList);
    }
    public interface OnPatientAdded {
        public void onPatientAdded(View callingView, Long rowId);
    }
    public interface OnArchiveItemSelected {
        public void onArchiveItemSelected(int position, int option);
    }

}
