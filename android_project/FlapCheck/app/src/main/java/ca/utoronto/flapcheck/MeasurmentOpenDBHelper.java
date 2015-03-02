package ca.utoronto.flapcheck;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ca.utoronto.flapcheck.DBMeasurementContract.MeasurementEntry;

/**
 * Created by ahmadul.hassan on 2015-02-19.
 */
public class MeasurmentOpenDBHelper extends SQLiteOpenHelper {
    private static final String TAG = "MeasurementOpenDBHelper";

    public MeasurmentOpenDBHelper(Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PATIENT_TABLE = "CREATE TABLE " + MeasurementEntry.TABLE_NAME + "("
                + MeasurementEntry.COL_MEASUREMENT_ID+ " INTEGER PRIMARY KEY,"
                + MeasurementEntry.COL_MEASUREMENT_PATIENT_ID+ " INTEGER,"
                + MeasurementEntry.COL_MEASUREMENT_TIMESTAMP + " INTEGER,"
                + MeasurementEntry.COL_MEASUREMENT_TEMP_CELS + " REAL,"
                + MeasurementEntry.COL_MEASUREMENT_COLOUR_RGB + " TEXT,"
                + MeasurementEntry.COL_MEASUREMENT_COLOUR_LAB + " TEXT,"
                + MeasurementEntry.COL_MEASUREMENT_COLOUR_HEX + " TEXT" + ")";
        db.execSQL(CREATE_PATIENT_TABLE );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + MeasurementEntry.TABLE_NAME);
        // Create tables again
        onCreate(db);
    }

    //*********** All CRUD(Create, Read, Update, Delete) Operations ************

    /**
     * adds the measurement details to the database
     *
     * @param reading the measurement reading that will be stored in the Measurement details table
     * @return row ID of the newly inserted row, or -1 if an error occurred
     */
    public long addReading(MeasurementReading reading) {
        long id = -1;
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(MeasurementEntry.COL_MEASUREMENT_PATIENT_ID, reading.getMeas_patientID());
            values.put(MeasurementEntry.COL_MEASUREMENT_TIMESTAMP, reading.getMeas_timestamp());
            values.put(MeasurementEntry.COL_MEASUREMENT_TEMP_CELS, reading.getMeas_temperature());
            values.put(MeasurementEntry.COL_MEASUREMENT_COLOUR_RGB, reading.getMeas_colour_rgb());
            values.put(MeasurementEntry.COL_MEASUREMENT_COLOUR_LAB, reading.getMeas_colour_lab());
            values.put(MeasurementEntry.COL_MEASUREMENT_COLOUR_HEX, reading.getMeas_colour_hex());

            // Inserting Row
            id = db.insert(MeasurementEntry.TABLE_NAME, null, values);
            db.close(); // Closing database connection

        } catch (SQLiteException e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }
        return id;
    }

    /**
     * Gets the reading from the database using the id of the measurement entry
     *
     * @param id - the row ID of the patient in the database
     * @return the patient object if found; null otherwise
     */
    public MeasurementReading getReading(int id) {
        MeasurementReading foundReading = null;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.query(MeasurementEntry.TABLE_NAME,
                    new String[] {MeasurementEntry.COL_MEASUREMENT_ID,
                            MeasurementEntry.COL_MEASUREMENT_PATIENT_ID,
                            MeasurementEntry.COL_MEASUREMENT_TIMESTAMP,
                            MeasurementEntry.COL_MEASUREMENT_TEMP_CELS,
                            MeasurementEntry.COL_MEASUREMENT_COLOUR_RGB,
                            MeasurementEntry.COL_MEASUREMENT_COLOUR_LAB,
                            MeasurementEntry.COL_MEASUREMENT_COLOUR_HEX},
                    MeasurementEntry.COL_MEASUREMENT_ID + "=?",
                    new String[] { String.valueOf(id) }, null, null, null, null);

            if (cursor != null) {
                cursor.moveToFirst();
                //use constructor that supports id of the record in the db
                foundReading = new MeasurementReading(Long.parseLong(cursor.getString(0)), //meas ID
                        Long.parseLong(cursor.getString(1)), //meas patient ID
                        Long.parseLong(cursor.getString(2)), //meas timestamp
                        Float.parseFloat(cursor.getString(3)), //meas temp cels
                        (cursor.getString(4)), //meas colour rgb
                        (cursor.getString(5)), //meas colour lab
                        (cursor.getString(6))); //meas colour hex
            }
            db.close();

        } catch (SQLiteException e) {
            Log.d(TAG, e.getMessage());
        }
        return foundReading;
    }

    public MeasurementReading findReading(String name, String mrn) {
        //TODO: <if-required> implement findReading by name, mrn, timeofop
        return null;
    }

    /**
     * ONLY for DEBUG purposes.
     * Deletes all patients from the Patient table
     */
    public void deleteAllReadings() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(MeasurementEntry.TABLE_NAME, null, null);
            db.close();
        } catch (SQLiteException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    //********** Implement the following as and when required ******************8
    // Getting patients Count
//    public int getPatientsCount() {}

    // Updating single patient information
//    public int updatePatient(Patient contact) {}

    // Deleting single patient
//    public void deletePatient(Patient contact) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete(TABLE_patient, KEY_ID + " = ?",
//                new String[] { String.valueOf(contact.getID()) });
//        db.close();}
}
