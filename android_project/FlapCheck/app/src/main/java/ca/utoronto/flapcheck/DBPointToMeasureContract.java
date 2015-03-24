package ca.utoronto.flapcheck;

import android.provider.BaseColumns;

/**
 * Created by ahmadul.hassan on 2015-02-19.
 */
public class DBPointToMeasureContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public DBPointToMeasureContract() {}

    /* Inner class that defines the table contents */
    public static abstract class PointToMeasureEntry implements BaseColumns {
        public static final String TABLE_NAME = "PointToMeasureDetails";
        public static final String COL_POINT_ID = "id";
        public static final String COL_POINT_PATIENT_ID = "patientId";
        public static final String COL_POINT_INDEX = "pointIndex";
        public static final String COL_POINT_X = "xPosition";
        public static final String COL_POINT_Y = "yPosition";
    }
}
