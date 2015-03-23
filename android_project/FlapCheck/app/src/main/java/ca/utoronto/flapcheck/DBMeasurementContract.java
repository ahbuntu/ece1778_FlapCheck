package ca.utoronto.flapcheck;

import android.provider.BaseColumns;

/**
 * Created by ahmadul.hassan on 2015-02-19.
 */
public class DBMeasurementContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public DBMeasurementContract() {}

    /* Inner class that defines the table contents */
    public static abstract class MeasurementEntry implements BaseColumns {
        public static final String TABLE_NAME = "MeasurementDetails";
        public static final String COL_MEASUREMENT_ID = "id";
        public static final String COL_MEASUREMENT_PATIENT_ID = "pId";
        public static final String COL_MEASUREMENT_TIMESTAMP = "timestamp";
        public static final String COL_MEASUREMENT_POSITION = "position";
        public static final String COL_MEASUREMENT_TEMP_CELS = "temperatureCels";
        public static final String COL_MEASUREMENT_COLOUR_RGB = "colourRGB";
        public static final String COL_MEASUREMENT_COLOUR_LAB = "colourLAB";
        public static final String COL_MEASUREMENT_COLOUR_HEX = "colourHEX";
    }
}
