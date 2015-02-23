package ca.utoronto.flapcheck;

import android.view.View;

import java.util.List;

/**
 * Created by ahmadul.hassan on 2015-02-23.
 */
public class PatientEntryDBLoader {
    public interface OnPatientListRetrieved {
        public void onPatientListRetrieved(List<Patient> patientList);
    }
    public interface OnPatientAdded {
        public void onPatientAdded(View callingView, Long rowId);
    }
}
