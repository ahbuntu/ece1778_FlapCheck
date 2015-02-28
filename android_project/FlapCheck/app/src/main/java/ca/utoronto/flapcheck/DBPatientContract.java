package ca.utoronto.flapcheck;

import android.provider.BaseColumns;

/**
 * Created by ahmadul.hassan on 2015-02-19.
 */
public class DBPatientContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public DBPatientContract() {}

    /* Inner class that defines the table contents */
    public static abstract class PatientEntry implements BaseColumns {
        public static final String TABLE_NAME = "PatientDetails";
        public static final String COL_PATIENT_ID = "id";
        public static final String COL_PATIENT_NAME = "name";
        public static final String COL_PATIENT_MRN = "mrn";
        public static final String COL_PATIENT_OPTIME = "opDateTime";
        public static final String COL_PATIENT_PHOTO_PATH = "photoPath";
        public static final String COL_PATIENT_VIDEO_PATH = "videoPath";
    }
}
