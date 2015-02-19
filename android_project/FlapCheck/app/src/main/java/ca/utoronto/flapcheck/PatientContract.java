package ca.utoronto.flapcheck;

import android.provider.BaseColumns;

/**
 * Created by ahmadul.hassan on 2015-02-19.
 */
public class PatientContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public PatientContract() {}

    /* Inner class that defines the table contents */
    public static abstract class PatientEntry implements BaseColumns {
        public static final String TABLE_NAME = "PatientDetails";
        public static final String COL_PATIENT_ID = "id";
        public static final String COL_PATIENT_NAME = "name";
        public static final String COL_PATIENT_MRN = "mrn";
        public static final String COL_PATIENT_OPTIME = "opDateTime";
    }
}
