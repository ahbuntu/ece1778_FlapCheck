package ca.utoronto.flapcheck;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
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
public class DBLoaderMeasurement {
    private static final String TAG = "DBLoaderMeasurement";

    protected SQLiteDatabase activeDB;
    private DBHelper dbHelper;
    private Context mContext;


    public DBLoaderMeasurement(Context context) {
        mContext = context;
        dbHelper = DBHelper.getHelper(mContext);
        openDB();
    }

    public void openDB() throws SQLException {
        if(dbHelper == null)
            dbHelper = DBHelper.getHelper(mContext);
        activeDB = dbHelper.getWritableDatabase();
    }

    public void closeDB() {
        dbHelper.close();
        activeDB = null;
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
            ContentValues values = new ContentValues();
            values.put(MeasurementEntry.COL_MEASUREMENT_PATIENT_ID, reading.getMeas_patientID());
            values.put(MeasurementEntry.COL_MEASUREMENT_TIMESTAMP, reading.getMeas_timestamp());
            values.put(MeasurementEntry.COL_MEASUREMENT_TEMP_CELS, reading.getMeas_temperature());
            values.put(MeasurementEntry.COL_MEASUREMENT_COLOUR_RGB, reading.getMeas_colour_rgb());
            values.put(MeasurementEntry.COL_MEASUREMENT_COLOUR_LAB, reading.getMeas_colour_lab());
            values.put(MeasurementEntry.COL_MEASUREMENT_COLOUR_HEX, reading.getMeas_colour_hex());

            // Inserting Row
            id = activeDB.insert(MeasurementEntry.TABLE_NAME, null, values);
            closeDB(); // Closing database connection

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
            Cursor cursor = activeDB.query(MeasurementEntry.TABLE_NAME,
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
            closeDB();

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
     * Deletes all records from the Measurement table
     */
    public void deleteAllReadings() {
        try {
            activeDB.delete(MeasurementEntry.TABLE_NAME, null, null);
            closeDB();
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
