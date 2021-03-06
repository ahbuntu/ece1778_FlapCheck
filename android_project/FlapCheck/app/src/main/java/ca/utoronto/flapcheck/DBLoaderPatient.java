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

import ca.utoronto.flapcheck.DBPatientContract.PatientEntry;

/**
 * Created by ahmadul.hassan on 2015-02-19.
 */
public class DBLoaderPatient {
    private static final String TAG = "DBLoaderPatient";

    protected SQLiteDatabase activeDB;
    private DBHelper dbHelper;
    private Context mContext;


    public DBLoaderPatient(Context context) {
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
     * adds the patient details to the database, if the patient does not already exist
     * patient uniqueness is assumed and not strictly checked or enforced
     *
     * @param patient the patient object whose information will be stored in the Patient details table
     * @return row ID of the newly inserted row, or -1 if an error occurred
     */
    public long addPatient(Patient patient) {
        long id = -1;
        try {
            //TODO: <time-permitting> enforce uniquenes for same name + mrn + opdatetime

            ContentValues values = new ContentValues();
            values.put(PatientEntry.COL_PATIENT_NAME, patient.getPatientName());
            values.put(PatientEntry.COL_PATIENT_MRN, patient.getPatientMRN());
            values.put(PatientEntry.COL_PATIENT_OPTIME, patient.getPatientOpDateTime());
            values.put(PatientEntry.COL_PATIENT_PHOTO_PATH, patient.getPatientPhotoPath());
            values.put(PatientEntry.COL_PATIENT_VIDEO_PATH, patient.getPatientVidPath());

            // Inserting Row
            id = activeDB.insert(PatientEntry.TABLE_NAME, null, values);
            closeDB(); // Closing database connection

        } catch (SQLiteException e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }
        return id;
    }

    /**
     * Gets the patient from the database using the patientID
     *
     * @param id - the row ID of the patient in the database
     * @return the patient object if found; null otherwise
     */
    public Patient getPatient(long id) {
        Patient foundPatient = null;
        try {
            Cursor cursor = activeDB.query(PatientEntry.TABLE_NAME,
                    new String[] {PatientEntry.COL_PATIENT_ID,
                            PatientEntry.COL_PATIENT_NAME,
                            PatientEntry.COL_PATIENT_MRN,
                            PatientEntry.COL_PATIENT_OPTIME,
                            PatientEntry.COL_PATIENT_PHOTO_PATH,
                            PatientEntry.COL_PATIENT_VIDEO_PATH},
                    PatientEntry.COL_PATIENT_ID + "=?",
                    new String[] { String.valueOf(id) }, null, null, null, null);

            if (cursor != null) {
                cursor.moveToFirst();
                foundPatient = new Patient(Long.parseLong(cursor.getString(0)),
                        cursor.getString(1),
                        cursor.getString(2),
                        Long.parseLong(cursor.getString(3)),
                        cursor.getString(4),
                        cursor.getString(5));
            }
            closeDB();

        } catch (SQLiteException e) {
            Log.d(TAG, e.getMessage());
        }
        return foundPatient;
    }

    public Patient findPatient(String name, String mrn) {
        //TODO: <if-required> implement findPatient by name and mrn
        return null;
    }


    /**
     * gets all the patients stored in the database
     *
     * @return a List<Patient> collection of all the patients in the database
     */
    public List<Patient> getAllPatients() {
        List<Patient> patientList;
        try {
            patientList = new ArrayList<Patient>();
            // Select All Query
            String selectQuery = "SELECT  * FROM " + PatientEntry.TABLE_NAME;

            Cursor cursor = activeDB.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    Patient mPatient = new Patient();
                    mPatient.setPatientId(Long.parseLong(cursor.getString(0)));
                    mPatient.setPatientName(cursor.getString(1));
                    mPatient.setPatientMRN(cursor.getString(2));
                    mPatient.setPatientOpDateTime(Long.parseLong(cursor.getString(3)));

                    patientList.add(mPatient);
                } while (cursor.moveToNext());
            }

            closeDB();
        } catch (SQLiteException e) {
            Log.d(TAG, e.getMessage());
            patientList = null;
        }
        return  patientList;
    }

    /**
     * ONLY for DEBUG purposes.
     * Deletes all patients from the Patient table
     */
    public void deleteAllPatients() {
        try {
            activeDB.delete(PatientEntry.TABLE_NAME, null, null);
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
