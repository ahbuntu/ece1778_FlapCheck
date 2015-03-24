package ca.utoronto.flapcheck;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ca.utoronto.flapcheck.DBPointToMeasureContract.PointToMeasureEntry;

/**
 * Created by ahmadul.hassan on 2015-02-19.
 */
public class DBLoaderPointToMeasure {
    private static final String TAG = "DBLoaderPointToMeasure";

    protected SQLiteDatabase activeDB;
    private DBHelper dbHelper;
    private Context mContext;


    public DBLoaderPointToMeasure(Context context) {
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
     * adds the pointtomeasure details to the database
     * @param point the pointtomeasure object whose information will be stored in the PointToMeasure details table
     * @return row ID of the newly inserted row, or -1 if an error occurred
     */
    public long addPointToMeasure(PointToMeasure point) {
        long id = -1;
        try {
            ContentValues values = new ContentValues();
            values.put(PointToMeasureEntry.COL_POINT_PATIENT_ID, point.getPatientId());
            values.put(PointToMeasureEntry.COL_POINT_INDEX, point.getPointIndex());
            values.put(PointToMeasureEntry.COL_POINT_X, point.getPointX());
            values.put(PointToMeasureEntry.COL_POINT_Y, point.getPointY());

            // Inserting Row
            id = activeDB.insert(PointToMeasureEntry.TABLE_NAME, null, values);
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
//    public Patient getPatient(long id) {
//        Patient foundPatient = null;
//        try {
//            Cursor cursor = activeDB.query(PatientEntry.TABLE_NAME,
//                    new String[] {PatientEntry.COL_PATIENT_ID,
//                            PatientEntry.COL_PATIENT_NAME,
//                            PatientEntry.COL_PATIENT_MRN,
//                            PatientEntry.COL_PATIENT_OPTIME,
//                            PatientEntry.COL_PATIENT_PHOTO_PATH,
//                            PatientEntry.COL_PATIENT_VIDEO_PATH},
//                    PatientEntry.COL_PATIENT_ID + "=?",
//                    new String[] { String.valueOf(id) }, null, null, null, null);
//
//            if (cursor != null) {
//                cursor.moveToFirst();
//                foundPatient = new Patient(Long.parseLong(cursor.getString(0)),
//                        cursor.getString(1),
//                        cursor.getString(2),
//                        Long.parseLong(cursor.getString(3)),
//                        cursor.getString(4),
//                        cursor.getString(5));
//            }
//            closeDB();
//
//        } catch (SQLiteException e) {
//            Log.d(TAG, e.getMessage());
//        }
//        return foundPatient;
//    }

    /**
     * gets all the points stored in the database for the provided patient
     *
     * @return a List<PointToMeasure> collection of all the points to measure for the patient
     */
    public List<PointToMeasure> getPointsToMeasureForPatient(long patientID) {

        List<PointToMeasure> foundPoints = new ArrayList<>();
        try {
            String where = PointToMeasureEntry.COL_POINT_PATIENT_ID + "=? ";
            String orderBy = PointToMeasureEntry.COL_POINT_INDEX + " ASC";

            Cursor cursor = activeDB.query(PointToMeasureEntry.TABLE_NAME,
                    new String[] {PointToMeasureEntry.COL_POINT_ID,
                            PointToMeasureEntry.COL_POINT_PATIENT_ID ,
                            PointToMeasureEntry.COL_POINT_INDEX ,
                            PointToMeasureEntry.COL_POINT_X ,
                            PointToMeasureEntry.COL_POINT_Y },
                    where,
                    new String[] { String.valueOf(patientID) },
                    null, null, orderBy, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    PointToMeasure mPoint = new PointToMeasure();
                    mPoint.setPointId(Long.parseLong(cursor.getString(0)));
                    mPoint.setPatientId(Long.parseLong(cursor.getString(1)));
                    mPoint.setPointIndex(Integer.parseInt(cursor.getString(2)));
                    mPoint.setPointX(Integer.parseInt(cursor.getString(3)));
                    mPoint.setPointY(Integer.parseInt(cursor.getString(4)));

                    foundPoints.add(mPoint);
                } while (cursor.moveToNext());
            }
            closeDB();

        } catch (SQLiteException e) {
            Log.d(TAG, e.getMessage());
        }
        return foundPoints;
    }

}
