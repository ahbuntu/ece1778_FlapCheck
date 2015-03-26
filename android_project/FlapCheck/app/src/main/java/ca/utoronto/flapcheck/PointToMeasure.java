package ca.utoronto.flapcheck;

import android.os.Environment;

import java.io.File;

/**
 * Created by ahmadul.hassan on 2015-02-19.
 */
public class PointToMeasure {
    public static long INVALID_ID = -1;

    private long pointId = INVALID_ID; //this will only be populated after being inserted into the db
    private long patientId;
    private int pointIndex;
    private float pointX;
    private float pointY;

    /**
     * create an empty patient object with none of the attributes defined
     */
    public PointToMeasure() {

    }

    public PointToMeasure(long patID, int index, float x, float y) {
        patientId = patID;
        pointIndex = index;
        pointX = x;
        pointY = y;
    }

    /**
     * ONLY use this constructor when instantiating from a database record
     * under this condition, id of the patient is known
     * @param id
     * @param patID
     * @param index
     * @param x
     * @param y
     */
    public PointToMeasure(long id, long patID, int index, float x, float y) {
        pointId = id;
        patientId = patID;
        pointIndex = index;
        pointX = x;
        pointY = y;;
    }

    public long getPointId() {
        return pointId;
    }

    public void setPointId(long pointId) {
        this.pointId = pointId;
    }

    public long getPatientId() {
        return patientId;
    }

    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }

    public int getPointIndex() {
        return pointIndex;
    }

    public void setPointIndex(int pointIndex) {
        this.pointIndex = pointIndex;
    }

    public float getPointX() {
        return pointX;
    }

    public void setPointX(float pointX) {
        this.pointX = pointX;
    }

    public float getPointY() {
        return pointY;
    }

    public void setPointY(float pointY) {
        this.pointY = pointY;
    }
}
