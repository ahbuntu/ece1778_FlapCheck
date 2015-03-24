package ca.utoronto.flapcheck;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ahmadul.hassan on 2015-03-02.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static DBHelper instance;

    public static synchronized DBHelper getHelper(Context context) {
        if (instance == null)
            instance = new DBHelper(context);
        return instance;
    }

    private DBHelper(Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PATIENT_TABLE = "CREATE TABLE " + DBPatientContract.PatientEntry.TABLE_NAME + "("
                + DBPatientContract.PatientEntry.COL_PATIENT_ID + " INTEGER PRIMARY KEY,"
                + DBPatientContract.PatientEntry.COL_PATIENT_NAME + " TEXT,"
                + DBPatientContract.PatientEntry.COL_PATIENT_MRN + " TEXT,"
                + DBPatientContract.PatientEntry.COL_PATIENT_OPTIME + " INTEGER,"
                + DBPatientContract.PatientEntry.COL_PATIENT_PHOTO_PATH + " TEXT,"
                + DBPatientContract.PatientEntry.COL_PATIENT_VIDEO_PATH + " TEXT" + ")";
        db.execSQL(CREATE_PATIENT_TABLE );

        String CREATE_MEASUREMENTS_TABLE = "CREATE TABLE " + DBMeasurementContract.MeasurementEntry.TABLE_NAME + "("
                + DBMeasurementContract.MeasurementEntry.COL_MEASUREMENT_ID+ " INTEGER PRIMARY KEY,"
                + DBMeasurementContract.MeasurementEntry.COL_MEASUREMENT_PATIENT_ID+ " INTEGER,"
                + DBMeasurementContract.MeasurementEntry.COL_MEASUREMENT_TIMESTAMP + " INTEGER,"
                + DBMeasurementContract.MeasurementEntry.COL_MEASUREMENT_POSITION + " INTEGER,"
                + DBMeasurementContract.MeasurementEntry.COL_MEASUREMENT_TEMP_CELS + " REAL,"
                + DBMeasurementContract.MeasurementEntry.COL_MEASUREMENT_COLOUR_RGB + " TEXT,"
                + DBMeasurementContract.MeasurementEntry.COL_MEASUREMENT_COLOUR_LAB + " TEXT,"
                + DBMeasurementContract.MeasurementEntry.COL_MEASUREMENT_COLOUR_HEX + " TEXT" + ")";
        db.execSQL(CREATE_MEASUREMENTS_TABLE  );

        String CREATE_POINTTOMEASURE_TABLE = "CREATE TABLE " + DBPointToMeasureContract.PointToMeasureEntry.TABLE_NAME + "("
                + DBPointToMeasureContract.PointToMeasureEntry.COL_POINT_ID+ " INTEGER PRIMARY KEY,"
                + DBPointToMeasureContract.PointToMeasureEntry.COL_POINT_PATIENT_ID+ " INTEGER,"
                + DBPointToMeasureContract.PointToMeasureEntry.COL_POINT_INDEX + " INTEGER,"
                + DBPointToMeasureContract.PointToMeasureEntry.COL_POINT_X + " INTEGER,"
                + DBPointToMeasureContract.PointToMeasureEntry.COL_POINT_Y + " INTEGER" + ")";
        db.execSQL(CREATE_POINTTOMEASURE_TABLE  );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS " + DBPatientContract.PatientEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBMeasurementContract.MeasurementEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBPointToMeasureContract.PointToMeasureEntry.TABLE_NAME);
        // Create tables again
        onCreate(db);
    }
}
