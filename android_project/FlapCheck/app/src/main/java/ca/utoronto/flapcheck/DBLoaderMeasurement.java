package ca.utoronto.flapcheck;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
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
            values.put(MeasurementEntry.COL_MEASUREMENT_POSITION, reading.getMeas_position());
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
     * @param id - the row ID of the measurement in the database
     * @return the patient object if found; null otherwise
     */
    public MeasurementReading getReading(long id) {
        MeasurementReading foundReading = null;
        try {
            Cursor cursor = activeDB.query(MeasurementEntry.TABLE_NAME,
                    new String[] {MeasurementEntry.COL_MEASUREMENT_ID,
                            MeasurementEntry.COL_MEASUREMENT_PATIENT_ID,
                            MeasurementEntry.COL_MEASUREMENT_TIMESTAMP,
                            MeasurementEntry.COL_MEASUREMENT_POSITION,
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
                        Integer.parseInt(cursor.getString(3)), //meas position
                        Float.parseFloat(cursor.getString(4)), //meas temp cels
                        (cursor.getString(5)), //meas colour rgb
                        (cursor.getString(6)), //meas colour lab
                        (cursor.getString(7))); //meas colour hex
            }
            closeDB();

        } catch (SQLiteException e) {
            Log.d(TAG, e.getMessage());
        }
        return foundReading;
    }

    /**
     * Gets the reading from the database using the patient id
     *
     * @param patientID - the row ID of the patient in the database
     * @return
     */
    public MeasurementReading findPatientReading(long patientID) {
        //TODO: <if-required> implement findReading by name, mrn, timeofop
        MeasurementReading foundReading = null;
        try {
            Cursor cursor = activeDB.query(MeasurementEntry.TABLE_NAME,
                    new String[] {MeasurementEntry.COL_MEASUREMENT_ID,
                            MeasurementEntry.COL_MEASUREMENT_PATIENT_ID,
                            MeasurementEntry.COL_MEASUREMENT_TIMESTAMP,
                            MeasurementEntry.COL_MEASUREMENT_POSITION,
                            MeasurementEntry.COL_MEASUREMENT_TEMP_CELS,
                            MeasurementEntry.COL_MEASUREMENT_COLOUR_RGB,
                            MeasurementEntry.COL_MEASUREMENT_COLOUR_LAB,
                            MeasurementEntry.COL_MEASUREMENT_COLOUR_HEX},
                    MeasurementEntry.COL_MEASUREMENT_PATIENT_ID + "=?",
                    new String[] { String.valueOf(patientID) }, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                //use constructor that supports id of the record in the db
                foundReading = new MeasurementReading(Long.parseLong(cursor.getString(0)), //meas ID
                        Long.parseLong(cursor.getString(1)), //meas patient ID
                        Long.parseLong(cursor.getString(2)), //meas timestamp
                        Integer.parseInt(cursor.getString(3)), //meas position
                        Float.parseFloat(cursor.getString(4)), //meas temp cels
                        (cursor.getString(5)), //meas colour rgb
                        (cursor.getString(6)), //meas colour lab
                        (cursor.getString(7))); //meas colour hex
            }
            closeDB();

        } catch (SQLiteException e) {
            Log.d(TAG, e.getMessage());
        }
        return foundReading;
    }


    /**
     * Gets all readings for the specified patient
     *
     * @param patientID
     * @return
     */
    public List<MeasurementReading> getTemperaturesForPatient(long patientID) {

        List<MeasurementReading> foundReadings = new ArrayList<MeasurementReading>();
        try {
            String where = MeasurementEntry.COL_MEASUREMENT_PATIENT_ID + "=? " +
                    "AND " + MeasurementEntry.COL_MEASUREMENT_TEMP_CELS + "  <> 0.0";
            String orderBy = MeasurementEntry.COL_MEASUREMENT_TIMESTAMP + " ASC";

            Cursor cursor = activeDB.query(MeasurementEntry.TABLE_NAME,
                    new String[] {MeasurementEntry.COL_MEASUREMENT_ID,
                            MeasurementEntry.COL_MEASUREMENT_PATIENT_ID,
                            MeasurementEntry.COL_MEASUREMENT_TIMESTAMP,
                            MeasurementEntry.COL_MEASUREMENT_POSITION,
                            MeasurementEntry.COL_MEASUREMENT_TEMP_CELS,
                            MeasurementEntry.COL_MEASUREMENT_COLOUR_RGB,
                            MeasurementEntry.COL_MEASUREMENT_COLOUR_LAB,
                            MeasurementEntry.COL_MEASUREMENT_COLOUR_HEX},
                    where,
                    new String[] { String.valueOf(patientID) },
                    null, null, orderBy, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    MeasurementReading mReading = new MeasurementReading();
                    mReading.setMeasurementID(Long.parseLong(cursor.getString(0)));
                    mReading.setMeas_patientID(Long.parseLong(cursor.getString(1)));
                    mReading.setMeas_timestamp(Long.parseLong(cursor.getString(2)));
                    mReading.setMeas_position(Integer.parseInt(cursor.getString(3)));
                    mReading.setMeas_temperature(Float.parseFloat(cursor.getString(4)));
                    mReading.setMeas_colour_rgb(cursor.getString(5));
                    mReading.setMeas_colour_lab(cursor.getString(6));
                    mReading.setMeas_colour_hex(cursor.getString(7));

                    foundReadings.add(mReading);
                } while (cursor.moveToNext());
            }
            closeDB();

        } catch (SQLiteException e) {
            Log.d(TAG, e.getMessage());
        }
        return foundReadings;
    }

    /**
     * Gets all readings for the specified patient for the provided measurement point
     * @param patientID
     * @param pointIndex
     * @return
     */
    public List<MeasurementReading> getTemperaturesForPatientAtIndex(long patientID, int pointIndex) {

        List<MeasurementReading> foundReadings = new ArrayList<MeasurementReading>();
        try {
            String where = MeasurementEntry.COL_MEASUREMENT_PATIENT_ID + "=? " +
                    "AND " + MeasurementEntry.COL_MEASUREMENT_TEMP_CELS + "  <> 0.0 " +
                    "AND " + MeasurementEntry.COL_MEASUREMENT_POSITION + "  =? ";
            String orderBy = MeasurementEntry.COL_MEASUREMENT_TIMESTAMP + " ASC";

            Cursor cursor = activeDB.query(MeasurementEntry.TABLE_NAME,
                    new String[] {MeasurementEntry.COL_MEASUREMENT_ID,
                            MeasurementEntry.COL_MEASUREMENT_PATIENT_ID,
                            MeasurementEntry.COL_MEASUREMENT_TIMESTAMP,
                            MeasurementEntry.COL_MEASUREMENT_POSITION,
                            MeasurementEntry.COL_MEASUREMENT_TEMP_CELS,
                            MeasurementEntry.COL_MEASUREMENT_COLOUR_RGB,
                            MeasurementEntry.COL_MEASUREMENT_COLOUR_LAB,
                            MeasurementEntry.COL_MEASUREMENT_COLOUR_HEX},
                    where,
                    new String[] { String.valueOf(patientID) , String.valueOf(pointIndex)},
                    null, null, orderBy, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    MeasurementReading mReading = new MeasurementReading();
                    mReading.setMeasurementID(Long.parseLong(cursor.getString(0)));
                    mReading.setMeas_patientID(Long.parseLong(cursor.getString(1)));
                    mReading.setMeas_timestamp(Long.parseLong(cursor.getString(2)));
                    mReading.setMeas_position(Integer.parseInt(cursor.getString(3)));
                    mReading.setMeas_temperature(Float.parseFloat(cursor.getString(4)));
                    mReading.setMeas_colour_rgb(cursor.getString(5));
                    mReading.setMeas_colour_lab(cursor.getString(6));
                    mReading.setMeas_colour_hex(cursor.getString(7));

                    foundReadings.add(mReading);
                } while (cursor.moveToNext());
            }
            closeDB();

        } catch (SQLiteException e) {
            Log.d(TAG, e.getMessage());
        }
        return foundReadings;
    }

    /**
     * Gets all colour readings for the specified patient
     *
     * @param patientID
     * @return
     */
    public List<MeasurementReading> getColoursForPatientAtIndex(long patientID, int pointIndex) {

        List<MeasurementReading> foundReadings = new ArrayList<MeasurementReading>();
        try {
            //assuming that colour RGB, LAB and hex behave as an atomic unit
            String where = MeasurementEntry.COL_MEASUREMENT_PATIENT_ID + "=? " +
                    "AND " + MeasurementEntry.COL_MEASUREMENT_COLOUR_RGB + "  <> \"\"" +
                    "AND " + MeasurementEntry.COL_MEASUREMENT_POSITION + "  =? ";
            String orderBy = MeasurementEntry.COL_MEASUREMENT_TIMESTAMP + " ASC";

            Cursor cursor = activeDB.query(MeasurementEntry.TABLE_NAME,
                    new String[] {MeasurementEntry.COL_MEASUREMENT_ID,
                            MeasurementEntry.COL_MEASUREMENT_PATIENT_ID,
                            MeasurementEntry.COL_MEASUREMENT_TIMESTAMP,
                            MeasurementEntry.COL_MEASUREMENT_POSITION,
                            MeasurementEntry.COL_MEASUREMENT_TEMP_CELS,
                            MeasurementEntry.COL_MEASUREMENT_COLOUR_RGB,
                            MeasurementEntry.COL_MEASUREMENT_COLOUR_LAB,
                            MeasurementEntry.COL_MEASUREMENT_COLOUR_HEX},
                    where, new String[] { String.valueOf(patientID), String.valueOf(pointIndex)},
                    null, null, orderBy, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    MeasurementReading mReading = new MeasurementReading();
                    mReading.setMeasurementID(Long.parseLong(cursor.getString(0)));
                    mReading.setMeas_patientID(Long.parseLong(cursor.getString(1)));
                    mReading.setMeas_timestamp(Long.parseLong(cursor.getString(2)));
                    mReading.setMeas_position(Integer.parseInt(cursor.getString(3)));
                    mReading.setMeas_temperature(Float.parseFloat(cursor.getString(4)));
                    mReading.setMeas_colour_rgb(cursor.getString(5));
                    mReading.setMeas_colour_lab(cursor.getString(6));
                    mReading.setMeas_colour_hex(cursor.getString(7));

                    foundReadings.add(mReading);
                } while (cursor.moveToNext());
            }
            closeDB();

        } catch (SQLiteException e) {
            Log.d(TAG, e.getMessage());
        }
        return foundReadings;
    }

    /**
     * Gets all colour readings for the specified patient
     *
     * @param patientID
     * @return
     */
    public List<MeasurementReading> getColoursForPatient(long patientID) {

        List<MeasurementReading> foundReadings = new ArrayList<MeasurementReading>();
        try {
            //assuming that colour RGB, LAB and hex behave as an atomic unit
            String where = MeasurementEntry.COL_MEASUREMENT_PATIENT_ID + "=? " +
                    "AND " + MeasurementEntry.COL_MEASUREMENT_COLOUR_RGB + "  <> \"\"";
            String orderBy = MeasurementEntry.COL_MEASUREMENT_TIMESTAMP + " ASC";

            Cursor cursor = activeDB.query(MeasurementEntry.TABLE_NAME,
                    new String[] {MeasurementEntry.COL_MEASUREMENT_ID,
                            MeasurementEntry.COL_MEASUREMENT_PATIENT_ID,
                            MeasurementEntry.COL_MEASUREMENT_TIMESTAMP,
                            MeasurementEntry.COL_MEASUREMENT_POSITION,
                            MeasurementEntry.COL_MEASUREMENT_TEMP_CELS,
                            MeasurementEntry.COL_MEASUREMENT_COLOUR_RGB,
                            MeasurementEntry.COL_MEASUREMENT_COLOUR_LAB,
                            MeasurementEntry.COL_MEASUREMENT_COLOUR_HEX},
                    where, new String[] { String.valueOf(patientID) },
                    null, null, orderBy, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    MeasurementReading mReading = new MeasurementReading();
                    mReading.setMeasurementID(Long.parseLong(cursor.getString(0)));
                    mReading.setMeas_patientID(Long.parseLong(cursor.getString(1)));
                    mReading.setMeas_timestamp(Long.parseLong(cursor.getString(2)));
                    mReading.setMeas_position(Integer.parseInt(cursor.getString(3)));
                    mReading.setMeas_temperature(Float.parseFloat(cursor.getString(4)));
                    mReading.setMeas_colour_rgb(cursor.getString(5));
                    mReading.setMeas_colour_lab(cursor.getString(6));
                    mReading.setMeas_colour_hex(cursor.getString(7));

                    foundReadings.add(mReading);
                } while (cursor.moveToNext());
            }
            closeDB();

        } catch (SQLiteException e) {
            Log.d(TAG, e.getMessage());
        }
        return foundReadings;
    }


    /**
     * gets all the temperature measurements associated with a single patient
     *
     * @return a List<MeasurementReading> collection of all temperature measurements for the patient
     */
    public List<MeasurementReading> getAllTempReadings() {
        List<MeasurementReading> tempReadingList;
        try {
            tempReadingList = new ArrayList<MeasurementReading>();
            // Select All Query
            String selectQuery = "SELECT  * FROM " + MeasurementEntry.TABLE_NAME;

            Cursor cursor = activeDB.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    MeasurementReading mReading = new MeasurementReading();
                    mReading.setMeasurementID(Long.parseLong(cursor.getString(0)));
                    mReading.setMeas_patientID(Long.parseLong(cursor.getString(1)));
                    mReading.setMeas_timestamp(Long.parseLong(cursor.getString(2)));
                    mReading.setMeas_position(Integer.parseInt(cursor.getString(3)));
                    mReading.setMeas_temperature(Float.parseFloat(cursor.getString(4)));
                    mReading.setMeas_colour_rgb(cursor.getString(5));
                    mReading.setMeas_colour_hex(cursor.getString(6));
                    mReading.setMeas_colour_lab(cursor.getString(7));

                    tempReadingList.add(mReading);
                } while (cursor.moveToNext());
            }

            closeDB();
        } catch (SQLiteException e) {
            Log.d(TAG, e.getMessage());
            tempReadingList = null;
        }
        return  tempReadingList;
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
